package com.example.sprout.domain.usecase

import com.example.sprout.domain.repository.PlantsRepository
import com.example.sprout.notifications.ReminderScheduler
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class DeletePlantUseCase @Inject constructor(
    private val plantsRepository: PlantsRepository,
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock,
) {
    suspend operator fun invoke(plantId: Long) {
        plantsRepository.softDelete(plantId, Instant.now(clock))
        reminderScheduler.cancelAll(plantId)
    }
}
