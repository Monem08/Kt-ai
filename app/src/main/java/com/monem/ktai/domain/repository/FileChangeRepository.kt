package com.monem.ktai.domain.repository

import com.monem.ktai.domain.model.FileChange
import kotlinx.coroutines.flow.Flow

interface FileChangeRepository {
    fun getChanges(userId: String): Flow<List<FileChange>>
    fun getChangesForWorkspace(workspaceId: String): Flow<List<FileChange>>
    suspend fun saveChange(change: FileChange): Result<FileChange>
    suspend fun rollback(changeId: String): Result<Unit>
}
