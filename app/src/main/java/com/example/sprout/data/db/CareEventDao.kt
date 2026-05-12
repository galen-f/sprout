package com.example.sprout.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CareEventDao {
    @Query("SELECT * FROM care_events WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun observeByPlant(plantId: Long): Flow<List<CareEventEntity>>

    @Insert
    suspend fun insert(event: CareEventEntity)
}
