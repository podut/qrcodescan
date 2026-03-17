package com.proscan.scanner_presentation

sealed class ScannerEvent {
    object ToggleFlash : ScannerEvent()
    object ToggleBatch : ScannerEvent()
    object FlipCamera : ScannerEvent()
    object PermissionGranted : ScannerEvent()
    object PermissionDenied : ScannerEvent()
    data class CodeScanned(val value: String, val format: String) : ScannerEvent()
    object Close : ScannerEvent()
    object ResetCooldown : ScannerEvent()
    data class SetZoom(val level: Float) : ScannerEvent()
}
