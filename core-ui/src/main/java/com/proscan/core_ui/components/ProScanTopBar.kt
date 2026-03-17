package com.proscan.core_ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proscan.core_ui.theme.OutfitFamily
import com.proscan.core_ui.theme.Slate600
import com.proscan.core_ui.theme.Slate800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProScanTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "ProScan",
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Slate800
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Setări",
                    tint = Slate600
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        modifier = modifier
    )
}
