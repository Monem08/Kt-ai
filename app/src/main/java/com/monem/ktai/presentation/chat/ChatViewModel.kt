package com.monem.ktai.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monem.ktai.data.remote.ai.AIProvider
import com.monem.ktai.data.remote.ai.AIRequest
import com.monem.ktai.domain.model.ChatMessage
import com.monem.ktai.domain.model.FileEdit
import com.monem.ktai.domain.model.MessageRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val pendingRequests: Int = 0,
    val pendingFileEdits: List<FileEdit> = emptyList(),
    val error: String? = null,
) {
    val isLoading: Boolean get() = pendingRequests > 0
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiProvider: AIProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = content,
        )

        val previousMessages = _uiState.value.messages

        _uiState.value = _uiState.value.copy(
            messages = previousMessages + userMessage,
            pendingRequests = _uiState.value.pendingRequests + 1,
            error = null,
        )

        viewModelScope.launch {
            val request = AIRequest(
                prompt = content,
                conversationHistory = previousMessages,
            )

            aiProvider.sendMessage(request)
                .onSuccess { response ->
                    val assistantMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = MessageRole.ASSISTANT,
                        content = response.message,
                        fileEdits = response.fileEdits.ifEmpty { null },
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + assistantMessage,
                        pendingRequests = _uiState.value.pendingRequests - 1,
                        pendingFileEdits = response.fileEdits,
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        pendingRequests = _uiState.value.pendingRequests - 1,
                        error = error.message ?: "Failed to get response",
                    )
                }
        }
    }

    fun clearPendingEdits() {
        _uiState.value = _uiState.value.copy(pendingFileEdits = emptyList())
    }
}
