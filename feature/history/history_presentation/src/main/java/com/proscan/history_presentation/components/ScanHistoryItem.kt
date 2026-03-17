package com.proscan.history_presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.proscan.core.domain.model.ScanResult
import com.proscan.core.domain.model.ScanSource
import com.proscan.core.domain.model.ScanType
import com.proscan.core_ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScanHistoryItem(
    scan: ScanResult,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeStr = formatTime(scan.createdAt)
    val (bgColor, iconColor) = getTypeColors(scan.type)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type icon circle
            Surface(
                modifier = Modifier.size(40.dp).clip(CircleShape),
                color = bgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getTypeIcon(scan.type),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (scan.source == ScanSource.GENERATED) {
                        Surface(
                            color = Indigo100,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Generat",
                                style = MaterialTheme.typography.labelSmall,
                                color = Indigo600,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Actions
            Row {
                IconButton(onClick = onPin, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (scan.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                        contentDescription = "Pin",
                        tint = if (scan.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Șterge",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - millis
    val minutes = diff / 60_000

    return when {
        diff < 60_000 -> "Acum"
        minutes < 60 -> "$minutes min"
        else -> {
            val itemCal = Calendar.getInstance().apply { timeInMillis = millis }
            val todayCal = Calendar.getInstance()
            val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            when {
                itemCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                itemCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR) ->
                    "Azi, ${sdf.format(Date(millis))}"
                itemCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
                itemCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR) ->
                    "Ieri, ${sdf.format(Date(millis))}"
                else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(millis))
            }
        }
    }
}

@Composable
private fun getTypeColors(type: ScanType): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    return when (type) {
        ScanType.URL -> Pair(Blue100, Blue500)
        ScanType.TEXT -> Pair(Pink100, Pink500)
        ScanType.PHONE -> Pair(Orange100, Orange500)
        ScanType.EMAIL -> Pair(Indigo100, Indigo600)
        ScanType.SMS -> Pair(Cyan100, Cyan500)
        ScanType.WIFI -> Pair(Green100, Green500)
        ScanType.CONTACT -> Pair(Purple100, Purple500)
        ScanType.CALENDAR -> Pair(Rose100, Rose500)
        ScanType.LOCATION -> Pair(Amber100, Amber500)
        ScanType.UNKNOWN -> Pair(Slate100, Slate600)
    }
}

private fun getTypeIcon(type: ScanType) = when (type) {
    ScanType.URL -> Icons.Default.Link
    ScanType.TEXT -> Icons.Default.TextFields
    ScanType.PHONE -> Icons.Default.Phone
    ScanType.EMAIL -> Icons.Default.Email
    ScanType.SMS -> Icons.Default.Sms
    ScanType.WIFI -> Icons.Default.Wifi
    ScanType.CONTACT -> Icons.Default.Person
    ScanType.CALENDAR -> Icons.Default.CalendarMonth
    ScanType.LOCATION -> Icons.Default.LocationOn
    ScanType.UNKNOWN -> Icons.Default.QrCode
}
