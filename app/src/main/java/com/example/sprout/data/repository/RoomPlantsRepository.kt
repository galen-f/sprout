package com.example.sprout.data.repository

import com.example.sprout.data.db.PlantDao
import com.example.sprout.data.db.toDomain
import com.example.sprout.data.db.toEntity
import com.example.sprout.domain.model.Plant
import com.example.sprout.domain.repository.PlantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class RoomPlantsRepository @Inject constructor(
    private val dao: PlantDao,
) : PlantsRepository {

    override fun observeActivePlants(): Flow<List<Plant>> =
        dao.observeActivePlants().map { list -> list.map { it.toDomain() } }

    override fun observeAllPlants(): Flow<List<Plant>> =
        dao.observeAllPlants().map { list -> list.map { it.toDomain() } }

    override fun observePlantById(id: Long): Flow<Plant?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun upsert(plant: Plant): Long =
        dao.upsert(plant.toEntity())

    override suspend fun updateLastWatered(plantId: Long, timestamp: Instant) =
        dao.updateLastWatered(plantId, timestamp.toEpochMilli())

    override suspend fun updateLastFertilized(plantId: Long, timestamp: Instant) =
        dao.updateLastFertilized(plantId, timestamp.toEpochMilli())

    override suspend fun softDelete(plantId: Long, timestamp: Instant) =
        dao.softDelete(plantId, timestamp.toEpochMilli())

    override suspend fun restore(plantId: Long) =
        dao.restore(plantId)
}
