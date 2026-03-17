package com.proscan.history_presentation

import com.proscan.core.domain.model.ScanResult

sealed class HistoryEvent {
    data class Search(val query: String) : HistoryEvent()
    data class Delete(val id: String) : HistoryEvent()
    data class TogglePin(val id: String) : HistoryEvent()
    object Export : HistoryEvent()
    data class OpenDetail(val scan: ScanResult) : HistoryEvent()
    object CloseDetail : HistoryEvent()
}
