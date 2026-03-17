package com.proscan.result_presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proscan.core.domain.util.UiEvent
import com.proscan.result_presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onNavigateUp: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateUp -> onNavigateUp()
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rezultat Scanare") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(ResultEvent.NavigateBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Înapoi")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            state.scanResult?.let { scan ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ResultCard(scan = scan)

                    CopyShareRow(
                        onCopy = { viewModel.onEvent(ResultEvent.CopyToClipboard) },
                        onShare = { viewModel.onEvent(ResultEvent.Share) }
                    )

                    if (state.actions.isNotEmpty()) {
                        Text("Acțiuni", style = MaterialTheme.typography.titleMedium)
                        state.actions.forEach { action ->
                            ActionButton(
                                action = action,
                                onClick = { viewModel.onEvent(ResultEvent.ExecuteAction(action)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
