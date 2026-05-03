package com.monem.ktai.data.repository

import com.monem.ktai.data.local.dao.WorkspaceDao
import com.monem.ktai.data.local.entity.WorkspaceEntity
import com.monem.ktai.domain.model.Workspace
import com.monem.ktai.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val workspaceDao: WorkspaceDao,
) : WorkspaceRepository {

    override fun getWorkspaces(userId: String): Flow<List<Workspace>> =
        workspaceDao.getWorkspaces(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun createWorkspace(workspace: Workspace): Result<Workspace> {
        return try {
            workspaceDao.insert(workspace.toEntity())
            Result.success(workspace)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWorkspace(workspaceId: String): Result<Unit> {
        return try {
            workspaceDao.delete(workspaceId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLastOpened(workspaceId: String) {
        workspaceDao.updateLastOpened(workspaceId, System.currentTimeMillis())
    }

    private fun Workspace.toEntity() = WorkspaceEntity(
        id = id,
        userId = userId,
        name = name,
        folderUri = folderUri,
        createdAt = createdAt,
        lastOpenedAt = lastOpenedAt,
    )

    private fun WorkspaceEntity.toDomain() = Workspace(
        id = id,
        userId = userId,
        name = name,
        folderUri = folderUri,
        createdAt = createdAt,
        lastOpenedAt = lastOpenedAt,
    )
}
