package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameZoneColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = NeonPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0E4A5A),
    onSecondaryContainer = NeonCyanLight,
    tertiary = NeonPink,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderGlow,
    outlineVariant = Color(0xFF1E293B),
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun GameZoneTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameZoneColorScheme,
        typography = Typography,
        content = content
    )
}
