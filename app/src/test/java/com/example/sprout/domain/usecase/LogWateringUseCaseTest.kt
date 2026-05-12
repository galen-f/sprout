package com.example.sprout.domain.usecase

import com.example.sprout.domain.model.CareEventType
import com.example.sprout.domain.model.Plant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class LogWateringUseCaseTest {

    private lateinit var plantsRepository: FakePlantsRepository
    private lateinit var careEventsRepository: FakeCareEventsRepository
    private lateinit var reminderScheduler: FakeReminderScheduler
    private lateinit var useCase: LogWateringUseCase

    private val testPlant = Plant(id = 1L, name = "Fern", wateringIntervalDays = 7)
    private val fixedNow: Instant = Instant.parse("2024-06-01T10:00:00Z")
    private val fixedClock: Clock = Clock.fixed(fixedNow, ZoneId.of("UTC"))

    @Before
    fun setUp() {
        plantsRepository = FakePlantsRepository().also { it.addPlant(testPlant) }
        careEventsRepository = FakeCareEventsRepository()
        reminderScheduler = FakeReminderScheduler()

        val scheduleNext = ScheduleNextReminderUseCase(plantsRepository, reminderScheduler)
        useCase = LogWateringUseCase(plantsRepository, careEventsRepository, scheduleNext, fixedClock)
    }

    @Test
    fun `invoke inserts WATERED care event`() = runTest {
        useCase(plantId = 1L)

        val events = careEventsRepository.insertedEvents
        assertEquals(1, events.size)
        assertEquals(CareEventType.WATERED, events.first().type)
        assertEquals(1L, events.first().plantId)
    }

    @Test
    fun `invoke updates lastWateredAt`() = runTest {
        useCase(plantId = 1L)

        val plant = plantsRepository.observePlantById(1L).first()
        assertNotNull(plant?.lastWateredAt)
        assertEquals(fixedNow, plant?.lastWateredAt)
    }

    @Test
    fun `invoke schedules watering reminder`() = runTest {
        useCase(plantId = 1L)

        assertNotNull(reminderScheduler.scheduledWatering[1L])
    }
}
