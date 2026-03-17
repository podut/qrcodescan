package com.proscan.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proscan.core_ui.theme.*

@Composable
fun PremiumBadge(
    isPro: Boolean,
    modifier: Modifier = Modifier
) {
    val (gradient, label) = if (isPro) {
        Pair(
            Brush.horizontalGradient(listOf(Indigo600, Violet500)),
            "PRO"
        )
    } else {
        Pair(
            Brush.horizontalGradient(listOf(Slate400, Slate600)),
            "FREE"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}
