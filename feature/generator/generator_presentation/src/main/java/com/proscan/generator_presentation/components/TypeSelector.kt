package com.proscan.generator_presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.proscan.core_ui.theme.Indigo600
import com.proscan.generator_presentation.GeneratorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelector(
    selectedType: GeneratorType,
    onTypeSelected: (GeneratorType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GeneratorType.values().forEach { type ->
            val selected = selectedType == type
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
                    selectedContainerColor = Indigo600.copy(alpha = 0.12f),
                    selectedLabelColor = Indigo600,
                    selectedLeadingIconColor = Indigo600
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    selectedBorderColor = Indigo600.copy(alpha = 0.45f),
                    selectedBorderWidth = 1.5.dp
                )
            )
        }
    }
}

private fun typeIcon(type: GeneratorType): ImageVector = when (type) {
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
