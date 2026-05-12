package com.example.sprout.domain.usecase

import com.example.sprout.domain.model.Plant
import com.example.sprout.domain.repository.PlantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant

class FakePlantsRepository : PlantsRepository {
    private val plants = MutableStateFlow<List<Plant>>(emptyList())

    fun addPlant(plant: Plant) {
        plants.value = plants.value + plant
    }

    override fun observeActivePlants(): Flow<List<Plant>> =
        plants.map { list -> list.filter { it.archivedAt == null } }

    override fun observeAllPlants(): Flow<List<Plant>> = plants

    override fun observePlantById(id: Long): Flow<Plant?> =
        plants.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun upsert(plant: Plant): Long {
        val existing = plants.value.indexOfFirst { it.id == plant.id }
        if (existing >= 0) {
            val mutable = plants.value.toMutableList()
            mutable[existing] = plant
            plants.value = mutable
        } else {
            plants.value = plants.value + plant.copy(id = (plants.value.maxOfOrNull { it.id } ?: 0) + 1)
        }
        return plant.id
    }

    override suspend fun updateLastWatered(plantId: Long, timestamp: Instant) {
        plants.value = plants.value.map {
            if (it.id == plantId) it.copy(lastWateredAt = timestamp) else it
        }
    }

    override suspend fun updateLastFertilized(plantId: Long, timestamp: Instant) {
        plants.value = plants.value.map {
            if (it.id == plantId) it.copy(lastFertilizedAt = timestamp) else it
        }
    }

    override suspend fun softDelete(plantId: Long, timestamp: Instant) {
        plants.value = plants.value.map {
            if (it.id == plantId) it.copy(archivedAt = timestamp) else it
        }
    }

    override suspend fun restore(plantId: Long) {
        plants.value = plants.value.map {
            if (it.id == plantId) it.copy(archivedAt = null) else it
        }
    }
}
