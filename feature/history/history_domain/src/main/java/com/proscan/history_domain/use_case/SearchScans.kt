package com.proscan.history_domain.use_case

import com.proscan.core.domain.model.ScanResult

class SearchScans {
    operator fun invoke(scans: List<ScanResult>, query: String): List<ScanResult> {
        if (query.isBlank()) return scans
        val lower = query.lowercase()
        return scans.filter { scan ->
            scan.content.lowercase().contains(lower) ||
            scan.type.name.lowercase().contains(lower)
        }
    }
}
