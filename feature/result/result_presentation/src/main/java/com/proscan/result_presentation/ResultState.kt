package com.proscan.result_presentation

import com.proscan.core.domain.model.ScanResult
import com.proscan.result_domain.model.ScanAction

data class ResultState(
    val scanResult: ScanResult? = null,
    val actions: List<ScanAction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
