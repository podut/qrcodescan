package com.proscan.generator_presentation.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.proscan.generator_presentation.GeneratorType

private const val TAG = "TypeSelector"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelector(
    selectedType: GeneratorType,
    onTypeSelected: (GeneratorType) -> Unit,
    isGrid: Boolean,
    onToggleView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val types = GeneratorType.values().toList()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tip cod",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(onClick = {
                Log.d(TAG, "Toggle button clicked, isGrid=$isGrid -> switching to ${!isGrid}")
                onToggleView()
            }, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (isGrid) Icons.Outlined.ViewStream else Icons.Outlined.GridView,
                    contentDescription = if (isGrid) "Vizualizare listă" else "Vizualizare grid",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isGrid) {
            Log.d(TAG, "Rendering grid view, types count=${types.size}")
            val rows = (types.size + 3) / 4
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((rows * 72).dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(types) { type ->
                    Log.d(TAG, "GridTypeItem: type=$type")
                    GridTypeItem(
                        type = type,
                        selected = type == selectedType,
                        onClick = { onTypeSelected(type) }
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { type ->
                    val selected = type == selectedType
                    FilterChip(
                        selected = selected,
                        onClick = { onTypeSelected(type) },
                        leadingIcon = {
                            Icon(
                                imageVector = typeIcon(type),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text(type.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primary.copy(alpha = 0.12f),
                            selectedLabelColor = primary,
                            selectedLeadingIconColor = primary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = primary.copy(alpha = 0.45f),
                            selectedBorderWidth = 1.5.dp
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GridTypeItem(
    type: GeneratorType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "GridTypeItem composable: type=$type selected=$selected")
    val primary = MaterialTheme.colorScheme.primary
    val containerColor = if (selected)
        primary.copy(alpha = 0.12f)
    else
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = if (selected)
        primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = containerColor,
        shape = RoundedCornerShape(10.dp),
        border = if (selected) BorderStroke(1.5.dp, primary.copy(alpha = 0.45f)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = typeIcon(type),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = type.label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun typeIcon(type: GeneratorType): ImageVector = when (type) {
    GeneratorType.WIFI        -> Icons.Outlined.Wifi
    GeneratorType.TEXT        -> Icons.Outlined.Notes
    GeneratorType.URL         -> Icons.Outlined.Link
    GeneratorType.PHONE       -> Icons.Outlined.Phone
    GeneratorType.EMAIL       -> Icons.Outlined.Email
    GeneratorType.SMS         -> Icons.Outlined.Sms
    GeneratorType.CONTACT     -> Icons.Outlined.Person
    GeneratorType.CALENDAR    -> Icons.Outlined.CalendarMonth
    GeneratorType.LOCATION    -> Icons.Outlined.LocationOn
    GeneratorType.CLIPBOARD   -> Icons.Outlined.ContentPaste
    GeneratorType.EAN_13      -> Icons.Outlined.QrCodeScanner
    GeneratorType.UPC_E       -> Icons.Outlined.QrCodeScanner
    GeneratorType.UPC_A       -> Icons.Outlined.QrCodeScanner
    GeneratorType.CODE_39     -> Icons.Outlined.QrCodeScanner
    GeneratorType.CODE_93     -> Icons.Outlined.QrCodeScanner
    GeneratorType.CODE_128    -> Icons.Outlined.QrCodeScanner
    GeneratorType.ITF         -> Icons.Outlined.QrCodeScanner
    GeneratorType.PDF_417     -> Icons.Outlined.QrCode2
    GeneratorType.CODABAR     -> Icons.Outlined.QrCodeScanner
    GeneratorType.DATA_MATRIX -> Icons.Outlined.QrCode2
    GeneratorType.AZTEC       -> Icons.Outlined.QrCode2
}
