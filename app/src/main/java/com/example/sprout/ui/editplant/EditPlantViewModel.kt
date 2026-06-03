package com.example.sprout.ui.editplant

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.domain.repository.PlantsRepository
import com.example.sprout.domain.usecase.ScheduleNextReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plantsRepository: PlantsRepository,
    private val scheduleNextReminder: ScheduleNextReminderUseCase,
) : ViewModel() {

    private val plantId: Long = checkNotNull(savedStateHandle["plantId"])
    private val _uiState = MutableStateFlow<EditPlantUiState>(EditPlantUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val plant = plantsRepository.observePlantById(plantId).firstOrNull()
            if (plant == null) {
                _uiState.value = EditPlantUiState.Error("Plant not found")
                return@launch
            }
            _uiState.value = EditPlantUiState.Editing(
                EditPlantFormState(
                    name = plant.name,
                    species = plant.species ?: "",
                    wateringIntervalDays = plant.wateringIntervalDays,
                    enableFertilizer = plant.fertilizerIntervalDays != null,
                    fertilizerIntervalDays = plant.fertilizerIntervalDays ?: 30,
                    notes = plant.notes ?: "",
                )
            )
        }
    }

    private fun updateForm(block: EditPlantFormState.() -> EditPlantFormState) {
        val current = _uiState.value
        if (current is EditPlantUiState.Editing) {
            _uiState.value = EditPlantUiState.Editing(current.form.block())
        }
    }

    fun onNameChanged(name: String) = updateForm { copy(name = name, nameError = null) }
    fun onSpeciesChanged(s: String) = updateForm { copy(species = s) }
    fun onWateringIntervalChanged(days: Int) = updateForm { copy(wateringIntervalDays = days.coerceAtLeast(1)) }
    fun onEnableFertilizerChanged(enabled: Boolean) = updateForm { copy(enableFertilizer = enabled) }
    fun onFertilizerIntervalChanged(days: Int) = updateForm { copy(fertilizerIntervalDays = days.coerceAtLeast(1)) }
    fun onNotesChanged(notes: String) = updateForm { copy(notes = notes) }

    fun save() {
        val current = _uiState.value as? EditPlantUiState.Editing ?: return
        val f = current.form
        if (f.name.isBlank()) {
            updateForm { copy(nameError = "Name is required") }
            return
        }
        viewModelScope.launch {
            try {
                val existing = plantsRepository.observePlantById(plantId).firstOrNull() ?: return@launch
                val updated = existing.copy(
                    name = f.name.trim(),
                    species = f.species.trim().ifBlank { null },
                    wateringIntervalDays = f.wateringIntervalDays,
                    fertilizerIntervalDays = if (f.enableFertilizer) f.fertilizerIntervalDays else null,
                    notes = f.notes.trim().ifBlank { null },
                )
                plantsRepository.upsert(updated)
                scheduleNextReminder.scheduleForPlant(updated)
                _uiState.value = EditPlantUiState.Saved
            } catch (e: Exception) {
                _uiState.value = EditPlantUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
