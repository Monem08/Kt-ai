package com.monem.ktai.presentation.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.monem.ktai.domain.model.FileNode
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.Accent
import com.monem.ktai.presentation.common.theme.AccentGreen
import com.monem.ktai.presentation.common.theme.AccentOrange
import com.monem.ktai.presentation.common.theme.CardBorder
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

private val sampleFiles = listOf(
    FileNode("app", "app", isDirectory = true),
    FileNode("build.gradle.kts", "build.gradle.kts", isDirectory = false, extension = "kts"),
    FileNode("settings.gradle.kts", "settings.gradle.kts", isDirectory = false, extension = "kts"),
    FileNode("gradle.properties", "gradle.properties", isDirectory = false, extension = "properties"),
    FileNode("README.md", "README.md", isDirectory = false, extension = "md"),
)

@Composable
fun FileExplorerScreen(
    workspaceId: String,
    onNavigateToFileViewer: (String) -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val selectedFiles = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(
            title = "File Explorer",
            onBack = onNavigateBack,
            actions = {
                if (selectedFiles.isNotEmpty()) {
                    IconButton(onClick = onNavigateToChat) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat with selected files", tint = Primary)
                    }
                }
            },
        )

        if (selectedFiles.isNotEmpty()) {
            Text(
                "${selectedFiles.size} file(s) selected for AI context",
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(sampleFiles) { file ->
                FileRow(
                    file = file,
                    isSelected = file.path in selectedFiles,
                    onSelect = {
                        if (file.path in selectedFiles) selectedFiles.remove(file.path)
                        else selectedFiles.add(file.path)
                    },
                    onClick = {
                        if (file.isDirectory) {
                            // TODO: Navigate into directory
                        } else {
                            onNavigateToFileViewer(file.path)
                        }
                    },
                )
                HorizontalDivider(color = CardBorder.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
private fun FileRow(
    file: FileNode,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onClick: () -> Unit,
) {
    val iconColor = when {
        file.isDirectory -> AccentOrange
        file.extension in listOf("kt", "kts") -> Primary
        file.extension == "xml" -> AccentGreen
        file.extension in listOf("gradle", "properties") -> Accent
        else -> TextSecondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!file.isDirectory) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelect() },
                colors = CheckboxDefaults.colors(checkedColor = Primary),
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
        }

        Icon(
            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = file.name,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )
    }
}
