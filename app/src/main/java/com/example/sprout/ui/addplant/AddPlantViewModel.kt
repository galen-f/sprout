package com.example.sprout.ui.addplant

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.data.photo.PhotoStorage
import com.example.sprout.domain.model.Plant
import com.example.sprout.domain.repository.PlantsRepository
import com.example.sprout.domain.usecase.ScheduleNextReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val plantsRepository: PlantsRepository,
    private val photoStorage: PhotoStorage,
    private val scheduleNextReminder: ScheduleNextReminderUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val _form = MutableStateFlow(AddPlantFormState())
    val form = _form.asStateFlow()

    private val _uiState = MutableStateFlow<AddPlantUiState>(AddPlantUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onNameChanged(name: String) = _form.update { it.copy(name = name, nameError = null) }
    fun onSpeciesChanged(s: String) = _form.update { it.copy(species = s) }
    fun onPhotoSelected(uri: Uri?) = _form.update { it.copy(coverPhotoUri = uri) }
    fun onWateringIntervalChanged(days: Int) = _form.update { it.copy(wateringIntervalDays = days.coerceAtLeast(1)) }
    fun onEnableFertilizerChanged(enabled: Boolean) = _form.update { it.copy(enableFertilizer = enabled) }
    fun onFertilizerIntervalChanged(days: Int) = _form.update { it.copy(fertilizerIntervalDays = days.coerceAtLeast(1)) }
    fun onNotesChanged(notes: String) = _form.update { it.copy(notes = notes) }

    fun save() {
        val f = _form.value
        if (f.name.isBlank()) {
            _form.update { it.copy(nameError = "Name is required") }
            return
        }
        viewModelScope.launch {
            _uiState.value = AddPlantUiState.Saving
            try {
                val plant = Plant(
                    name = f.name.trim(),
                    species = f.species.trim().ifBlank { null },
                    wateringIntervalDays = f.wateringIntervalDays,
                    fertilizerIntervalDays = if (f.enableFertilizer) f.fertilizerIntervalDays else null,
                    notes = f.notes.trim().ifBlank { null },
                    createdAt = Instant.now(clock),
                )
                val plantId = plantsRepository.upsert(plant)

                val coverUri = f.coverPhotoUri
                if (coverUri != null) {
                    val dest = photoStorage.coverFile(plantId)
                    photoStorage.copyFromUri(coverUri, dest)
                    plantsRepository.upsert(plant.copy(id = plantId, coverPhotoUri = dest.absolutePath))
                }

                scheduleNextReminder(plantId)
                _uiState.value = AddPlantUiState.Saved(plantId)
            } catch (e: Exception) {
                _uiState.value = AddPlantUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
