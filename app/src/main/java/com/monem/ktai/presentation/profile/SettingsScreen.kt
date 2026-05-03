package com.monem.ktai.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monem.ktai.presentation.common.components.KtAICard
import com.monem.ktai.presentation.common.components.KtAIOutlinedButton
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    var darkTheme by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = "Settings", onBack = onNavigateBack)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KtAICard {
                Column {
                    Text("Appearance", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Dark Theme", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { darkTheme = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Primary),
                        )
                    }
                }
            }

            KtAICard {
                Column {
                    Text("AI Provider", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Placeholder (Default)", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Configure API keys in settings to enable DeepSeek, HuggingFace, NVIDIA, OpenRouter, or Custom providers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            KtAICard {
                Column {
                    Text("About", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Kt AI v1.0.0", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("AI Coding Agent for Android Developers", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            KtAIOutlinedButton(text = "Clear Cache", onClick = { /* TODO */ })
        }
    }
}
