package com.monem.ktai.data.remote.ai

enum class AIProviderType {
    PLACEHOLDER,
    DEEPSEEK,
    HUGGINGFACE,
    NVIDIA,
    OPENROUTER,
    CUSTOM,
}

object AIProviderFactory {
    fun create(type: AIProviderType, apiKey: String? = null): AIProvider {
        return when (type) {
            AIProviderType.PLACEHOLDER -> PlaceholderAIProvider()
            AIProviderType.DEEPSEEK -> TODO("Implement DeepSeek provider with API key")
            AIProviderType.HUGGINGFACE -> TODO("Implement HuggingFace provider with API key")
            AIProviderType.NVIDIA -> TODO("Implement NVIDIA provider with API key")
            AIProviderType.OPENROUTER -> TODO("Implement OpenRouter provider with API key")
            AIProviderType.CUSTOM -> TODO("Implement Custom API provider")
        }
    }
}
