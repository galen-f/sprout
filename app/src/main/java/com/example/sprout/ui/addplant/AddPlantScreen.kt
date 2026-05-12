package com.example.sprout.ui.addplant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.res.painterResource
import com.example.sprout.R
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sprout.ui.theme.SproutCreamDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    onNavigateBack: () -> Unit,
    onPlantSaved: (Long) -> Unit,
    viewModel: AddPlantViewModel = hiltViewModel(),
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AddPlantUiState.Saved) {
            onPlantSaved((uiState as AddPlantUiState.Saved).plantId)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? -> viewModel.onPhotoSelected(uri) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success -> if (success) viewModel.onPhotoSelected(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add plant") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(SproutCreamDark)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (form.coverPhotoUri != null) {
                    AsyncImage(
                        model = form.coverPhotoUri,
                        contentDescription = "Cover photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_photo),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                        Text(
                            "Tap to add photo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

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

            Text("Watering interval: ${form.wateringIntervalDays} days", style = MaterialTheme.typography.bodyMedium)
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
                Text("Fertilizer interval: ${form.fertilizerIntervalDays} days", style = MaterialTheme.typography.bodyMedium)
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
                enabled = uiState !is AddPlantUiState.Saving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (uiState is AddPlantUiState.Saving) "Saving…" else "Save plant")
            }
        }
    }
}
