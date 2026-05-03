package com.monem.ktai.data.repository

import com.monem.ktai.data.local.dao.ChatSessionDao
import com.monem.ktai.data.local.dao.FileChangeDao
import com.monem.ktai.data.local.dao.MessageDao
import com.monem.ktai.data.local.dao.UsageDao
import com.monem.ktai.data.local.dao.UserDao
import com.monem.ktai.data.local.dao.WorkspaceDao
import com.monem.ktai.data.local.entity.UsageRecordEntity
import com.monem.ktai.data.local.entity.UserProfileEntity
import com.monem.ktai.domain.model.SubscriptionPlan
import com.monem.ktai.domain.model.UserProfile
import com.monem.ktai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val usageDao: UsageDao,
    private val workspaceDao: WorkspaceDao,
    private val chatSessionDao: ChatSessionDao,
    private val messageDao: MessageDao,
    private val fileChangeDao: FileChangeDao,
) : AuthRepository {

    override val currentUser: Flow<UserProfile?> =
        userDao.observeCurrentUser().map { it?.toDomain() }

    override val isLoggedIn: Flow<Boolean> =
        currentUser.map { it != null }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        // TODO: Replace with Supabase auth
        return try {
            val userId = UUID.randomUUID().toString()
            val profile = UserProfile(
                id = userId,
                email = email,
                displayName = email.substringBefore("@"),
                plan = SubscriptionPlan.FREE,
            )
            userDao.insertUser(profile.toEntity())
            usageDao.insert(
                UsageRecordEntity(
                    userId = userId,
                    plan = "FREE",
                    messagesUsed = 0,
                    messagesLimit = 20,
                    workspacesUsed = 0,
                    workspacesLimit = 1,
                    contextTokensLimit = 2000,
                    resetAt = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
                )
            )
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String,
    ): Result<UserProfile> {
        // TODO: Replace with Supabase auth
        return try {
            val userId = UUID.randomUUID().toString()
            val profile = UserProfile(
                id = userId,
                email = email,
                displayName = displayName,
                plan = SubscriptionPlan.FREE,
            )
            userDao.insertUser(profile.toEntity())
            usageDao.insert(
                UsageRecordEntity(
                    userId = userId,
                    plan = "FREE",
                    messagesUsed = 0,
                    messagesLimit = 20,
                    workspacesUsed = 0,
                    workspacesLimit = 1,
                    contextTokensLimit = 2000,
                    resetAt = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
                )
            )
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        messageDao.clearAll()
        chatSessionDao.clearAll()
        fileChangeDao.clearAll()
        workspaceDao.clearAll()
        usageDao.clearAll()
        userDao.clearAll()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        // TODO: Replace with Supabase auth
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): UserProfile? {
        return null // TODO: Replace with Supabase session check
    }

    private fun UserProfile.toEntity() = UserProfileEntity(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        plan = plan.name,
        createdAt = createdAt,
    )

    private fun UserProfileEntity.toDomain() = UserProfile(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        plan = SubscriptionPlan.valueOf(plan),
        createdAt = createdAt,
    )
}
