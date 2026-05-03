package com.monem.ktai.data.repository

import com.monem.ktai.data.local.dao.FileChangeDao
import com.monem.ktai.data.local.entity.FileChangeEntity
import com.monem.ktai.domain.model.FileChange
import com.monem.ktai.domain.repository.FileChangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileChangeRepositoryImpl @Inject constructor(
    private val fileChangeDao: FileChangeDao,
) : FileChangeRepository {

    override fun getChanges(userId: String): Flow<List<FileChange>> =
        fileChangeDao.getChanges(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getChangesForWorkspace(workspaceId: String): Flow<List<FileChange>> =
        fileChangeDao.getChangesForWorkspace(workspaceId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveChange(change: FileChange): Result<FileChange> {
        return try {
            fileChangeDao.insert(change.toEntity())
            Result.success(change)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rollback(changeId: String): Result<Unit> {
        return try {
            val change = fileChangeDao.getChange(changeId)
                ?: return Result.failure(Exception("Change not found"))
            // TODO: Restore file content via SAF using change.oldContent
            fileChangeDao.markRolledBack(changeId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun FileChange.toEntity() = FileChangeEntity(
        id = id, userId = userId, workspaceId = workspaceId,
        sessionId = sessionId, filePath = filePath,
        oldContent = oldContent, newContent = newContent,
        requestSummary = requestSummary, timestamp = timestamp,
        isRolledBack = isRolledBack,
    )

    private fun FileChangeEntity.toDomain() = FileChange(
        id = id, userId = userId, workspaceId = workspaceId,
        sessionId = sessionId, filePath = filePath,
        oldContent = oldContent, newContent = newContent,
        requestSummary = requestSummary, timestamp = timestamp,
        isRolledBack = isRolledBack,
    )
}
