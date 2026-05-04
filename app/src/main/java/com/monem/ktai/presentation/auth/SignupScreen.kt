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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.monem.ktai.presentation.common.theme.AccentGreen
import com.monem.ktai.presentation.common.theme.AccentRed
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var displayName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var agreedToTerms by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateToHome()
    }

    if (uiState.confirmationRequired) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text("Check Your Email", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "We sent a confirmation link to $email. Please verify your email then sign in.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(24.dp))
            KtAIButton(text = "Go to Sign In", onClick = onNavigateToLogin)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        Icon(
            imageVector = Icons.Default.Code,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text("Create Account", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("Start coding with AI", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(32.dp))

        KtAITextField(value = displayName, onValueChange = { displayName = it }, label = "Display Name")
        Spacer(Modifier.height(16.dp))
        KtAITextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(Modifier.height(16.dp))
        KtAITextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)
        Spacer(Modifier.height(16.dp))
        KtAITextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password", isPassword = true)

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = { agreedToTerms = it },
                colors = CheckboxDefaults.colors(checkedColor = Primary),
            )
            Text("I agree to the Terms of Service", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        if (uiState.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(uiState.error!!, style = MaterialTheme.typography.bodySmall, color = AccentRed)
        }

        Spacer(Modifier.height(24.dp))
        KtAIButton(
            text = "Create Account",
            onClick = { viewModel.signup(email, password, confirmPassword, displayName) },
            isLoading = uiState.isLoading,
            enabled = agreedToTerms,
        )

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(
                "Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = Primary,
                modifier = Modifier.clickable { onNavigateToLogin() },
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
