package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

// Clean, calm Light Color Scheme with Deep Forest Green
private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = ForestGreenOnPrimary,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = ForestGreenOnContainer,
    secondary = ForestGreenSecondary,
    onSecondary = ForestGreenOnSecondary,
    secondaryContainer = ForestGreenSecondaryContainer,
    onSecondaryContainer = ForestGreenOnSecondaryContainer,
    tertiary = ForestGreenTertiary,
    onTertiary = ForestGreenOnTertiary,
    tertiaryContainer = ForestGreenTertiaryContainer,
    onTertiaryContainer = ForestGreenOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

/**
 * Main application theme enforcing:
 * 1. Deep Forest Green color palette with high-contrast, calm light surfaces.
 * 2. Noto Sans Bengali typography with strict scale.
 * 3. Phone system font scale clamped to maximum 1.15f to maintain compact card layouts.
 */
@Composable
fun HardwareManagerTheme(
    content: @Composable () -> Unit
) {
    val currentDensity = LocalDensity.current
    val clampedDensity = remember(currentDensity.density, currentDensity.fontScale) {
        Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale.coerceAtMost(1.15f)
        )
    }

    CompositionLocalProvider(LocalDensity provides clampedDensity) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
