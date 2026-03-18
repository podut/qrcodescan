package com.proscan.result_presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proscan.result_presentation.UrlSecurityWarning

@Composable
fun UrlSecurityBanner(warnings: List<UrlSecurityWarning>, modifier: Modifier = Modifier) {
    if (warnings.isEmpty()) return

    val containerColor = Color(0xFFFFF3CD)
    val contentColor = Color(0xFF664D00)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Avertisment de securitate",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = contentColor
                )
            }

            warnings.forEach { warning ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text("•", color = contentColor, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = warningText(warning),
                        color = contentColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun warningText(warning: UrlSecurityWarning): String = when (warning) {
    is UrlSecurityWarning.HttpOnly ->
        "Conexiune necriptată (HTTP). Datele tale pot fi interceptate."
    is UrlSecurityWarning.UrlShortener ->
        "Link scurtat (${warning.host}) — destinația reală este ascunsă."
    is UrlSecurityWarning.SuspiciousDomain ->
        "Domeniu cu mulți subdomenii — verifică cu atenție adresa înainte de a continua."
}
