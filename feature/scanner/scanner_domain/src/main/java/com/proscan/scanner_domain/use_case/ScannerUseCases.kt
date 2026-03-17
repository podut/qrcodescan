package com.proscan.scanner_domain.use_case

data class ScannerUseCases(
    val processScanResult: ProcessScanResult,
    val saveScanToHistory: SaveScanToHistory
)
