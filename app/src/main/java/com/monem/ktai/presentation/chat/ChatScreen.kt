package com.monem.ktai.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DifferenceOutlined
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.monem.ktai.domain.model.ChatMessage
import com.monem.ktai.domain.model.MessageRole
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.CardBackground
import com.monem.ktai.presentation.common.theme.CardBorder
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.PrimaryContainer
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.SurfaceContainerHigh
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary
import com.monem.ktai.presentation.common.theme.TextTertiary

@Composable
fun ChatScreen(
    sessionId: String,
    onNavigateToDiff: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    LaunchedEffect(uiState.pendingFileEdits) {
        if (uiState.pendingFileEdits.isNotEmpty()) {
            onNavigateToDiff()
            viewModel.clearPendingEdits()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .imePadding(),
    ) {
        KtAITopBar(title = "AI Chat", onBack = onNavigateBack)

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.messages.isEmpty()) {
                item {
                    EmptyChatPlaceholder()
                }
            }

            items(uiState.messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }

            if (uiState.isLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Primary,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Thinking...", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask AI to code...", color = TextTertiary) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = CardBorder,
                    cursorColor = Primary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                ),
            )
            Spacer(Modifier.width(8.dp))
            SmallFloatingActionButton(
                onClick = {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                },
                containerColor = Primary,
                shape = CircleShape,
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = TextPrimary)
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    val bgColor = if (isUser) PrimaryContainer else SurfaceContainerHigh
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp,
                    )
                )
                .background(bgColor)
                .padding(12.dp),
        ) {
            if (!isUser) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Kt AI", style = MaterialTheme.typography.labelSmall, color = Primary)
                }
                Spacer(Modifier.height(4.dp))
            }

            Text(
                text = message.content,
                style = if (isUser) MaterialTheme.typography.bodyMedium
                else MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Default),
                color = TextPrimary,
            )

            if (message.fileEdits != null && message.fileEdits.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Icon(
                        Icons.Default.DifferenceOutlined,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${message.fileEdits.size} file edit(s) ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyChatPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.Code,
            contentDescription = null,
            tint = Primary.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text("Start a conversation", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            "Ask me to create files, fix bugs,\ngenerate screens, or explain code.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}
