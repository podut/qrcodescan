package com.proscan.history_presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proscan.history_domain.use_case.HistoryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCases: HistoryUseCases,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        useCases.getScanHistory()
            .onEach { scans ->
                val stats = useCases.getScanStats(scans)
                val filtered = if (_state.value.searchQuery.isBlank()) scans
                else useCases.searchScans(scans, _state.value.searchQuery)
                _state.value = _state.value.copy(
                    allScans = scans,
                    filteredScans = filtered,
                    stats = stats,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.Search -> {
                val filtered = useCases.searchScans(_state.value.allScans, event.query)
                _state.value = _state.value.copy(searchQuery = event.query, filteredScans = filtered)
            }
            is HistoryEvent.Delete -> {
                viewModelScope.launch { useCases.deleteScan(event.id) }
            }
            is HistoryEvent.TogglePin -> {
                viewModelScope.launch { useCases.togglePinScan(event.id) }
            }
            is HistoryEvent.Export -> {
                exportCsv()
            }
            is HistoryEvent.OpenDetail -> {
                _state.value = _state.value.copy(selectedScan = event.scan, showDetailSheet = true)
            }
            is HistoryEvent.CloseDetail -> {
                _state.value = _state.value.copy(selectedScan = null, showDetailSheet = false)
            }
            is HistoryEvent.ToggleGrouping -> {
                _state.value = _state.value.copy(isGrouped = !_state.value.isGrouped)
            }
        }
    }

    private fun exportCsv() {
        viewModelScope.launch {
            val csv = useCases.exportToCSV(_state.value.allScans)
            val file = File(context.cacheDir, "proscan_history.csv")
            file.writeText(csv)
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Export CSV").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
