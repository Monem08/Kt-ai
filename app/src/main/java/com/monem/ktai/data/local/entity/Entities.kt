package com.monem.ktai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val plan: String,
    val createdAt: Long,
)

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val folderUri: String,
    val createdAt: Long,
    val lastOpenedAt: Long,
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val workspaceId: String?,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val role: String,
    val content: String,
    val codeBlocksJson: String?,
    val fileEditsJson: String?,
    val timestamp: Long,
)

@Entity(tableName = "file_changes")
data class FileChangeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val workspaceId: String,
    val sessionId: String,
    val filePath: String,
    val oldContent: String,
    val newContent: String,
    val requestSummary: String,
    val timestamp: Long,
    val isRolledBack: Boolean,
)

@Entity(tableName = "usage_records")
data class UsageRecordEntity(
    @PrimaryKey val userId: String,
    val plan: String,
    val messagesUsed: Int,
    val messagesLimit: Int,
    val workspacesUsed: Int,
    val workspacesLimit: Int,
    val contextTokensLimit: Int,
    val resetAt: Long,
)
