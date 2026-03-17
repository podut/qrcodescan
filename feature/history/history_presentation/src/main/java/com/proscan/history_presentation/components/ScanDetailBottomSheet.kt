package com.proscan.history_presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.proscan.core.domain.model.ScanResult
import com.proscan.core_ui.components.TypeBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDetailBottomSheet(
    scan: ScanResult,
    onDismiss: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TypeBadge(scanType = scan.type)
                Text(
                    text = formatTime(scan.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = scan.content,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )
            }

            Button(
                onClick = onNavigateToResult,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Deschide Rezultat")
            }
        }
    }
}
