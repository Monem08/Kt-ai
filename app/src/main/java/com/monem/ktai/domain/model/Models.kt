package com.monem.ktai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarUrl: String? = null,
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
enum class SubscriptionPlan {
    FREE, PRO, MAX
}

@Serializable
data class Workspace(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val folderUri: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastOpenedAt: Long = System.currentTimeMillis(),
)

@Serializable
data class ChatSession(
    val id: String = "",
    val userId: String = "",
    val workspaceId: String? = null,
    val title: String = "New Chat",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Serializable
data class ChatMessage(
    val id: String = "",
    val sessionId: String = "",
    val role: MessageRole = MessageRole.USER,
    val content: String = "",
    val codeBlocks: List<CodeBlock> = emptyList(),
    val fileEdits: List<FileEdit>? = null,
    val timestamp: Long = System.currentTimeMillis(),
)

@Serializable
enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

@Serializable
data class CodeBlock(
    val language: String = "kotlin",
    val code: String = "",
)

@Serializable
data class FileEdit(
    val filePath: String = "",
    val oldContent: String = "",
    val newContent: String = "",
    val editType: EditType = EditType.MODIFY,
)

@Serializable
enum class EditType {
    CREATE, MODIFY, DELETE
}

@Serializable
data class FileChange(
    val id: String = "",
    val userId: String = "",
    val workspaceId: String = "",
    val sessionId: String = "",
    val filePath: String = "",
    val oldContent: String = "",
    val newContent: String = "",
    val requestSummary: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRolledBack: Boolean = false,
)

@Serializable
data class UsageLimit(
    val userId: String = "",
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val messagesUsed: Int = 0,
    val messagesLimit: Int = 20,
    val workspacesUsed: Int = 0,
    val workspacesLimit: Int = 1,
    val contextTokensLimit: Int = 2000,
    val resetAt: Long = 0,
)

data class FileNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: List<FileNode> = emptyList(),
    val extension: String = name.substringAfterLast('.', ""),
    val size: Long = 0,
    val uri: String = "",
)

data class DiffLine(
    val lineNumber: Int?,
    val content: String,
    val type: DiffLineType,
)

enum class DiffLineType {
    ADDED, REMOVED, UNCHANGED, HEADER
}
