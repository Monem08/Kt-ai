package com.monem.ktai.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.monem.ktai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    suspend fun checkAuthSession(): Boolean {
        return try {
            authRepository.getCurrentUser() != null
        } catch (_: Exception) {
            false
        }
    }
}
