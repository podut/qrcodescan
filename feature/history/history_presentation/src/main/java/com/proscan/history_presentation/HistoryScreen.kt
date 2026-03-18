package com.proscan.history_presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.AutoMirrored.Outlined.List
import androidx.compose.material.icons.AutoMirrored.Outlined.Segment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proscan.core.domain.model.ScanType
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

        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(HistoryEvent.Search(it)) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.onEvent(HistoryEvent.ToggleGrouping) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (state.isGrouped) List else Segment,
                    contentDescription = if (state.isGrouped) "Vizualizare listă" else "Grupează după tip",
                    tint = if (state.isGrouped) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.filteredScans.isEmpty()) {
            EmptyHistory(hasSearchQuery = state.searchQuery.isNotBlank())
        } else if (state.isGrouped) {
            GroupedHistoryList(
                scans = state.filteredScans,
                onPin = { viewModel.onEvent(HistoryEvent.TogglePin(it)) },
                onDelete = { viewModel.onEvent(HistoryEvent.Delete(it)) },
                onClick = { viewModel.onEvent(HistoryEvent.OpenDetail(it)) }
            )
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

@Composable
private fun GroupedHistoryList(
    scans: List<com.proscan.core.domain.model.ScanResult>,
    onPin: (String) -> Unit,
    onDelete: (String) -> Unit,
    onClick: (com.proscan.core.domain.model.ScanResult) -> Unit
) {
    val grouped = scans.groupBy { it.type }
    val orderedTypes = ScanType.values().filter { grouped.containsKey(it) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        orderedTypes.forEach { type ->
            val items = grouped[type] ?: return@forEach
            item(key = "header_${type.name}") {
                Text(
                    text = type.displayName(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(items, key = { it.id }) { scan ->
                ScanHistoryItem(
                    scan = scan,
                    onPin = { onPin(scan.id) },
                    onDelete = { onDelete(scan.id) },
                    onClick = { onClick(scan) }
                )
            }
        }
    }
}

private fun ScanType.displayName(): String = when (this) {
    ScanType.URL      -> "URL"
    ScanType.TEXT     -> "Text"
    ScanType.PHONE    -> "Telefon"
    ScanType.EMAIL    -> "Email"
    ScanType.SMS      -> "SMS"
    ScanType.WIFI     -> "WiFi"
    ScanType.CONTACT  -> "Contact"
    ScanType.CALENDAR -> "Calendar"
    ScanType.LOCATION -> "Locație"
    ScanType.UNKNOWN  -> "Necunoscut"
}
