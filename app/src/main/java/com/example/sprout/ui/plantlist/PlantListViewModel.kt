package com.example.sprout.ui.plantlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.data.prefs.UserPreferencesRepository
import com.example.sprout.domain.model.wateringDueAt
import com.example.sprout.domain.model.wateringStatus
import com.example.sprout.domain.repository.PlantsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class PlantListViewModel @Inject constructor(
    private val plantsRepository: PlantsRepository,
    private val prefsRepository: UserPreferencesRepository,
    private val clock: Clock,
) : ViewModel() {

    val uiState = combine(
        plantsRepository.observeActivePlants(),
        prefsRepository.plantListView,
        prefsRepository.plantSortOrder,
    ) { plants, viewStyle, sortOrder ->
        if (plants.isEmpty()) {
            PlantListUiState.Empty
        } else {
            val now = Instant.now(clock)
            val items = plants.map { plant ->
                PlantWithStatus(
                    plant = plant,
                    wateringStatus = plant.wateringStatus(now),
                )
            }.sortedWith(sortOrder)
            PlantListUiState.Content(plants = items, viewStyle = viewStyle)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlantListUiState.Loading,
    )
}

private fun List<PlantWithStatus>.sortedWith(sortOrder: String): List<PlantWithStatus> =
    when (sortOrder) {
        "name" -> sortedBy { it.plant.name.lowercase() }
        "recently_added" -> sortedByDescending { it.plant.createdAt }
        else -> sortedWith(compareBy(nullsFirst()) { it.plant.wateringDueAt() })
    }
