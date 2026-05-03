package com.monem.ktai.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.monem.ktai.presentation.common.components.KtAICard
import com.monem.ktai.presentation.common.components.PlanBadge
import com.monem.ktai.presentation.common.components.UsageBar
import com.monem.ktai.presentation.common.theme.Accent
import com.monem.ktai.presentation.common.theme.AccentGreen
import com.monem.ktai.presentation.common.theme.AccentOrange
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToWorkspace: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToUsage: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        // Greeting
        Text(
            "Hello, ${user?.displayName ?: "Developer"}",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Current Plan: ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            PlanBadge(plan = user?.plan?.name ?: "Free")
        }

        Spacer(Modifier.height(24.dp))

        // Usage card
        KtAICard(onClick = onNavigateToUsage) {
            Column {
                Text("Usage", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(12.dp))
                UsageBar(used = 5, total = 20, label = "AI Messages")
                Spacer(Modifier.height(8.dp))
                UsageBar(used = 1, total = 1, label = "Workspaces")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Quick Actions
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                icon = Icons.Default.Chat,
                title = "New Chat",
                color = Primary,
                onClick = onNavigateToChat,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Default.Folder,
                title = "Workspace",
                color = AccentGreen,
                onClick = onNavigateToWorkspace,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                icon = Icons.Default.Rocket,
                title = "Upgrade",
                color = AccentOrange,
                onClick = onNavigateToSubscription,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Default.TrendingUp,
                title = "Usage",
                color = Accent,
                onClick = onNavigateToUsage,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(24.dp))

        // Recent workspaces
        Text("Recent Workspaces", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(12.dp))
        KtAICard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(40.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text("No workspaces yet", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text("Select a project folder to get started", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KtAICard(modifier = modifier, onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        }
    }
}
