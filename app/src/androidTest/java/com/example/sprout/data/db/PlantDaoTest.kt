package com.example.sprout.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantDaoTest {

    private lateinit var db: SproutDatabase
    private lateinit var dao: PlantDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SproutDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.plantDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insertAndObservePlant() = runTest {
        val plant = PlantEntity(
            name = "Cactus",
            species = "Opuntia",
            coverPhotoUri = null,
            wateringIntervalDays = 14,
            lastWateredAt = null,
            fertilizerIntervalDays = null,
            lastFertilizedAt = null,
            notes = null,
            createdAt = System.currentTimeMillis(),
            archivedAt = null,
        )
        val id = dao.upsert(plant)
        val result = dao.observeActivePlants().first()
        assertEquals(1, result.size)
        assertEquals("Cactus", result.first().name)
        assertEquals(id, result.first().id)
    }

    @Test
    fun softDeleteRemovesFromActivePlants() = runTest {
        val plant = PlantEntity(
            name = "Fern",
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
        val id = dao.upsert(plant)
        dao.softDelete(id, System.currentTimeMillis())
        val active = dao.observeActivePlants().first()
        val all = dao.observeAllPlants().first()
        assertEquals(0, active.size)
        assertEquals(1, all.size)
        assertNotNull(all.first().archivedAt)
    }

    @Test
    fun updateLastWatered() = runTest {
        val plant = PlantEntity(
            name = "Rose",
            species = null,
            coverPhotoUri = null,
            wateringIntervalDays = 3,
            lastWateredAt = null,
            fertilizerIntervalDays = null,
            lastFertilizedAt = null,
            notes = null,
            createdAt = System.currentTimeMillis(),
            archivedAt = null,
        )
        val id = dao.upsert(plant)
        val ts = System.currentTimeMillis()
        dao.updateLastWatered(id, ts)
        val result = dao.observeById(id).first()
        assertEquals(ts, result?.lastWateredAt)
    }
}
