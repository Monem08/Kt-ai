package com.monem.ktai.domain.repository

import com.monem.ktai.domain.model.ChatMessage
import com.monem.ktai.domain.model.ChatSession
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getSessions(userId: String): Flow<List<ChatSession>>
    fun getMessages(sessionId: String): Flow<List<ChatMessage>>
    suspend fun createSession(session: ChatSession): Result<ChatSession>
    suspend fun addMessage(message: ChatMessage): Result<ChatMessage>
    suspend fun deleteSession(sessionId: String): Result<Unit>
}
