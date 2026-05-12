package com.example.sprout.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class PlantExtensionsTest {

    private fun makePlant(
        lastWateredAt: Instant? = null,
        wateringIntervalDays: Int = 7,
        lastFertilizedAt: Instant? = null,
        fertilizerIntervalDays: Int? = null,
    ) = Plant(
        name = "Test",
        wateringIntervalDays = wateringIntervalDays,
        lastWateredAt = lastWateredAt,
        lastFertilizedAt = lastFertilizedAt,
        fertilizerIntervalDays = fertilizerIntervalDays,
    )

    // --- Watering ---

    @Test
    fun `NeverWatered when lastWateredAt is null`() {
        val plant = makePlant()
        val result = plant.wateringStatus(Instant.now())
        assertEquals(WateringStatus.NeverWatered, result)
    }

    @Test
    fun `DueIn when watered today and interval is 7 days`() {
        val now = Instant.now()
        val plant = makePlant(lastWateredAt = now, wateringIntervalDays = 7)
        val result = plant.wateringStatus(now)
        assertTrue("Expected DueIn but got $result", result is WateringStatus.DueIn)
        assertEquals(7, (result as WateringStatus.DueIn).days)
    }

    @Test
    fun `DueToday when due date is today`() {
        val now = Instant.now()
        val lastWatered = now.minus(7, ChronoUnit.DAYS)
        val plant = makePlant(lastWateredAt = lastWatered, wateringIntervalDays = 7)
        val result = plant.wateringStatus(now)
        assertEquals(WateringStatus.DueToday, result)
    }

    @Test
    fun `OverdueBy 2 when overdue by 2 days`() {
        val now = Instant.now()
        val lastWatered = now.minus(9, ChronoUnit.DAYS)
        val plant = makePlant(lastWateredAt = lastWatered, wateringIntervalDays = 7)
        val result = plant.wateringStatus(now)
        assertTrue("Expected OverdueBy but got $result", result is WateringStatus.OverdueBy)
        assertEquals(2, (result as WateringStatus.OverdueBy).days)
    }

    // --- Fertilizer ---

    @Test
    fun `NotScheduled when fertilizerIntervalDays is null`() {
        val plant = makePlant()
        val result = plant.fertilizerStatus(Instant.now())
        assertEquals(FertilizerStatus.NotScheduled, result)
    }

    @Test
    fun `NeverFertilized when interval set but lastFertilizedAt is null`() {
        val plant = makePlant(fertilizerIntervalDays = 30)
        val result = plant.fertilizerStatus(Instant.now())
        assertEquals(FertilizerStatus.NeverFertilized, result)
    }

    @Test
    fun `FertilizerDueIn when fertilized today`() {
        val now = Instant.now()
        val plant = makePlant(lastFertilizedAt = now, fertilizerIntervalDays = 30)
        val result = plant.fertilizerStatus(now)
        assertTrue("Expected DueIn but got $result", result is FertilizerStatus.DueIn)
        assertEquals(30, (result as FertilizerStatus.DueIn).days)
    }

    @Test
    fun `FertilizerDueToday when fertilizer due today`() {
        val now = Instant.now()
        val lastFertilized = now.minus(30, ChronoUnit.DAYS)
        val plant = makePlant(lastFertilizedAt = lastFertilized, fertilizerIntervalDays = 30)
        val result = plant.fertilizerStatus(now)
        assertEquals(FertilizerStatus.DueToday, result)
    }

    @Test
    fun `FertilizerOverdueBy 5`() {
        val now = Instant.now()
        val lastFertilized = now.minus(35, ChronoUnit.DAYS)
        val plant = makePlant(lastFertilizedAt = lastFertilized, fertilizerIntervalDays = 30)
        val result = plant.fertilizerStatus(now)
        assertTrue("Expected OverdueBy but got $result", result is FertilizerStatus.OverdueBy)
        assertEquals(5, (result as FertilizerStatus.OverdueBy).days)
    }
}
