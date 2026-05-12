package com.example.sprout.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants WHERE archivedAt IS NULL ORDER BY name ASC")
    fun observeActivePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants ORDER BY name ASC")
    fun observeAllPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :id")
    fun observeById(id: Long): Flow<PlantEntity?>

    @Upsert
    suspend fun upsert(plant: PlantEntity): Long

    @Query("UPDATE plants SET lastWateredAt = :timestampMs WHERE id = :plantId")
    suspend fun updateLastWatered(plantId: Long, timestampMs: Long)

    @Query("UPDATE plants SET lastFertilizedAt = :timestampMs WHERE id = :plantId")
    suspend fun updateLastFertilized(plantId: Long, timestampMs: Long)

    @Query("UPDATE plants SET archivedAt = :timestampMs WHERE id = :plantId")
    suspend fun softDelete(plantId: Long, timestampMs: Long)

    @Query("UPDATE plants SET archivedAt = NULL WHERE id = :plantId")
    suspend fun restore(plantId: Long)
}
