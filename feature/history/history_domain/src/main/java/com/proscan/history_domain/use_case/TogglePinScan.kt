package com.proscan.history_domain.use_case

import com.proscan.history_domain.repository.HistoryRepository

class TogglePinScan(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(id: String) {
        repository.togglePin(id)
    }
}
