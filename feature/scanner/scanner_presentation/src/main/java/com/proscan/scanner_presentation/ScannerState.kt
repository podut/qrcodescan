package com.proscan.scanner_presentation

data class ScannerState(
    val isScanning: Boolean = false,
    val isBatchMode: Boolean = false,
    val batchScanCount: Int = 0,
    val lastScanned: String? = null,
    val flashEnabled: Boolean = false,
    val facingFront: Boolean = false,
    val hasPermission: Boolean = false,
    val isLoading: Boolean = false,
    val zoomLevel: Float = 0f
)
