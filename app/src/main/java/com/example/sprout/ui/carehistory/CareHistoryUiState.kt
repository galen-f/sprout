package com.example.sprout.ui.carehistory

import com.example.sprout.domain.model.CareEvent
import com.example.sprout.domain.model.CareEventType

sealed interface CareHistoryUiState {
    object Loading : CareHistoryUiState
    data class Content(
        val plantName: String,
        val events: List<CareEvent>,
        val selectedFilter: CareEventType?,
    ) : CareHistoryUiState
}
