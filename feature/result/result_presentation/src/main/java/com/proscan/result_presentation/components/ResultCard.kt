package com.proscan.result_presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.proscan.core.domain.model.ScanResult
import com.proscan.core_ui.components.TypeBadge
import com.proscan.core_ui.theme.Green500
import com.proscan.core_ui.theme.Slate200

@Composable
fun ResultCard(
    scan: ScanResult,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TypeBadge(scanType = scan.type)
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Scanat cu succes",
                    tint = Green500,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = scan.content,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
