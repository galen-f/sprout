package com.example.sprout.ui.plantdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sprout.ui.components.CareEventListItem
import com.example.sprout.ui.components.LoadingIndicator
import com.example.sprout.ui.components.StatusPill
import com.example.sprout.ui.components.WaterButton
import com.example.sprout.ui.theme.SproutCreamDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToCareHistory: (Long) -> Unit,
    onPlantDeleted: () -> Unit,
    viewModel: PlantDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is PlantDetailUiState.Deleted) onPlantDeleted()
    }

    when (val state = uiState) {
        is PlantDetailUiState.Loading -> LoadingIndicator()
        is PlantDetailUiState.Deleted -> Unit
        is PlantDetailUiState.Content -> {
            if (state.showLogSheet) {
                LogCareEventSheet(
                    sheetState = sheetState,
                    onDismiss = viewModel::onDismissLogSheet,
                    onLogWatering = viewModel::onWaterNow,
                    onLogFertilizing = viewModel::onFertilizeNow,
                    onLogEvent = { type, note -> viewModel.onLogCareEvent(type, note) },
                )
            }

            if (state.confirmDelete) {
                AlertDialog(
                    onDismissRequest = viewModel::onDismissDelete,
                    title = { Text("Delete ${state.plant.name}?") },
                    text = { Text("This plant will be archived and reminders cancelled.") },
                    confirmButton = {
                        TextButton(onClick = viewModel::onConfirmDelete) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::onDismissDelete) { Text("Cancel") }
                    },
                )
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(state.plant.name) },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { onNavigateToEdit(state.plant.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Care history") },
                                    onClick = {
                                        menuExpanded = false
                                        onNavigateToCareHistory(state.plant.id)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete plant") },
                                    onClick = {
                                        menuExpanded = false
                                        viewModel.onRequestDelete()
                                    },
                                )
                            }
                        },
                    )
                },
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                    ) {
                        if (state.plant.coverPhotoUri != null) {
                            AsyncImage(
                                model = state.plant.coverPhotoUri,
                                contentDescription = state.plant.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize().background(SproutCreamDark),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = state.plant.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.headlineLarge,
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        state.plant.species?.let { species ->
                            Text(species, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(Modifier.height(8.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusPill(status = state.wateringStatus)
                            Spacer(Modifier.width(8.dp))
                            WaterButton(onClick = viewModel::onWaterNow)
                        }

                        Spacer(Modifier.height(16.dp))

                        TextButton(onClick = viewModel::onShowLogSheet) {
                            Text("Log care event")
                        }

                        if (state.recentEvents.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Text("Recent activity", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(8.dp))
                            state.recentEvents.forEach { event ->
                                CareEventListItem(event = event)
                            }
                        }

                        state.plant.notes?.let { notes ->
                            Spacer(Modifier.height(16.dp))
                            Text("Notes", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(notes, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
