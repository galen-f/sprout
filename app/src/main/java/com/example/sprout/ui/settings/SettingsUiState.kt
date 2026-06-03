package com.example.sprout.ui.settings

import com.example.sprout.domain.model.Plant

data class SettingsUiState(
    val showArchivedPlants: Boolean = false,
    val archivedPlants: List<Plant> = emptyList(),
    val themeMode: String = "system",
    val plantListView: String = "grid",
    val plantSortOrder: String = "next_due",
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val repeatReminders: Boolean = false,
)
