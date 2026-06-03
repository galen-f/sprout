package com.example.sprout.ui.plantlist

import com.example.sprout.domain.model.Plant
import com.example.sprout.domain.model.WateringStatus

data class PlantWithStatus(
    val plant: Plant,
    val wateringStatus: WateringStatus,
)

sealed interface PlantListUiState {
    object Loading : PlantListUiState
    object Empty : PlantListUiState
    data class Content(
        val plants: List<PlantWithStatus>,
        val viewStyle: String = "grid",
    ) : PlantListUiState
    data class Error(val message: String) : PlantListUiState
}
