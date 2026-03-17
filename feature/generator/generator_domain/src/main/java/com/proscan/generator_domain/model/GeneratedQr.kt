package com.proscan.generator_domain.model

import android.graphics.Bitmap
import com.proscan.core.domain.model.ScanType

data class GeneratedQr(
    val content: String,
    val type: ScanType,
    val bitmap: Bitmap?
)
