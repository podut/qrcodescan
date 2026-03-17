package com.proscan.history_domain.use_case

import com.proscan.history_domain.repository.HistoryRepository

class DeleteScan(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteScan(id)
    }
}
