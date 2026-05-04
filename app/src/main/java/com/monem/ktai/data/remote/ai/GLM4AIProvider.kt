package com.monem.ktai.data.remote.ai

import com.monem.ktai.domain.model.MessageRole
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GLM4AIProvider(
    private val apiKey: String,
    private val httpClient: HttpClient,
) : AIProvider {

    override val name = "GLM-4 (NVIDIA)"
    override val requiresApiKey = true

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun sendMessage(request: AIRequest): Result<AIResponse> {
        return try {
            val messages = buildMessages(request)

            val chatRequest = ChatCompletionRequest(
                model = MODEL_NAME,
                messages = messages,
                temperature = 0.6,
                topP = 0.95,
                maxTokens = 4096,
                stream = false,
            )

            val response = httpClient.post(API_URL) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(chatRequest)
            }

            val chatResponse: ChatCompletionResponse = response.body()
            val content = chatResponse.choices.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("Empty response from AI"))

            Result.success(AIResponse(message = content))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            val errorMessage = when {
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true ->
                    "Invalid API key. Please check your GLM API key in settings."
                e.message?.contains("429") == true || e.message?.contains("Too Many") == true ->
                    "Rate limit exceeded. Please wait a moment and try again."
                e.message?.contains("timeout") == true || e.message?.contains("Timeout") == true ->
                    "Request timed out. Please check your internet connection."
                e.message?.contains("Unable to resolve host") == true ||
                    e.message?.contains("No address associated") == true ->
                    "No internet connection. Please check your network."
                else -> "AI request failed: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun isAvailable(): Boolean {
        return apiKey.isNotBlank()
    }

    private fun buildMessages(request: AIRequest): List<ChatMessageDto> {
        val messages = mutableListOf<ChatMessageDto>()

        messages.add(ChatMessageDto(role = "system", content = request.systemPrompt))

        if (request.fileContexts.isNotEmpty()) {
            val contextText = buildString {
                appendLine("The user has selected the following files from their project:")
                request.fileContexts.forEach { ctx ->
                    appendLine("\n--- ${ctx.filePath} ---")
                    appendLine(ctx.content)
                }
            }
            messages.add(ChatMessageDto(role = "system", content = contextText))
        }

        request.conversationHistory.forEach { msg ->
            val role = when (msg.role) {
                MessageRole.USER -> "user"
                MessageRole.ASSISTANT -> "assistant"
                MessageRole.SYSTEM -> "system"
            }
            messages.add(ChatMessageDto(role = role, content = msg.content))
        }

        messages.add(ChatMessageDto(role = "user", content = request.prompt))

        return messages
    }

    companion object {
        private const val API_URL = "https://integrate.api.nvidia.com/v1/chat/completions"
        private const val MODEL_NAME = "z-ai/glm4.7"
    }
}

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Double = 0.6,
    @SerialName("top_p") val topP: Double = 0.95,
    @SerialName("max_tokens") val maxTokens: Int = 4096,
    val stream: Boolean = false,
)

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String,
    @SerialName("reasoning_content") val reasoningContent: String? = null,
)

@Serializable
data class ChatCompletionResponse(
    val id: String = "",
    val choices: List<ChatChoice> = emptyList(),
    val usage: UsageInfo? = null,
)

@Serializable
data class ChatChoice(
    val index: Int = 0,
    val message: ChatMessageDto = ChatMessageDto("", ""),
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class UsageInfo(
    @SerialName("prompt_tokens") val promptTokens: Int = 0,
    @SerialName("completion_tokens") val completionTokens: Int = 0,
    @SerialName("total_tokens") val totalTokens: Int = 0,
)
