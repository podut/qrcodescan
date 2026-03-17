package com.proscan.history_domain.use_case

import com.proscan.core.domain.model.ScanResult
import com.proscan.history_domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetScanHistory(
    private val repository: HistoryRepository
) {
    operator fun invoke(): Flow<List<ScanResult>> {
        return repository.getAllScans().map { scans ->
            scans.sortedWith(
                compareByDescending<ScanResult> { it.isPinned }
                    .thenByDescending { it.createdAt }
            )
        }
    }
}
