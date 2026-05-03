package com.monem.ktai.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.monem.ktai.presentation.common.components.KtAICard
import com.monem.ktai.presentation.common.components.KtAIOutlinedButton
import com.monem.ktai.presentation.common.components.PlanBadge
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.PrimaryContainer
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsState()
    val isLoggingOut by viewModel.isLoggingOut.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .padding(16.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                user?.displayName ?: "Developer",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(8.dp))
            PlanBadge(plan = user?.plan?.name ?: "Free")
        }

        Spacer(Modifier.height(32.dp))

        ProfileMenuItem(icon = Icons.Default.Settings, title = "Settings", onClick = onNavigateToSettings)
        Spacer(Modifier.height(12.dp))
        ProfileMenuItem(icon = Icons.Default.Security, title = "Security & Permissions", onClick = onNavigateToSecurity)
        Spacer(Modifier.height(12.dp))
        ProfileMenuItem(icon = Icons.Default.CreditCard, title = "Subscription", onClick = onNavigateToSubscription)

        Spacer(Modifier.height(32.dp))

        KtAIOutlinedButton(
            text = if (isLoggingOut) "Logging out..." else "Logout",
            onClick = { viewModel.logout(onLogout) },
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    KtAICard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(12.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        }
    }
}
