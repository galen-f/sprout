package com.example.sprout.ui.settings

import com.example.sprout.domain.model.Plant

data class SettingsUiState(
    val showArchivedPlants: Boolean = false,
    val archivedPlants: List<Plant> = emptyList(),
)
