package com.proscan.scanner_presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.proscan.core_ui.theme.Indigo600
import com.proscan.core_ui.theme.Dimensions

@Composable
fun ScannerOverlay(
    isBatchMode: Boolean,
    batchScanCount: Int,
    lastScanned: String?,
    modifier: Modifier = Modifier
) {
    val scanLineAnimation = rememberInfiniteTransition(label = "scanLine")
    val scanLineY by scanLineAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLineY"
    )

    val cutoutSize = Dimensions.scannerCutoutSize

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cutoutPx = cutoutSize.toPx()
            val cutoutLeft = (size.width - cutoutPx) / 2f
            val cutoutTop = (size.height - cutoutPx) / 2f
            val cornerRadius = 24.dp.toPx()

            // Dark overlay
            drawRect(color = Color.Black.copy(alpha = 0.7f))

            // Clear cutout (transparent hole)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(cutoutLeft, cutoutTop),
                size = Size(cutoutPx, cutoutPx),
                cornerRadius = CornerRadius(cornerRadius),
                blendMode = BlendMode.Clear
            )

            // Corner accents
            val accentLength = 24.dp.toPx()
            val strokeWidth = 3.dp.toPx()
            val accentColor = Color(0xFF6366F1) // Indigo600

            // Top-left
            drawLine(accentColor, Offset(cutoutLeft, cutoutTop + cornerRadius), Offset(cutoutLeft, cutoutTop + accentLength), strokeWidth)
            drawLine(accentColor, Offset(cutoutLeft + cornerRadius, cutoutTop), Offset(cutoutLeft + accentLength, cutoutTop), strokeWidth)
            // Top-right
            drawLine(accentColor, Offset(cutoutLeft + cutoutPx, cutoutTop + cornerRadius), Offset(cutoutLeft + cutoutPx, cutoutTop + accentLength), strokeWidth)
            drawLine(accentColor, Offset(cutoutLeft + cutoutPx - cornerRadius, cutoutTop), Offset(cutoutLeft + cutoutPx - accentLength, cutoutTop), strokeWidth)
            // Bottom-left
            drawLine(accentColor, Offset(cutoutLeft, cutoutTop + cutoutPx - cornerRadius), Offset(cutoutLeft, cutoutTop + cutoutPx - accentLength), strokeWidth)
            drawLine(accentColor, Offset(cutoutLeft + cornerRadius, cutoutTop + cutoutPx), Offset(cutoutLeft + accentLength, cutoutTop + cutoutPx), strokeWidth)
            // Bottom-right
            drawLine(accentColor, Offset(cutoutLeft + cutoutPx, cutoutTop + cutoutPx - cornerRadius), Offset(cutoutLeft + cutoutPx, cutoutTop + cutoutPx - accentLength), strokeWidth)
            drawLine(accentColor, Offset(cutoutLeft + cutoutPx - cornerRadius, cutoutTop + cutoutPx), Offset(cutoutLeft + cutoutPx - accentLength, cutoutTop + cutoutPx), strokeWidth)

            // Scan line
            val scanY = cutoutTop + (cutoutPx * scanLineY)
            drawLine(
                color = Color(0xFF6366F1).copy(alpha = 0.8f),
                start = Offset(cutoutLeft + cornerRadius, scanY),
                end = Offset(cutoutLeft + cutoutPx - cornerRadius, scanY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
