package com.monem.ktai.presentation.workspace

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.monem.ktai.presentation.common.components.KtAIButton
import com.monem.ktai.presentation.common.components.KtAICard
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.AccentGreen
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

@Composable
fun SelectFolderScreen(
    onNavigateBack: () -> Unit,
    onFolderSelected: () -> Unit,
    viewModel: WorkspaceViewModel = hiltViewModel(),
) {
    val folderSaved by viewModel.folderSaved.collectAsState()

    LaunchedEffect(folderSaved) {
        if (folderSaved) {
            viewModel.resetFolderSaved()
            onFolderSelected()
        }
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        if (uri != null) {
            viewModel.onFolderSelected(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = "Select Project Folder", onBack = onNavigateBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Default.CreateNewFolder,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(72.dp),
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Select Your Project Folder",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Kt AI will only access files inside the folder you select. " +
                    "We use Android's Storage Access Framework for safe, scoped access.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            KtAICard {
                Column {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Privacy Guarantee", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "• No full storage scan\n• Only accesses selected folder\n• No MANAGE_EXTERNAL_STORAGE\n• Uses ACTION_OPEN_DOCUMENT_TREE",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            KtAIButton(
                text = "Choose Folder",
                onClick = { folderPickerLauncher.launch(null) },
            )
        }
    }
}
