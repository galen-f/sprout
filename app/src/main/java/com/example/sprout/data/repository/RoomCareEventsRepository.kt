package com.example.sprout.data.repository

import com.example.sprout.data.db.CareEventDao
import com.example.sprout.data.db.toDomain
import com.example.sprout.data.db.toEntity
import com.example.sprout.domain.model.CareEvent
import com.example.sprout.domain.repository.CareEventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCareEventsRepository @Inject constructor(
    private val dao: CareEventDao,
) : CareEventsRepository {

    override fun observeEventsForPlant(plantId: Long): Flow<List<CareEvent>> =
        dao.observeByPlant(plantId).map { list -> list.map { it.toDomain() } }

    override suspend fun getAllEvents(): List<CareEvent> =
        dao.getAllEvents().map { it.toDomain() }

    override suspend fun insert(event: CareEvent) =
        dao.insert(event.toEntity())
}
