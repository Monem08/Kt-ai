package com.monem.ktai.data.remote.ai

import com.monem.ktai.domain.model.EditType
import com.monem.ktai.domain.model.FileEdit

class PlaceholderAIProvider : AIProvider {
    override val name = "Placeholder"
    override val requiresApiKey = false

    override suspend fun sendMessage(request: AIRequest): Result<AIResponse> {
        val prompt = request.prompt.lowercase()

        return Result.success(
            when {
                "create" in prompt && "file" in prompt -> AIResponse(
                    message = "I'll create a new Kotlin file for you. Here's the generated code:",
                    fileEdits = listOf(
                        FileEdit(
                            filePath = "app/src/main/java/NewFile.kt",
                            oldContent = "",
                            newContent = "package com.example\n\nclass NewFile {\n    // TODO: Implement\n}",
                            editType = EditType.CREATE,
                        )
                    ),
                )
                "fix" in prompt || "error" in prompt -> AIResponse(
                    message = "I've analyzed the issue. Here's my suggested fix:\n\n" +
                        "The error is likely caused by a missing dependency or incorrect import. " +
                        "Please share the error message and relevant code for a specific fix.",
                )
                "explain" in prompt -> AIResponse(
                    message = "Here's an explanation of the code:\n\n" +
                        "This code follows the MVVM architecture pattern with Jetpack Compose for UI. " +
                        "Share the specific code you'd like me to explain.",
                )
                else -> AIResponse(
                    message = "I'm the Kt AI coding assistant. I can help you:\n\n" +
                        "- Create new Kotlin files\n" +
                        "- Edit existing code\n" +
                        "- Fix Gradle errors\n" +
                        "- Generate Compose screens\n" +
                        "- Refactor code\n" +
                        "- Explain code\n\n" +
                        "What would you like me to help with?",
                )
            }
        )
    }

    override suspend fun isAvailable(): Boolean = true
}
