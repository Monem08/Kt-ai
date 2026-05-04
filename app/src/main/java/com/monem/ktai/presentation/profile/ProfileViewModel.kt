package com.monem.ktai.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monem.ktai.domain.model.UserProfile
import com.monem.ktai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user.asStateFlow()

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { _user.value = it }
        }
        viewModelScope.launch {
            if (_user.value == null) {
                _user.value = authRepository.getCurrentUser()
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoggingOut.value = true
            authRepository.logout()
            _isLoggingOut.value = false
            onComplete()
        }
    }
}
