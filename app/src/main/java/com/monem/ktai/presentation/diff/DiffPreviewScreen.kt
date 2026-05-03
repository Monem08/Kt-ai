package com.monem.ktai.presentation.diff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.monem.ktai.domain.model.DiffLine
import com.monem.ktai.domain.model.DiffLineType
import com.monem.ktai.presentation.common.components.KtAIButton
import com.monem.ktai.presentation.common.components.KtAIOutlinedButton
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.AccentOrange
import com.monem.ktai.presentation.common.theme.DiffAddedBg
import com.monem.ktai.presentation.common.theme.DiffAddedText
import com.monem.ktai.presentation.common.theme.DiffRemovedBg
import com.monem.ktai.presentation.common.theme.DiffRemovedText
import com.monem.ktai.presentation.common.theme.DiffUnchangedText
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.SurfaceContainerHigh
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary
import com.monem.ktai.presentation.common.theme.TextTertiary

private val sampleDiff = listOf(
    DiffLine(null, "--- a/App.kt", DiffLineType.HEADER),
    DiffLine(null, "+++ b/App.kt", DiffLineType.HEADER),
    DiffLine(1, "package com.monem.ktai", DiffLineType.UNCHANGED),
    DiffLine(2, "", DiffLineType.UNCHANGED),
    DiffLine(3, "import android.app.Application", DiffLineType.UNCHANGED),
    DiffLine(null, "- class OldApplication : Application()", DiffLineType.REMOVED),
    DiffLine(null, "+ @HiltAndroidApp", DiffLineType.ADDED),
    DiffLine(null, "+ class KtAIApplication : Application()", DiffLineType.ADDED),
)

@Composable
fun DiffPreviewScreen(
    onApply: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = "Code Diff Preview", onBack = onCancel)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            // File info
            Text("App.kt", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("app/src/main/java/com/monem/ktai/", style = MaterialTheme.typography.bodySmall, color = TextTertiary)

            Spacer(Modifier.height(16.dp))

            // Warning
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AccentOrange.copy(alpha = 0.1f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AccentOrange)
                Spacer(Modifier.width(8.dp))
                Text(
                    "AI can make mistakes. Review before applying.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentOrange,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Diff view
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerHigh)
                    .padding(8.dp),
            ) {
                sampleDiff.forEach { line ->
                    val (bgColor, textColor, prefix) = when (line.type) {
                        DiffLineType.ADDED -> Triple(DiffAddedBg, DiffAddedText, "+ ")
                        DiffLineType.REMOVED -> Triple(DiffRemovedBg, DiffRemovedText, "- ")
                        DiffLineType.UNCHANGED -> Triple(SurfaceContainerHigh, DiffUnchangedText, "  ")
                        DiffLineType.HEADER -> Triple(SurfaceContainerHigh, TextTertiary, "")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        if (line.lineNumber != null) {
                            Text(
                                "${line.lineNumber}".padStart(3),
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = TextTertiary,
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            text = if (line.type == DiffLineType.HEADER) line.content else "$prefix${line.content}",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = textColor,
                        )
                    }
                }
            }
        }

        // Action buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            KtAIButton(text = "Apply Changes", onClick = onApply)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KtAIOutlinedButton(
                    text = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                )
                KtAIOutlinedButton(
                    text = "Regenerate",
                    onClick = { /* TODO: Request regeneration */ },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
