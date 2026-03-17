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

@Composable
fun ScannerControls(
    flashEnabled: Boolean,
    isBatchMode: Boolean,
    onFlashToggle: () -> Unit,
    onBatchToggle: () -> Unit,
    onFlipCamera: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            icon = if (flashEnabled) Icons.Filled.FlashOn else Icons.Outlined.FlashOff,
            contentDescription = if (flashEnabled) "Flash an" else "Flash oprit",
            onClick = onFlashToggle,
            isActive = flashEnabled
        )
        ControlButton(
            icon = Icons.Filled.QrCodeScanner,
            contentDescription = "Mod lot",
            onClick = onBatchToggle,
            isActive = isBatchMode
        )
        ControlButton(
            icon = Icons.Filled.FlipCameraAndroid,
            contentDescription = "Inversează camera",
            onClick = onFlipCamera
        )
        ControlButton(
            icon = Icons.Filled.Close,
            contentDescription = "Închide",
            onClick = onClose
        )
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isActive: Boolean = false
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
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
