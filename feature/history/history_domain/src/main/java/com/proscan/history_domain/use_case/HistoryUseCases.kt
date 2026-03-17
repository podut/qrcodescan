package com.proscan.history_domain.use_case

data class HistoryUseCases(
    val getScanHistory: GetScanHistory,
    val deleteScan: DeleteScan,
    val togglePinScan: TogglePinScan,
    val searchScans: SearchScans,
    val getScanStats: GetScanStats,
    val exportToCSV: ExportToCSV
)
