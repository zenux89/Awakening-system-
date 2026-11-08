package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val AwakenDarkColorScheme = darkColorScheme(
    primary = SystemCyan,
    onPrimary = SystemObsidian,
    primaryContainer = SystemSurfaceElevated,
    onPrimaryContainer = SystemCyan,
    secondary = SystemViolet,
    onSecondary = SystemTextPrimary,
    secondaryContainer = SystemVioletDim,
    onSecondaryContainer = SystemTextPrimary,
    tertiary = SystemGold,
    onTertiary = SystemObsidian,
    background = SystemObsidian,
    onBackground = SystemTextPrimary,
    surface = SystemCharcoal,
    onSurface = SystemTextPrimary,
    surfaceVariant = SystemSurface,
    onSurfaceVariant = SystemTextSecondary,
    outline = SystemBorderDim,
    error = SystemCrimson,
    onError = SystemTextPrimary
)

val LocalReducedAnimations = staticCompositionLocalOf { false }

@Composable
fun AwakenSystemTheme(
    reducedAnimations: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalReducedAnimations provides reducedAnimations) {
        MaterialTheme(
            colorScheme = AwakenDarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AwakenSystemTheme(reducedAnimations = false, content = content)
}
