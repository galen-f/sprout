package com.example.sprout.ui.carehistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sprout.domain.model.CareEventType
import com.example.sprout.ui.components.CareEventListItem
import com.example.sprout.ui.components.EmptyState
import com.example.sprout.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: CareHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (val s = uiState) {
                            is CareHistoryUiState.Content -> "${s.plantName} history"
                            else -> "Care history"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is CareHistoryUiState.Loading -> LoadingIndicator(modifier = Modifier.padding(paddingValues))
            is CareHistoryUiState.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item {
                                FilterChip(
                                    selected = state.selectedFilter == null,
                                    onClick = { viewModel.setFilter(null) },
                                    label = { Text("All") },
                                )
                            }
                            items(CareEventType.entries) { type ->
                                FilterChip(
                                    selected = state.selectedFilter == type,
                                    onClick = { viewModel.setFilter(if (state.selectedFilter == type) null else type) },
                                    label = { Text(type.label()) },
                                )
                            }
                        }
                    }
                    if (state.events.isEmpty()) {
                        item {
                            EmptyState(
                                title = "No events",
                                subtitle = "No care events logged yet",
                            )
                        }
                    } else {
                        items(state.events, key = { it.id }) { event ->
                            CareEventListItem(event = event)
                        }
                    }
                }
            }
        }
    }
}

private fun CareEventType.label(): String = when (this) {
    CareEventType.WATERED -> "Watered"
    CareEventType.FERTILIZED -> "Fertilized"
    CareEventType.PH_MEASURED -> "pH"
    CareEventType.REPOTTED -> "Repotted"
    CareEventType.NOTE -> "Note"
}
