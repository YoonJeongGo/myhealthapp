package com.healthlog.myapplication1.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val darkColorScheme = darkColorScheme(
    primary          = GreenAccent,
    onPrimary        = Color(0xFF0E1A14),
    primaryContainer = GreenDim2,
    onPrimaryContainer = GreenAccent,

    secondary        = InfoBlue,
    onSecondary      = Color.White,

    tertiary         = Purple,
    onTertiary       = Color.White,

    background       = BgBase,
    onBackground     = TextPrimary,

    surface          = BgSurface,
    onSurface        = TextPrimary,

    surfaceVariant   = BgCard,
    onSurfaceVariant = TextSecondary,

    error            = Danger,
    onError          = Color.White,

    outline          = Border2,
    outlineVariant   = Border
)

@Composable
fun HealthLogTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
