package com.proscan.result_presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Text(action.label)
    }
}
