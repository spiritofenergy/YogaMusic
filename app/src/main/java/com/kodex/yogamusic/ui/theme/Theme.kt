package com.kodex.yogamusic.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColors(
    primary = ThemeColors.Dark.primary,
    surface = ThemeColors.Dark.surface,
    onPrimary = ThemeColors.Dark.text,
    background = ThemeColors.Dark.background
)

@Composable
fun JakeBoxComposeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = Typography,
        content = content,
        colors = DarkColorScheme
    )
}