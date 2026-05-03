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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monem.ktai.presentation.common.components.KtAICard
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.AccentGreen
import com.monem.ktai.presentation.common.theme.AccentOrange
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun SecurityScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = "Security & Permissions", onBack = onNavigateBack)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KtAICard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Storage Access", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    }
                    Spacer(Modifier.height(12.dp))

                    SecurityItem(text = "Uses Storage Access Framework (SAF)", isSecure = true)
                    SecurityItem(text = "No MANAGE_EXTERNAL_STORAGE", isSecure = true)
                    SecurityItem(text = "Only accesses user-selected folders", isSecure = true)
                    SecurityItem(text = "Never scans full phone storage", isSecure = true)
                }
            }

            KtAICard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sensitive File Protection", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "The following files are protected and require explicit confirmation before any AI modifications:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    listOf(".env", "keystore files", "google-services.json", "local.properties").forEach { file ->
                        Text("  \u2022 $file", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                }
            }

            KtAICard {
                Column {
                    Text("Data Handling", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "• File content is sent to AI only when you ask\n" +
                            "• Backups are created before every change\n" +
                            "• Rollback is always available\n" +
                            "• Diff preview shown before applying",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityItem(text: String, isSecure: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isSecure) AccentGreen else AccentOrange,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}
