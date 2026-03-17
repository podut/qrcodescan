package com.proscan.result_presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CopyShareRow(
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCopy,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Copiază")
        }
        Button(
            onClick = onShare,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Distribuie")
        }
    }
}
