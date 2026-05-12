package com.example.sprout.ui.addplant

sealed interface AddPlantUiState {
    object Idle : AddPlantUiState
    object Saving : AddPlantUiState
    data class Saved(val plantId: Long) : AddPlantUiState
    data class Error(val message: String) : AddPlantUiState
}
