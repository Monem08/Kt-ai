package com.monem.ktai.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.monem.ktai.presentation.common.components.KtAIButton
import com.monem.ktai.presentation.common.components.KtAITextField
import com.monem.ktai.presentation.common.theme.AccentRed
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(64.dp))

        Icon(
            imageVector = Icons.Default.Code,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("Sign in to continue", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(40.dp))

        KtAITextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            label = "Email",
        )
        Spacer(Modifier.height(16.dp))
        KtAITextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            label = "Password",
            isPassword = true,
        )

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                "Forgot Password?",
                style = MaterialTheme.typography.bodySmall,
                color = Primary,
                modifier = Modifier.clickable { onNavigateToForgotPassword() },
            )
        }

        if (uiState.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(uiState.error!!, style = MaterialTheme.typography.bodySmall, color = AccentRed)
        }

        Spacer(Modifier.height(24.dp))
        KtAIButton(
            text = "Sign In",
            onClick = { viewModel.login(email, password) },
            isLoading = uiState.isLoading,
        )

        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(
                "Sign Up",
                style = MaterialTheme.typography.bodyMedium,
                color = Primary,
                modifier = Modifier.clickable { onNavigateToSignup() },
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
