package com.example.sprout.domain.usecase

import com.example.sprout.domain.model.CareEvent
import com.example.sprout.domain.repository.CareEventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCareEventsRepository : CareEventsRepository {
    private val events = MutableStateFlow<List<CareEvent>>(emptyList())

    val insertedEvents: List<CareEvent> get() = events.value

    override fun observeEventsForPlant(plantId: Long): Flow<List<CareEvent>> =
        events.map { list -> list.filter { it.plantId == plantId } }

    override suspend fun insert(event: CareEvent) {
        events.value = events.value + event.copy(id = events.value.size.toLong() + 1)
    }
}
