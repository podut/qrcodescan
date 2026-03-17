package com.proscan.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proscan.core.domain.model.ScanType
import com.proscan.core_ui.theme.*

@Composable
fun TypeBadge(
    scanType: ScanType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (scanType) {
        ScanType.URL -> Triple(Blue100, Blue500, "URL")
        ScanType.TEXT -> Triple(Pink100, Pink500, "TEXT")
        ScanType.PHONE -> Triple(Orange100, Orange500, "TEL")
        ScanType.EMAIL -> Triple(Indigo100, Indigo600, "EMAIL")
        ScanType.SMS -> Triple(Cyan100, Cyan500, "SMS")
        ScanType.WIFI -> Triple(Green100, Green500, "WIFI")
        ScanType.CONTACT -> Triple(Purple100, Purple500, "CONTACT")
        ScanType.CALENDAR -> Triple(Rose100, Rose500, "CALENDAR")
        ScanType.LOCATION -> Triple(Amber100, Amber500, "LOC")
        ScanType.UNKNOWN -> Triple(Slate100, Slate600, "TEXT")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
