package com.example.sprout.domain.repository

import com.example.sprout.domain.model.Plant
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface PlantsRepository {
    fun observeActivePlants(): Flow<List<Plant>>
    fun observeAllPlants(): Flow<List<Plant>>
    fun observePlantById(id: Long): Flow<Plant?>
    suspend fun upsert(plant: Plant): Long
    suspend fun updateLastWatered(plantId: Long, timestamp: Instant)
    suspend fun updateLastFertilized(plantId: Long, timestamp: Instant)
    suspend fun softDelete(plantId: Long, timestamp: Instant)
    suspend fun restore(plantId: Long)
}
