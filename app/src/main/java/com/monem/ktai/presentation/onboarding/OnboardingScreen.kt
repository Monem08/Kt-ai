package com.monem.ktai.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monem.ktai.presentation.common.components.KtAIButton
import com.monem.ktai.presentation.common.components.KtAIOutlinedButton
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary
import com.monem.ktai.presentation.common.theme.TextTertiary
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

private val pages = listOf(
    OnboardingPage(
        icon = Icons.Default.Code,
        title = "AI Coding Agent",
        description = "Kt AI is not just a chatbot. It's a coding agent that can create, edit, and fix your Android/Kotlin code directly.",
    ),
    OnboardingPage(
        icon = Icons.Default.Folder,
        title = "Work Inside Your Project",
        description = "Select your project folder and let AI work with your actual files. Preview diffs before applying any changes.",
    ),
    OnboardingPage(
        icon = Icons.Default.Chat,
        title = "Smart Code Assistance",
        description = "Generate Compose screens, fix Gradle errors, refactor code, and more. All from your phone.",
    ),
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(24.dp),
    ) {
        Spacer(Modifier.height(48.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            OnboardingPageContent(pages[page])
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Primary else TextTertiary,
                        ),
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        if (pagerState.currentPage == pages.size - 1) {
            KtAIButton(text = "Get Started", onClick = onNavigateToLogin)
        } else {
            KtAIButton(
                text = "Next",
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
            )
            Spacer(Modifier.height(12.dp))
            KtAIOutlinedButton(text = "Skip", onClick = onNavigateToLogin)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        androidx.compose.material3.Icon(
            imageVector = page.icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(80.dp),
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}
