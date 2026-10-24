package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = TextOnAccent,
    primaryContainer = CrimsonPrimaryVariant,
    onPrimaryContainer = TextPrimary,
    secondary = CrimsonSecondary,
    onSecondary = TextOnAccent,
    secondaryContainer = BurgundySurfaceContainer,
    onSecondaryContainer = TextSecondary,
    tertiary = AccentGold,
    onTertiary = Color.Black,
    background = BurgundyBackground,
    onBackground = TextPrimary,
    surface = BurgundySurface,
    onSurface = TextPrimary,
    surfaceVariant = BurgundySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BurgundyOutline,
    outlineVariant = BurgundyOutlineVariant,
    error = StatusError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // ZX SplitPlay enforces a gaming dark burgundy theme
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
