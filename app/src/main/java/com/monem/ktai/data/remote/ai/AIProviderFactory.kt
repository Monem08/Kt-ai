package com.monem.ktai.data.remote.ai

import io.ktor.client.HttpClient

enum class AIProviderType {
    PLACEHOLDER,
    GLM4,
    DEEPSEEK,
    HUGGINGFACE,
    NVIDIA,
    OPENROUTER,
    CUSTOM,
}

object AIProviderFactory {
    fun create(type: AIProviderType, apiKey: String? = null, httpClient: HttpClient? = null): AIProvider {
        return when (type) {
            AIProviderType.PLACEHOLDER -> PlaceholderAIProvider()
            AIProviderType.GLM4 -> {
                requireNotNull(apiKey) { "GLM-4 requires an API key" }
                requireNotNull(httpClient) { "GLM-4 requires an HTTP client" }
                GLM4AIProvider(apiKey, httpClient)
            }
            AIProviderType.DEEPSEEK -> TODO("Implement DeepSeek provider with API key")
            AIProviderType.HUGGINGFACE -> TODO("Implement HuggingFace provider with API key")
            AIProviderType.NVIDIA -> TODO("Implement NVIDIA provider with API key")
            AIProviderType.OPENROUTER -> TODO("Implement OpenRouter provider with API key")
            AIProviderType.CUSTOM -> TODO("Implement Custom API provider")
        }
    }
}
