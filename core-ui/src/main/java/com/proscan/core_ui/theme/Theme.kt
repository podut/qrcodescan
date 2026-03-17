package com.proscan.core_ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.proscan.core.domain.model.AppTheme

// ── Shared dark background tokens ─────────────────────────────────────────────
private val DarkBg            = Color(0xFF0F172A)
private val DarkSurface       = Color(0xFF1E293B)
private val DarkSurfaceVariant= Color(0xFF334155)
private val DarkOnSurface     = Color(0xFFF1F5F9)
private val DarkOnSurfaceVar  = Color(0xFF94A3B8)
private val DarkOutline       = Color(0xFF475569)

// ── Per-theme color pairs (light primary / light container / dark primary / dark container) ──
private data class ThemeColors(
    val lightPrimary: Color,
    val lightContainer: Color,
    val lightOnContainer: Color,
    val darkPrimary: Color,
    val darkContainer: Color
)

private val themeColors = mapOf(
    AppTheme.INDIGO  to ThemeColors(Color(0xFF6366F1), Color(0xFFE0E7FF), Color(0xFF4F46E5), Color(0xFF818CF8), Color(0xFF3730A3)),
    AppTheme.PURPLE  to ThemeColors(Color(0xFF7C3AED), Color(0xFFEDE9FE), Color(0xFF6D28D9), Color(0xFFA78BFA), Color(0xFF5B21B6)),
    AppTheme.EMERALD to ThemeColors(Color(0xFF10B981), Color(0xFFD1FAE5), Color(0xFF047857), Color(0xFF34D399), Color(0xFF065F46)),
    AppTheme.ROSE    to ThemeColors(Color(0xFFF43F5E), Color(0xFFFFE4E6), Color(0xFFE11D48), Color(0xFFFB7185), Color(0xFF9F1239)),
    AppTheme.OCEAN   to ThemeColors(Color(0xFF0EA5E9), Color(0xFFE0F2FE), Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF0C4A6E)),
    AppTheme.AMBER   to ThemeColors(Color(0xFFF59E0B), Color(0xFFFEF3C7), Color(0xFFD97706), Color(0xFFFCD34D), Color(0xFF78350F))
)

fun getColorScheme(appTheme: AppTheme, darkMode: Boolean) = themeColors[appTheme]!!.let { t ->
    if (darkMode) darkColorScheme(
        primary             = t.darkPrimary,
        onPrimary           = DarkBg,
        primaryContainer    = t.darkContainer,
        onPrimaryContainer  = Color.White,
        secondary           = Color(0xFFFB7185),
        onSecondary         = DarkBg,
        secondaryContainer  = Color(0xFF9F1239),
        onSecondaryContainer= Color.White,
        tertiary            = Color(0xFFA78BFA),
        onTertiary          = DarkBg,
        background          = DarkBg,
        onBackground        = DarkOnSurface,
        surface             = DarkSurface,
        onSurface           = DarkOnSurface,
        surfaceVariant      = DarkSurfaceVariant,
        onSurfaceVariant    = DarkOnSurfaceVar,
        outline             = DarkOutline,
        outlineVariant      = DarkSurfaceVariant
    ) else lightColorScheme(
        primary             = t.lightPrimary,
        onPrimary           = Color.White,
        primaryContainer    = t.lightContainer,
        onPrimaryContainer  = t.lightOnContainer,
        secondary           = Rose500,
        onSecondary         = Color.White,
        secondaryContainer  = Rose100,
        onSecondaryContainer= Rose500,
        tertiary            = Violet500,
        onTertiary          = Color.White,
        tertiaryContainer   = Purple100,
        onTertiaryContainer = Purple500,
        background          = Slate50,
        onBackground        = Slate800,
        surface             = Color.White,
        onSurface           = Slate800,
        surfaceVariant      = Slate100,
        onSurfaceVariant    = Slate600,
        outline             = Slate200,
        outlineVariant      = Slate200
    )
}

@Composable
fun ProScanTheme(
    appTheme: AppTheme = AppTheme.INDIGO,
    darkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = getColorScheme(appTheme, darkMode),
        typography = ProScanTypography,
        shapes = ProScanShapes,
        content = content
    )
}
