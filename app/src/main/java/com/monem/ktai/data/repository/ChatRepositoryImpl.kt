package com.monem.ktai.data.repository

import com.monem.ktai.data.local.dao.ChatSessionDao
import com.monem.ktai.data.local.dao.MessageDao
import com.monem.ktai.data.local.entity.ChatSessionEntity
import com.monem.ktai.data.local.entity.MessageEntity
import com.monem.ktai.domain.model.ChatMessage
import com.monem.ktai.domain.model.ChatSession
import com.monem.ktai.domain.model.MessageRole
import com.monem.ktai.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatSessionDao: ChatSessionDao,
    private val messageDao: MessageDao,
) : ChatRepository {

    override fun getSessions(userId: String): Flow<List<ChatSession>> =
        chatSessionDao.getSessions(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getMessages(sessionId: String): Flow<List<ChatMessage>> =
        messageDao.getMessages(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun createSession(session: ChatSession): Result<ChatSession> {
        return try {
            chatSessionDao.insert(session.toEntity())
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMessage(message: ChatMessage): Result<ChatMessage> {
        return try {
            messageDao.insert(message.toEntity())
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            messageDao.deleteBySession(sessionId)
            chatSessionDao.delete(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ChatSession.toEntity() = ChatSessionEntity(
        id = id, userId = userId, workspaceId = workspaceId,
        title = title, createdAt = createdAt, updatedAt = updatedAt,
    )

    private fun ChatSessionEntity.toDomain() = ChatSession(
        id = id, userId = userId, workspaceId = workspaceId,
        title = title, createdAt = createdAt, updatedAt = updatedAt,
    )

    private fun ChatMessage.toEntity() = MessageEntity(
        id = id, sessionId = sessionId, role = role.name,
        content = content, codeBlocksJson = null, fileEditsJson = null,
        timestamp = timestamp,
    )

    private fun MessageEntity.toDomain() = ChatMessage(
        id = id, sessionId = sessionId, role = MessageRole.valueOf(role),
        content = content, timestamp = timestamp,
    )
}
