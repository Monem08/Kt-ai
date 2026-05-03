package com.monem.ktai.presentation.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = Accent,
    onSecondary = Color.Black,
    tertiary = AccentGreen,
    background = Surface,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = AccentRed,
    onError = Color.White,
)

@Composable
fun KtAITheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false,
        )
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = KtAITypography,
        content = content,
    )
}
