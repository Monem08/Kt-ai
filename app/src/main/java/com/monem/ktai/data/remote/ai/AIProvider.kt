package com.monem.ktai.data.remote.ai

import com.monem.ktai.domain.model.ChatMessage
import com.monem.ktai.domain.model.FileEdit

data class AIRequest(
    val prompt: String,
    val conversationHistory: List<ChatMessage> = emptyList(),
    val fileContexts: List<FileContext> = emptyList(),
    val systemPrompt: String = DEFAULT_SYSTEM_PROMPT,
)

data class FileContext(
    val filePath: String,
    val content: String,
)

data class AIResponse(
    val message: String,
    val fileEdits: List<FileEdit> = emptyList(),
    val isError: Boolean = false,
)

interface AIProvider {
    val name: String
    val requiresApiKey: Boolean
    suspend fun sendMessage(request: AIRequest): Result<AIResponse>
    suspend fun isAvailable(): Boolean
}

private const val DEFAULT_SYSTEM_PROMPT = """You are Kt AI, an expert Android/Kotlin coding assistant. You help developers write, edit, fix, and refactor Kotlin code for Android apps.

When the user asks you to create or edit files, respond with clear file edit instructions in this format:
- Specify the file path
- Show the complete new content or the specific changes

Always explain your changes clearly. Write clean, idiomatic Kotlin code following Android best practices.
When generating Jetpack Compose UI, use Material 3 components and follow modern Android architecture (MVVM, StateFlow, Hilt).

Important:
- Never modify sensitive files (.env, keystore, google-services.json, local.properties) without explicit warning.
- Always show the full diff of changes before applying.
- Write production-quality code with proper error handling."""
