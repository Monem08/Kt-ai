package com.monem.ktai.domain.repository

import com.monem.ktai.domain.model.Workspace
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {
    fun getWorkspaces(userId: String): Flow<List<Workspace>>
    suspend fun createWorkspace(workspace: Workspace): Result<Workspace>
    suspend fun deleteWorkspace(workspaceId: String): Result<Unit>
    suspend fun updateLastOpened(workspaceId: String)
}
