package com.proscan.core_ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system fonts as fallback (custom fonts would need to be added to res/font)
val OutfitFamily = FontFamily.Default
val InterFamily = FontFamily.Default

val ProScanTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 57.sp,
        letterSpacing = (-0.02).sp
    ),
    displayMedium = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 45.sp,
        letterSpacing = (-0.02).sp
    ),
    displaySmall = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = (-0.02).sp
    ),
    titleLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
    )
)
