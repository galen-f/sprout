package com.example.sprout.domain.repository

import com.example.sprout.domain.model.CareEvent
import kotlinx.coroutines.flow.Flow

interface CareEventsRepository {
    fun observeEventsForPlant(plantId: Long): Flow<List<CareEvent>>
    suspend fun insert(event: CareEvent)
}
