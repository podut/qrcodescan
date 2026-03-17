package com.proscan.generator_presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proscan.core_ui.components.GradientButton

@Composable
fun GenerateButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        GradientButton(
            text = "Generează QR",
            onClick = onClick,
            modifier = modifier.fillMaxWidth()
        )
    }
}
