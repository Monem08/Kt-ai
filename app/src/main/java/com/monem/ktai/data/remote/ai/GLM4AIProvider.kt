package com.monem.ktai.data.remote.ai

import android.util.Log
import com.monem.ktai.domain.model.MessageRole
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.readUTF8Line
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
            Log.d(TAG, "Sending streaming message to GLM-4...")
            val messages = buildMessages(request)

            val chatRequest = ChatCompletionRequest(
                model = MODEL_NAME,
                messages = messages,
                temperature = 0.6,
                topP = 0.95,
                maxTokens = 8192,
                stream = true,
            )

            val response = httpClient.post(API_URL) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(chatRequest)
            }

            Log.d(TAG, "Response status: ${response.status.value}")

            if (!response.status.isSuccess()) {
                val statusCode = response.status.value
                Log.e(TAG, "API error $statusCode")
                val errorMessage = when (statusCode) {
                    401 -> "Invalid API key. Please check your GLM API key in settings."
                    429 -> "Rate limit exceeded. Please wait a moment and try again."
                    in 500..599 -> "AI server error ($statusCode). Please try again later."
                    else -> "AI request failed with status $statusCode"
                }
                return Result.failure(Exception(errorMessage))
            }

            val channel = response.bodyAsChannel()
            val contentBuilder = StringBuilder()
            var chunkCount = 0

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break

                if (!line.startsWith("data: ")) continue
                val data = line.removePrefix("data: ").trim()
                if (data == "[DONE]") break

                try {
                    val chunk = json.decodeFromString<StreamChunkResponse>(data)
                    val delta = chunk.choices.firstOrNull()?.delta ?: continue

                    val content = delta.content
                    if (content != null) {
                        contentBuilder.append(content)
                        chunkCount++
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse chunk: ${e.message}")
                }
            }

            Log.d(TAG, "Stream complete: $chunkCount content chunks, ${contentBuilder.length} chars")

            val finalContent = contentBuilder.toString()
            if (finalContent.isBlank()) {
                return Result.failure(Exception("Empty response from AI. Please try again."))
            }

            Result.success(AIResponse(message = finalContent))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Log.e(TAG, "Exception in sendMessage", e)
            val errorMessage = when {
                e.message?.contains("timeout") == true || e.message?.contains("Timeout") == true ->
                    "Request timed out. Please try again."
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

    private fun buildMessages(request: AIRequest): List<RequestMessageDto> {
        val messages = mutableListOf<RequestMessageDto>()

        messages.add(RequestMessageDto(role = "system", content = request.systemPrompt))

        if (request.fileContexts.isNotEmpty()) {
            val contextText = buildString {
                appendLine("The user has selected the following files from their project:")
                request.fileContexts.forEach { ctx ->
                    appendLine("\n--- ${ctx.filePath} ---")
                    appendLine(ctx.content)
                }
            }
            messages.add(RequestMessageDto(role = "system", content = contextText))
        }

        request.conversationHistory.forEach { msg ->
            val role = when (msg.role) {
                MessageRole.USER -> "user"
                MessageRole.ASSISTANT -> "assistant"
                MessageRole.SYSTEM -> "system"
            }
            messages.add(RequestMessageDto(role = role, content = msg.content))
        }

        messages.add(RequestMessageDto(role = "user", content = request.prompt))

        return messages
    }

    companion object {
        private const val TAG = "GLM4AI"
        private const val API_URL = "https://integrate.api.nvidia.com/v1/chat/completions"
        private const val MODEL_NAME = "z-ai/glm4.7"
    }
}

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<RequestMessageDto>,
    val temperature: Double = 0.6,
    @SerialName("top_p") val topP: Double = 0.95,
    @SerialName("max_tokens") val maxTokens: Int = 8192,
    val stream: Boolean = true,
)

@Serializable
data class RequestMessageDto(
    val role: String,
    val content: String,
)

@Serializable
data class StreamDelta(
    val content: String? = null,
    @SerialName("reasoning_content") val reasoningContent: String? = null,
    val role: String? = null,
)

@Serializable
data class StreamChoice(
    val index: Int = 0,
    val delta: StreamDelta = StreamDelta(),
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class StreamChunkResponse(
    val id: String = "",
    val choices: List<StreamChoice> = emptyList(),
)
