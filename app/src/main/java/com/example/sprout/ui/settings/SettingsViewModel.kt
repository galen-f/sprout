package com.example.sprout.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.data.prefs.UserPreferencesRepository
import com.example.sprout.domain.repository.PlantsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
    private val plantsRepository: PlantsRepository,
) : ViewModel() {

    val uiState = combine(
        prefsRepository.showArchivedPlants,
        plantsRepository.observeAllPlants(),
    ) { showArchived, allPlants ->
        SettingsUiState(
            showArchivedPlants = showArchived,
            archivedPlants = if (showArchived) allPlants.filter { it.archivedAt != null } else emptyList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    fun setShowArchived(show: Boolean) {
        viewModelScope.launch { prefsRepository.setShowArchived(show) }
    }

    fun restorePlant(plantId: Long) {
        viewModelScope.launch { plantsRepository.restore(plantId) }
    }
}
