package com.proscan.generator_presentation.components

import android.graphics.Bitmap
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*

@Composable
fun QrPreviewCard(
    bitmap: Bitmap,
    content: String,
    onShare: () -> Unit,
    onCopy: () -> Unit,
    onSave: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // QR image
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code generat",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Content preview pill
            if (content.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Action buttons row — vertical cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    label = "Copiază",
                    fallbackIcon = Icons.Default.ContentCopy,
                    lottieRes = null, // drop ic_anim_copy.json in res/raw/ to enable
                    isPrimary = false,
                    onClick = onCopy,
                    modifier = Modifier.weight(1f)
                )

                if (onSave != null) {
                    ActionCard(
                        label = "Salvează",
                        fallbackIcon = Icons.Default.Download,
                        lottieRes = null, // drop ic_anim_save.json in res/raw/ to enable
                        isPrimary = false,
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    )
                }

                ActionCard(
                    label = "Distribuie",
                    fallbackIcon = Icons.Default.Share,
                    lottieRes = null, // drop ic_anim_share.json in res/raw/ to enable
                    isPrimary = true,
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    label: String,
    fallbackIcon: ImageVector,
    @RawRes lottieRes: Int?,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var triggerAnim by remember { mutableStateOf(false) }

    val containerColor = if (isPrimary)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val contentColor = if (isPrimary)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        onClick = {
            triggerAnim = true
            onClick()
        },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (lottieRes != null) {
                LottieActionIcon(
                    lottieRes = lottieRes,
                    trigger = triggerAnim,
                    onAnimEnd = { triggerAnim = false },
                    tintColor = contentColor
                )
            } else {
                Icon(
                    imageVector = fallbackIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LottieActionIcon(
    @RawRes lottieRes: Int,
    trigger: Boolean,
    onAnimEnd: () -> Unit,
    tintColor: Color
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = trigger,
        iterations = 1,
        restartOnPlay = true
    )

    LaunchedEffect(trigger, progress) {
        if (trigger && progress >= 0.99f) onAnimEnd()
    }

    LottieAnimation(
        composition = composition,
        progress = { if (trigger) progress else 0f },
        modifier = Modifier.size(26.dp)
    )
}
