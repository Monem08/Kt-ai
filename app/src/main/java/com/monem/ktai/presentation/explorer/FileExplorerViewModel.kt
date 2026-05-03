package com.monem.ktai.presentation.explorer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monem.ktai.data.local.saf.SAFHelper
import com.monem.ktai.domain.model.FileNode
import com.monem.ktai.domain.model.Workspace
import com.monem.ktai.domain.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FileExplorerState(
    val files: List<FileNode> = emptyList(),
    val currentPath: String = "",
    val pathSegments: List<String> = emptyList(),
    val workspaceName: String = "",
    val folderUri: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class FileExplorerViewModel @Inject constructor(
    private val safHelper: SAFHelper,
    private val workspaceRepository: WorkspaceRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val workspaceId: String = savedStateHandle["workspaceId"] ?: ""

    private val _uiState = MutableStateFlow(FileExplorerState())
    val uiState: StateFlow<FileExplorerState> = _uiState.asStateFlow()

    init {
        loadWorkspaceFiles()
    }

    private fun loadWorkspaceFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val workspaces = workspaceRepository.getWorkspaces("local-user").firstOrNull()
            val workspace = workspaces?.find { it.id == workspaceId }

            if (workspace == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Workspace not found",
                )
                return@launch
            }

            val uri = Uri.parse(workspace.folderUri)
            if (!safHelper.hasPermission(uri)) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Permission lost. Please re-select the folder.",
                )
                return@launch
            }

            val files = safHelper.getChildrenOf(uri, "")
            _uiState.value = _uiState.value.copy(
                files = files,
                workspaceName = workspace.name,
                folderUri = workspace.folderUri,
                isLoading = false,
                currentPath = "",
                pathSegments = emptyList(),
            )
        }
    }

    fun navigateToFolder(folderNode: FileNode) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val folderUri = Uri.parse(_uiState.value.folderUri)
            val newPath = folderNode.path
            val files = safHelper.getChildrenOf(folderUri, newPath)

            _uiState.value = _uiState.value.copy(
                files = files,
                currentPath = newPath,
                pathSegments = newPath.split("/"),
                isLoading = false,
            )
        }
    }

    fun navigateToRoot() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val folderUri = Uri.parse(_uiState.value.folderUri)
            val files = safHelper.getChildrenOf(folderUri, "")

            _uiState.value = _uiState.value.copy(
                files = files,
                currentPath = "",
                pathSegments = emptyList(),
                isLoading = false,
            )
        }
    }

    fun navigateUp(): Boolean {
        val currentPath = _uiState.value.currentPath
        if (currentPath.isEmpty()) return false

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val parentPath = currentPath.substringBeforeLast("/", "")
            val folderUri = Uri.parse(_uiState.value.folderUri)
            val files = safHelper.getChildrenOf(folderUri, parentPath)

            _uiState.value = _uiState.value.copy(
                files = files,
                currentPath = parentPath,
                pathSegments = if (parentPath.isEmpty()) emptyList() else parentPath.split("/"),
                isLoading = false,
            )
        }
        return true
    }

    fun navigateToBreadcrumb(index: Int) {
        val segments = _uiState.value.pathSegments
        if (index >= segments.size) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val newPath = segments.take(index + 1).joinToString("/")
            val folderUri = Uri.parse(_uiState.value.folderUri)
            val files = safHelper.getChildrenOf(folderUri, newPath)

            _uiState.value = _uiState.value.copy(
                files = files,
                currentPath = newPath,
                pathSegments = newPath.split("/"),
                isLoading = false,
            )
        }
    }

    fun readFileContent(fileNode: FileNode): Result<String> {
        if (fileNode.uri.isEmpty()) {
            val folderUri = Uri.parse(_uiState.value.folderUri)
            return safHelper.readFileContent(folderUri, fileNode.path)
        }
        return safHelper.readFileContent(Uri.parse(fileNode.uri))
    }
}
