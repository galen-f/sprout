package com.example.sprout.ui.plantlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sprout.ui.components.EmptyState
import com.example.sprout.ui.components.LoadingIndicator
import com.example.sprout.ui.components.PlantCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    onNavigateToPlant: (Long) -> Unit,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: PlantListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlantListContent(
        uiState = uiState,
        onNavigateToPlant = onNavigateToPlant,
        onNavigateToAddPlant = onNavigateToAddPlant,
        onNavigateToSettings = onNavigateToSettings,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlantListContent(
    uiState: PlantListUiState,
    onNavigateToPlant: (Long) -> Unit,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sprout") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                menuExpanded = false
                                onNavigateToSettings()
                            },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddPlant) {
                Icon(Icons.Default.Add, contentDescription = "Add plant")
            }
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is PlantListUiState.Loading -> LoadingIndicator(modifier = Modifier.padding(paddingValues))
            is PlantListUiState.Empty -> EmptyState(
                title = "No plants yet",
                subtitle = "Tap + to add your first plant",
                modifier = Modifier.padding(paddingValues),
            )
            is PlantListUiState.Content -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                ) {
                    items(state.plants, key = { it.plant.id }) { item ->
                        PlantCard(
                            plant = item.plant,
                            wateringStatus = item.wateringStatus,
                            onClick = { onNavigateToPlant(item.plant.id) },
                        )
                    }
                }
            }
            is PlantListUiState.Error -> EmptyState(
                title = "Something went wrong",
                subtitle = state.message,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}
