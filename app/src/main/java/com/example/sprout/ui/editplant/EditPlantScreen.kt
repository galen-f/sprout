package com.example.sprout.ui.editplant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sprout.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlantScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditPlantViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is EditPlantUiState.Saved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit plant") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is EditPlantUiState.Loading -> LoadingIndicator(modifier = Modifier.padding(paddingValues))
            is EditPlantUiState.Saved -> Unit
            is EditPlantUiState.Error -> Text(state.message, modifier = Modifier.padding(paddingValues).padding(16.dp))
            is EditPlantUiState.Editing -> {
                val form = state.form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = viewModel::onNameChanged,
                        label = { Text("Plant name *") },
                        isError = form.nameError != null,
                        supportingText = form.nameError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = form.species,
                        onValueChange = viewModel::onSpeciesChanged,
                        label = { Text("Species (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Watering interval: ${form.wateringIntervalDays} days")
                    Slider(
                        value = form.wateringIntervalDays.toFloat(),
                        onValueChange = { viewModel.onWateringIntervalChanged(it.toInt()) },
                        valueRange = 1f..60f,
                        steps = 58,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Enable fertilizer reminders", modifier = Modifier.weight(1f))
                        Switch(
                            checked = form.enableFertilizer,
                            onCheckedChange = viewModel::onEnableFertilizerChanged,
                        )
                    }
                    if (form.enableFertilizer) {
                        Text("Fertilizer interval: ${form.fertilizerIntervalDays} days")
                        Slider(
                            value = form.fertilizerIntervalDays.toFloat(),
                            onValueChange = { viewModel.onFertilizerIntervalChanged(it.toInt()) },
                            valueRange = 1f..120f,
                            steps = 118,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = form.notes,
                        onValueChange = viewModel::onNotesChanged,
                        label = { Text("Notes (optional)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = viewModel::save,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Save changes")
                    }
                }
            }
        }
    }
}
