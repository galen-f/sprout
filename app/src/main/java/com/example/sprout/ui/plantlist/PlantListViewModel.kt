package com.example.sprout.ui.plantlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.domain.model.wateringStatus
import com.example.sprout.domain.repository.PlantsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class PlantListViewModel @Inject constructor(
    private val plantsRepository: PlantsRepository,
    private val clock: Clock,
) : ViewModel() {

    val uiState = plantsRepository.observeActivePlants()
        .map { plants ->
            if (plants.isEmpty()) {
                PlantListUiState.Empty
            } else {
                val now = Instant.now(clock)
                PlantListUiState.Content(
                    plants.map { plant ->
                        PlantWithStatus(
                            plant = plant,
                            wateringStatus = plant.wateringStatus(now),
                        )
                    }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlantListUiState.Loading,
        )
}
