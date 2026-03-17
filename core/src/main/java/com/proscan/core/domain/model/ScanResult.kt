package com.proscan.core.domain.model

import java.util.UUID

data class ScanResult(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val type: ScanType,
    val content: String,
    val format: String = "QR_CODE",
    val source: String = ScanSource.SCANNED,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isSynced: Boolean = false
)

object ScanSource {
    const val SCANNED = "SCANNED"
    const val GENERATED = "GENERATED"
}
