package com.proscan.result_presentation.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proscan.core.domain.model.ScanResult
import com.proscan.core.domain.model.ScanType
import com.proscan.core_ui.components.TypeBadge
import com.proscan.core_ui.theme.Green500
import com.proscan.core_ui.theme.Slate200

@Composable
fun ResultCard(
    scan: ScanResult,
    modifier: Modifier = Modifier
) {
    val isUrl = scan.type == ScanType.URL
    val domain = remember(scan.content) {
        if (isUrl) try {
            Uri.parse(scan.content).host?.removePrefix("www.") ?: scan.content
        } catch (e: Exception) { scan.content }
        else null
    }

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

            if (isUrl && domain != null) {
                Text(
                    text = domain,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = scan.content,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = scan.content,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
