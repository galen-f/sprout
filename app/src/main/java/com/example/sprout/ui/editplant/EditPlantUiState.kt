package com.example.sprout.ui.editplant

sealed interface EditPlantUiState {
    object Loading : EditPlantUiState
    data class Editing(val form: EditPlantFormState) : EditPlantUiState
    object Saved : EditPlantUiState
    data class Error(val message: String) : EditPlantUiState
}
