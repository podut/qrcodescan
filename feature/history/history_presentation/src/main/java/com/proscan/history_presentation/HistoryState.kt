package com.proscan.history_presentation

import com.proscan.core.domain.model.ScanResult
import com.proscan.history_domain.use_case.ScanStats

data class HistoryState(
    val allScans: List<ScanResult> = emptyList(),
    val filteredScans: List<ScanResult> = emptyList(),
    val searchQuery: String = "",
    val stats: ScanStats = ScanStats(0, 0, emptyMap()),
    val isLoading: Boolean = true,
    val selectedScan: ScanResult? = null,
    val showDetailSheet: Boolean = false
)
