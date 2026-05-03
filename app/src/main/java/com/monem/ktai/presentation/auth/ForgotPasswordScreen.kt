package com.monem.ktai.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.monem.ktai.presentation.common.components.KtAIButton
import com.monem.ktai.presentation.common.components.KtAITextField
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.AccentGreen
import com.monem.ktai.presentation.common.theme.AccentRed
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = "Reset Password", onBack = onNavigateBack)

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Enter your email and we'll send you a link to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(Modifier.height(24.dp))
            KtAITextField(value = email, onValueChange = { email = it }, label = "Email")

            if (uiState.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(uiState.error!!, style = MaterialTheme.typography.bodySmall, color = AccentRed)
            }

            if (uiState.resetEmailSent) {
                Spacer(Modifier.height(8.dp))
                Text("Reset link sent! Check your email.", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
            }

            Spacer(Modifier.height(24.dp))
            KtAIButton(
                text = "Send Reset Link",
                onClick = { viewModel.sendPasswordReset(email) },
                isLoading = uiState.isLoading,
            )
        }
    }
}
