package com.proscan.history_presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proscan.history_presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onScanClick: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.showDetailSheet && state.selectedScan != null) {
        ScanDetailBottomSheet(
            scan = state.selectedScan!!,
            onDismiss = { viewModel.onEvent(HistoryEvent.CloseDetail) },
            onNavigateToResult = {
                viewModel.onEvent(HistoryEvent.CloseDetail)
                onScanClick(state.selectedScan!!.id)
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        StatsBar(stats = state.stats)

        SearchBar(
            query = state.searchQuery,
            onQueryChange = { viewModel.onEvent(HistoryEvent.Search(it)) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.filteredScans.isEmpty()) {
            EmptyHistory(hasSearchQuery = state.searchQuery.isNotBlank())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(state.filteredScans, key = { it.id }) { scan ->
                    ScanHistoryItem(
                        scan = scan,
                        onPin = { viewModel.onEvent(HistoryEvent.TogglePin(scan.id)) },
                        onDelete = { viewModel.onEvent(HistoryEvent.Delete(scan.id)) },
                        onClick = { viewModel.onEvent(HistoryEvent.OpenDetail(scan)) }
                    )
                }
            }
        }
    }
}
