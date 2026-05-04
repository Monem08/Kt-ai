package com.monem.ktai.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monem.ktai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val resetEmailSent: Boolean = false,
    val confirmationRequired: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email and password are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.login(email, password)
                .onSuccess { _uiState.value = AuthUiState(isSuccess = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "Login failed") }
        }
    }

    fun signup(email: String, password: String, confirmPassword: String, displayName: String) {
        when {
            email.isBlank() || password.isBlank() || displayName.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "All fields are required")
                return
            }
            password != confirmPassword -> {
                _uiState.value = _uiState.value.copy(error = "Passwords do not match")
                return
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
                return
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.signup(email, password, displayName)
                .onSuccess { user ->
                    if (user.id.isEmpty()) {
                        _uiState.value = AuthUiState(confirmationRequired = true)
                    } else {
                        _uiState.value = AuthUiState(isSuccess = true)
                    }
                }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "Signup failed") }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email is required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.sendPasswordResetEmail(email)
                .onSuccess { _uiState.value = AuthUiState(resetEmailSent = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "Failed to send reset email") }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
