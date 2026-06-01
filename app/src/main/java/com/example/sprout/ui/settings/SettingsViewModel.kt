package com.example.sprout.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.data.export.CsvExporter
import com.example.sprout.data.prefs.UserPreferencesRepository
import com.example.sprout.domain.repository.CareEventsRepository
import com.example.sprout.domain.repository.PlantsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
    private val plantsRepository: PlantsRepository,
    private val careEventsRepository: CareEventsRepository,
    private val csvExporter: CsvExporter,
) : ViewModel() {

    private val reminderTimeFlow = combine(
        prefsRepository.reminderHour,
        prefsRepository.reminderMinute,
    ) { hour, minute -> hour to minute }

    val uiState = combine(
        prefsRepository.showArchivedPlants,
        prefsRepository.themeMode,
        prefsRepository.repeatReminders,
        plantsRepository.observeAllPlants(),
        reminderTimeFlow,
    ) { showArchived, themeMode, repeatReminders, allPlants, reminderTime ->
        SettingsUiState(
            showArchivedPlants = showArchived,
            themeMode = themeMode,
            repeatReminders = repeatReminders,
            reminderHour = reminderTime.first,
            reminderMinute = reminderTime.second,
            archivedPlants = if (showArchived) allPlants.filter { it.archivedAt != null } else emptyList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    private val _exportUri = MutableSharedFlow<Uri>()
    val exportUri: SharedFlow<Uri> = _exportUri.asSharedFlow()

    fun setShowArchived(show: Boolean) {
        viewModelScope.launch { prefsRepository.setShowArchived(show) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { prefsRepository.setThemeMode(mode) }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            prefsRepository.setReminderHour(hour)
            prefsRepository.setReminderMinute(minute)
        }
    }

    fun setRepeatReminders(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setRepeatReminders(enabled) }
    }

    fun restorePlant(plantId: Long) {
        viewModelScope.launch { plantsRepository.restore(plantId) }
    }

    fun exportCsv() {
        viewModelScope.launch {
            val plants = plantsRepository.observeAllPlants().first()
            val events = careEventsRepository.getAllEvents()
            val uri = csvExporter.export(plants, events)
            _exportUri.emit(uri)
        }
    }
}
