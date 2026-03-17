package com.proscan.settings_presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.proscan.core_ui.theme.Indigo600
import com.proscan.core_ui.theme.Violet500

@Composable
fun PremiumUpgradeCard(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.horizontalGradient(listOf(Indigo600, Violet500))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                Text(
                    text = "Treci la ProScan Pro",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Scanare în lot (Batch Mode)", "Export CSV", "Personalizare culori").forEach { feature ->
                    Text("• $feature", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                }
            }
            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Indigo600
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Încearcă Pro Gratuit", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
