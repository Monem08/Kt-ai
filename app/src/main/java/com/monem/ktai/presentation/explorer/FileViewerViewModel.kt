package com.monem.ktai.presentation.explorer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monem.ktai.data.local.saf.SAFHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FileViewerState(
    val fileName: String = "",
    val filePath: String = "",
    val content: String = "",
    val isSensitive: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class FileViewerViewModel @Inject constructor(
    private val safHelper: SAFHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val fileUri: String = savedStateHandle["fileUri"] ?: ""
    private val fileName: String = savedStateHandle["fileName"] ?: ""
    private val filePath: String = savedStateHandle["filePath"] ?: ""

    private val _uiState = MutableStateFlow(FileViewerState())
    val uiState: StateFlow<FileViewerState> = _uiState.asStateFlow()

    init {
        loadFileContent()
    }

    private fun loadFileContent() {
        viewModelScope.launch(Dispatchers.IO) {
            val displayName = fileName.ifEmpty { filePath.substringAfterLast("/") }
            val isSensitive = safHelper.isSensitiveFile(displayName)

            _uiState.value = FileViewerState(
                fileName = displayName,
                filePath = filePath,
                isSensitive = isSensitive,
                isLoading = true,
            )

            if (fileUri.isNotEmpty()) {
                val uri = Uri.parse(fileUri)
                safHelper.readFileContent(uri)
                    .onSuccess { content ->
                        _uiState.value = _uiState.value.copy(
                            content = content,
                            isLoading = false,
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to read file",
                            isLoading = false,
                        )
                    }
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "No file URI provided",
                    isLoading = false,
                )
            }
        }
    }
}
