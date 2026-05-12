package com.example.sprout.domain.usecase

import com.example.sprout.domain.model.CareEvent
import com.example.sprout.domain.model.CareEventType
import com.example.sprout.domain.repository.CareEventsRepository
import com.example.sprout.domain.repository.PlantsRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class LogWateringUseCase @Inject constructor(
    private val plantsRepository: PlantsRepository,
    private val careEventsRepository: CareEventsRepository,
    private val scheduleNextReminder: ScheduleNextReminderUseCase,
    private val clock: Clock,
) {
    suspend operator fun invoke(plantId: Long, timestamp: Instant = Instant.now(clock)) {
        careEventsRepository.insert(
            CareEvent(plantId = plantId, type = CareEventType.WATERED, timestamp = timestamp)
        )
        plantsRepository.updateLastWatered(plantId, timestamp)
        scheduleNextReminder(plantId)
    }
}
