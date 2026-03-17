package com.proscan.scanner_presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScannerControls(
    flashEnabled: Boolean,
    isBatchMode: Boolean,
    zoomLevel: Float,
    onFlashToggle: () -> Unit,
    onBatchToggle: () -> Unit,
    onFlipCamera: () -> Unit,
    onClose: () -> Unit,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Zoom slider
        ZoomSlider(
            zoomLevel = zoomLevel,
            onZoomChange = onZoomChange
        )

        // Control buttons with labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                icon = if (flashEnabled) Icons.Filled.FlashOn else Icons.Outlined.FlashOff,
                label = if (flashEnabled) "Flash an" else "Flash",
                onClick = onFlashToggle,
                isActive = flashEnabled
            )
            ControlButton(
                icon = Icons.Filled.QrCodeScanner,
                label = if (isBatchMode) "Lot activ" else "Mod lot",
                onClick = onBatchToggle,
                isActive = isBatchMode
            )
            ControlButton(
                icon = Icons.Filled.FlipCameraAndroid,
                label = "Întoarce",
                onClick = onFlipCamera
            )
            ControlButton(
                icon = Icons.Filled.Close,
                label = "Închide",
                onClick = onClose
            )
        }
    }
}

@Composable
private fun ZoomSlider(
    zoomLevel: Float,
    onZoomChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ZoomOut,
            contentDescription = "Zoom out",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
        )
        Slider(
            value = zoomLevel,
            onValueChange = onZoomChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.25f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
        Icon(
            imageVector = Icons.Filled.ZoomIn,
            contentDescription = "Zoom in",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isActive) Color.White.copy(alpha = 0.3f)
                    else Color.Black.copy(alpha = 0.4f)
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}
