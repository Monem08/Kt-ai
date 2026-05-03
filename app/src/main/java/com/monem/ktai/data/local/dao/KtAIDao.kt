package com.monem.ktai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.monem.ktai.data.local.entity.ChatSessionEntity
import com.monem.ktai.data.local.entity.FileChangeEntity
import com.monem.ktai.data.local.entity.MessageEntity
import com.monem.ktai.data.local.entity.UsageRecordEntity
import com.monem.ktai.data.local.entity.UserProfileEntity
import com.monem.ktai.data.local.entity.WorkspaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    suspend fun getUser(userId: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun observeCurrentUser(): Flow<UserProfileEntity?>

    @Query("DELETE FROM user_profiles")
    suspend fun clearAll()
}

@Dao
interface WorkspaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workspace: WorkspaceEntity)

    @Query("SELECT * FROM workspaces WHERE userId = :userId ORDER BY lastOpenedAt DESC")
    fun getWorkspaces(userId: String): Flow<List<WorkspaceEntity>>

    @Query("DELETE FROM workspaces WHERE id = :workspaceId")
    suspend fun delete(workspaceId: String)

    @Query("UPDATE workspaces SET lastOpenedAt = :timestamp WHERE id = :workspaceId")
    suspend fun updateLastOpened(workspaceId: String, timestamp: Long)
}

@Dao
interface ChatSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ChatSessionEntity)

    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getSessions(userId: String): Flow<List<ChatSessionEntity>>

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun delete(sessionId: String)
}

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessages(sessionId: String): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE sessionId = :sessionId")
    suspend fun deleteBySession(sessionId: String)
}

@Dao
interface FileChangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(change: FileChangeEntity)

    @Query("SELECT * FROM file_changes WHERE userId = :userId ORDER BY timestamp DESC")
    fun getChanges(userId: String): Flow<List<FileChangeEntity>>

    @Query("SELECT * FROM file_changes WHERE workspaceId = :workspaceId ORDER BY timestamp DESC")
    fun getChangesForWorkspace(workspaceId: String): Flow<List<FileChangeEntity>>

    @Query("SELECT * FROM file_changes WHERE id = :changeId")
    suspend fun getChange(changeId: String): FileChangeEntity?

    @Query("UPDATE file_changes SET isRolledBack = 1 WHERE id = :changeId")
    suspend fun markRolledBack(changeId: String)
}

@Dao
interface UsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usage: UsageRecordEntity)

    @Query("SELECT * FROM usage_records WHERE userId = :userId")
    suspend fun getUsage(userId: String): UsageRecordEntity?

    @Query("SELECT * FROM usage_records WHERE userId = :userId")
    fun observeUsage(userId: String): Flow<UsageRecordEntity?>

    @Query("UPDATE usage_records SET messagesUsed = messagesUsed + 1 WHERE userId = :userId")
    suspend fun incrementMessageCount(userId: String)
}
