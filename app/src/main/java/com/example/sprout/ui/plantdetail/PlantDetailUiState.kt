package com.example.sprout.ui.plantdetail

import com.example.sprout.domain.model.CareEvent
import com.example.sprout.domain.model.FertilizerStatus
import com.example.sprout.domain.model.Plant
import com.example.sprout.domain.model.WateringStatus

sealed interface PlantDetailUiState {
    object Loading : PlantDetailUiState
    object Deleted : PlantDetailUiState
    data class Content(
        val plant: Plant,
        val wateringStatus: WateringStatus,
        val fertilizerStatus: FertilizerStatus,
        val recentEvents: List<CareEvent>,
        val showLogSheet: Boolean = false,
        val confirmDelete: Boolean = false,
    ) : PlantDetailUiState
}
