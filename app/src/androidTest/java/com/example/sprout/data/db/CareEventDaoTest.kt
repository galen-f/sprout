package com.example.sprout.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CareEventDaoTest {

    private lateinit var db: SproutDatabase
    private lateinit var plantDao: PlantDao
    private lateinit var careEventDao: CareEventDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SproutDatabase::class.java,
        ).allowMainThreadQueries().build()
        plantDao = db.plantDao()
        careEventDao = db.careEventDao()
    }

    @After
    fun tearDown() = db.close()

    private suspend fun insertPlant(name: String): Long {
        return plantDao.upsert(
            PlantEntity(
                name = name,
                species = null,
                coverPhotoUri = null,
                wateringIntervalDays = 7,
                lastWateredAt = null,
                fertilizerIntervalDays = null,
                lastFertilizedAt = null,
                notes = null,
                createdAt = System.currentTimeMillis(),
                archivedAt = null,
            )
        )
    }

    @Test
    fun insertAndObserveCareEvents() = runTest {
        val plantId = insertPlant("Tulip")
        careEventDao.insert(
            CareEventEntity(
                plantId = plantId,
                type = "WATERED",
                timestamp = System.currentTimeMillis(),
                note = null,
            )
        )
        val events = careEventDao.observeByPlant(plantId).first()
        assertEquals(1, events.size)
        assertEquals("WATERED", events.first().type)
    }

    @Test
    fun cascadeDeleteRemovesCareEventsWhenPlantDeleted() = runTest {
        val plantId = insertPlant("Orchid")
        careEventDao.insert(
            CareEventEntity(plantId = plantId, type = "WATERED", timestamp = System.currentTimeMillis(), note = null)
        )
        careEventDao.insert(
            CareEventEntity(plantId = plantId, type = "FERTILIZED", timestamp = System.currentTimeMillis(), note = null)
        )
        assertEquals(2, careEventDao.observeByPlant(plantId).first().size)

        // Verify CASCADE DELETE works correctly via direct SQL delete on the parent
        db.openHelper.writableDatabase.execSQL("DELETE FROM plants WHERE id = $plantId")

        val events = careEventDao.observeByPlant(plantId).first()
        assertEquals(0, events.size)
    }
}
