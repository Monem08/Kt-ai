package com.monem.ktai.presentation.workspace

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monem.ktai.data.local.saf.SAFHelper
import com.monem.ktai.domain.model.Workspace
import com.monem.ktai.domain.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class WorkspaceListState(
    val workspaces: List<Workspace> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepository,
    private val safHelper: SAFHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkspaceListState())
    val uiState: StateFlow<WorkspaceListState> = _uiState.asStateFlow()

    // Hardcoded userId for MVP (will come from auth in production)
    private val userId = "local-user"

    init {
        loadWorkspaces()
    }

    private fun loadWorkspaces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            workspaceRepository.getWorkspaces(userId).collect { workspaces ->
                _uiState.value = _uiState.value.copy(
                    workspaces = workspaces,
                    isLoading = false,
                )
            }
        }
    }

    private val _folderSaved = MutableStateFlow(false)
    val folderSaved: StateFlow<Boolean> = _folderSaved.asStateFlow()

    fun onFolderSelected(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            safHelper.persistPermission(uri)

            val folderName = safHelper.getFolderName(uri)
            val workspace = Workspace(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = folderName,
                folderUri = uri.toString(),
            )

            workspaceRepository.createWorkspace(workspace)
            _folderSaved.value = true
        }
    }

    fun resetFolderSaved() {
        _folderSaved.value = false
    }

    fun deleteWorkspace(workspace: Workspace) {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = Uri.parse(workspace.folderUri)
            safHelper.releasePermission(uri)
            workspaceRepository.deleteWorkspace(workspace.id)
        }
    }

    fun openWorkspace(workspace: Workspace) {
        viewModelScope.launch {
            workspaceRepository.updateLastOpened(workspace.id)
        }
    }
}
