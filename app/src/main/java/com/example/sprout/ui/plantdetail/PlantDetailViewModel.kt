package com.example.sprout.ui.plantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.domain.model.CareEventType
import com.example.sprout.domain.model.fertilizerStatus
import com.example.sprout.domain.model.wateringStatus
import com.example.sprout.domain.repository.CareEventsRepository
import com.example.sprout.domain.repository.PlantsRepository
import com.example.sprout.domain.usecase.DeletePlantUseCase
import com.example.sprout.domain.usecase.LogCareEventUseCase
import com.example.sprout.domain.usecase.LogFertilizingUseCase
import com.example.sprout.domain.usecase.LogWateringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plantsRepository: PlantsRepository,
    private val careEventsRepository: CareEventsRepository,
    private val logWatering: LogWateringUseCase,
    private val logFertilizing: LogFertilizingUseCase,
    private val logCareEvent: LogCareEventUseCase,
    private val deletePlant: DeletePlantUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val plantId: Long = checkNotNull(savedStateHandle["plantId"])

    private val _extraState = MutableStateFlow(ExtraState())

    private data class ExtraState(
        val showLogSheet: Boolean = false,
        val confirmDelete: Boolean = false,
    )

    val uiState = combine(
        plantsRepository.observePlantById(plantId),
        careEventsRepository.observeEventsForPlant(plantId),
        _extraState,
    ) { plant, events, extra ->
        if (plant == null) return@combine PlantDetailUiState.Deleted
        val now = Instant.now(clock)
        PlantDetailUiState.Content(
            plant = plant,
            wateringStatus = plant.wateringStatus(now),
            fertilizerStatus = plant.fertilizerStatus(now),
            recentEvents = events.take(5),
            showLogSheet = extra.showLogSheet,
            confirmDelete = extra.confirmDelete,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlantDetailUiState.Loading,
    )

    fun onWaterNow() {
        viewModelScope.launch { logWatering(plantId) }
    }

    fun onFertilizeNow() {
        viewModelScope.launch { logFertilizing(plantId) }
    }

    fun onLogCareEvent(type: CareEventType, note: String? = null) {
        viewModelScope.launch { logCareEvent(plantId, type, note) }
    }

    fun onShowLogSheet() = _extraState.update { it.copy(showLogSheet = true) }
    fun onDismissLogSheet() = _extraState.update { it.copy(showLogSheet = false) }
    fun onRequestDelete() = _extraState.update { it.copy(confirmDelete = true) }
    fun onDismissDelete() = _extraState.update { it.copy(confirmDelete = false) }

    fun onConfirmDelete() {
        viewModelScope.launch { deletePlant(plantId) }
    }
}
