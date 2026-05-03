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
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val userDao: UserDao,
    private val usageDao: UsageDao,
    private val workspaceDao: WorkspaceDao,
    private val chatSessionDao: ChatSessionDao,
    private val messageDao: MessageDao,
    private val fileChangeDao: FileChangeDao,
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)

    override val currentUser: Flow<UserProfile?> = _currentUser

    override val isLoggedIn: Flow<Boolean> = _currentUser.map { it != null }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val session = auth.currentSessionOrNull()
            val user = session?.user ?: return Result.failure(Exception("Login succeeded but no user returned"))

            val profile = UserProfile(
                id = user.id,
                email = user.email ?: email,
                displayName = user.userMetadata?.get("display_name")?.toString()?.trim('"')
                    ?: email.substringBefore("@"),
                plan = SubscriptionPlan.FREE,
            )

            saveUserLocally(profile)
            _currentUser.value = profile
            Result.success(profile)
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("Invalid login credentials") == true -> "Invalid email or password"
                e.message?.contains("Email not confirmed") == true -> "Please verify your email first"
                e.message?.contains("network", ignoreCase = true) == true -> "Network error. Check your connection"
                else -> e.message ?: "Login failed"
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String,
    ): Result<UserProfile> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = kotlinx.serialization.json.buildJsonObject {
                    put("display_name", kotlinx.serialization.json.JsonPrimitive(displayName))
                }
            }

            val session = auth.currentSessionOrNull()
            val user = session?.user

            if (user != null) {
                val profile = UserProfile(
                    id = user.id,
                    email = user.email ?: email,
                    displayName = displayName,
                    plan = SubscriptionPlan.FREE,
                )
                saveUserLocally(profile)
                _currentUser.value = profile
                Result.success(profile)
            } else {
                val profile = UserProfile(
                    email = email,
                    displayName = displayName,
                    plan = SubscriptionPlan.FREE,
                )
                Result.success(profile)
            }
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("already registered") == true -> "This email is already registered"
                e.message?.contains("password", ignoreCase = true) == true -> "Password is too weak (min 6 characters)"
                e.message?.contains("network", ignoreCase = true) == true -> "Network error. Check your connection"
                else -> e.message ?: "Signup failed"
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) {
            // Continue with local cleanup even if remote logout fails
        }
        _currentUser.value = null
        messageDao.clearAll()
        chatSessionDao.clearAll()
        fileChangeDao.clearAll()
        workspaceDao.clearAll()
        usageDao.clearAll()
        userDao.clearAll()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to send reset email"))
        }
    }

    override suspend fun getCurrentUser(): UserProfile? {
        return try {
            val session = auth.currentSessionOrNull()
            val user = session?.user ?: return null

            val profile = UserProfile(
                id = user.id,
                email = user.email ?: "",
                displayName = user.userMetadata?.get("display_name")?.toString()?.trim('"')
                    ?: user.email?.substringBefore("@") ?: "",
                plan = SubscriptionPlan.FREE,
            )
            saveUserLocally(profile)
            _currentUser.value = profile
            profile
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun saveUserLocally(profile: UserProfile) {
        userDao.insertUser(profile.toEntity())
        val existingUsage = usageDao.getUsage(profile.id)
        if (existingUsage == null) {
            usageDao.insert(
                UsageRecordEntity(
                    userId = profile.id,
                    plan = "FREE",
                    messagesUsed = 0,
                    messagesLimit = 20,
                    workspacesUsed = 0,
                    workspacesLimit = 1,
                    contextTokensLimit = 2000,
                    resetAt = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
                )
            )
        }
    }

    private fun UserProfile.toEntity() = UserProfileEntity(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        plan = plan.name,
        createdAt = createdAt,
    )
}
