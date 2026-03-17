package com.proscan.result_presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.proscan.result_domain.model.ScanAction

@Composable
fun ActionButton(
    action: ScanAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = actionIcon(action),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(action.label)
    }
}

private fun actionIcon(action: ScanAction): ImageVector = when (action) {
    is ScanAction.OpenUrl      -> Icons.Outlined.OpenInBrowser
    is ScanAction.Dial         -> Icons.Outlined.Phone
    is ScanAction.SendEmail    -> Icons.Outlined.Email
    is ScanAction.SendSms      -> Icons.Outlined.Sms
    is ScanAction.OpenMap      -> Icons.Outlined.Map
    is ScanAction.SaveContact  -> Icons.Outlined.PersonAdd
    is ScanAction.AddCalendar  -> Icons.Outlined.CalendarMonth
}
