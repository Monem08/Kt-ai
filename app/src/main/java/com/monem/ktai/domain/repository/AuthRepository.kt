package com.monem.ktai.domain.repository

import com.monem.ktai.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    val isLoggedIn: Flow<Boolean>
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun signup(email: String, password: String, displayName: String): Result<UserProfile>
    suspend fun logout()
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun getCurrentUser(): UserProfile?
}
