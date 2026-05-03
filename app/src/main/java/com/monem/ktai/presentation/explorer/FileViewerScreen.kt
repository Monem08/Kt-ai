package com.monem.ktai.presentation.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.SurfaceContainerHigh
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary
import com.monem.ktai.presentation.common.theme.TextTertiary

private val sampleCode = """package com.monem.ktai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KtAIApplication : Application()"""

@Composable
fun FileViewerScreen(
    filePath: String,
    onNavigateBack: () -> Unit,
) {
    val fileName = filePath.substringAfterLast("/")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = fileName, onBack = onNavigateBack)

        Text(
            text = filePath,
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        Spacer(Modifier.height(8.dp))

        // Code viewer with line numbers
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .background(SurfaceContainerHigh)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .padding(12.dp),
        ) {
            // TODO: Read actual file content via SAF
            sampleCode.lines().forEachIndexed { index, line ->
                Row {
                    Text(
                        text = "${index + 1}".padStart(3),
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = TextTertiary,
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = TextPrimary,
                    )
                }
            }
        }
    }
}
