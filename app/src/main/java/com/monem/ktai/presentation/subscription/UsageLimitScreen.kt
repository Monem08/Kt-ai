package com.monem.ktai.presentation.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monem.ktai.presentation.common.components.KtAIButton
import com.monem.ktai.presentation.common.components.KtAICard
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.components.PlanBadge
import com.monem.ktai.presentation.common.components.UsageBar
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun UsageLimitScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = "Usage & Limits", onBack = onNavigateBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            KtAICard {
                Column {
                    Text("Current Plan", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    PlanBadge(plan = "Free")
                    Spacer(Modifier.height(4.dp))
                    Text("Resets in 28 days", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            KtAICard {
                Column {
                    Text("AI Messages", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    UsageBar(used = 5, total = 20, label = "Messages used this month")
                }
            }

            KtAICard {
                Column {
                    Text("Workspaces", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    UsageBar(used = 1, total = 1, label = "Active workspaces")
                }
            }

            KtAICard {
                Column {
                    Text("Context Window", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("2,000 tokens per request", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("Upgrade to Pro for 8K or Max for 32K", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            KtAIButton(text = "Upgrade Plan", onClick = { /* TODO: Navigate to subscription */ })
        }
    }
}
