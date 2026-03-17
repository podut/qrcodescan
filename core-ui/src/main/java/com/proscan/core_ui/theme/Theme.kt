package com.proscan.core_ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ProScanColorScheme = lightColorScheme(
    primary = Indigo600,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo700,
    secondary = Rose500,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Rose100,
    onSecondaryContainer = Rose500,
    tertiary = Violet500,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Purple100,
    onTertiaryContainer = Purple500,
    background = Slate50,
    onBackground = Slate800,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Slate800,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate200
)

@Composable
fun ProScanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ProScanColorScheme,
        typography = ProScanTypography,
        shapes = ProScanShapes,
        content = content
    )
}
