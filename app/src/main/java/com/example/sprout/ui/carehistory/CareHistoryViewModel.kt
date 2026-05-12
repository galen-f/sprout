package com.example.sprout.ui.carehistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprout.domain.model.CareEventType
import com.example.sprout.domain.repository.CareEventsRepository
import com.example.sprout.domain.repository.PlantsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CareHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    plantsRepository: PlantsRepository,
    careEventsRepository: CareEventsRepository,
) : ViewModel() {

    private val plantId: Long = checkNotNull(savedStateHandle["plantId"])
    private val _selectedFilter = MutableStateFlow<CareEventType?>(null)

    val uiState = combine(
        plantsRepository.observePlantById(plantId),
        careEventsRepository.observeEventsForPlant(plantId),
        _selectedFilter,
    ) { plant, events, filter ->
        val filtered = if (filter != null) events.filter { it.type == filter } else events
        CareHistoryUiState.Content(
            plantName = plant?.name ?: "Plant",
            events = filtered,
            selectedFilter = filter,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CareHistoryUiState.Loading,
    )

    fun setFilter(type: CareEventType?) {
        _selectedFilter.value = type
    }
}
