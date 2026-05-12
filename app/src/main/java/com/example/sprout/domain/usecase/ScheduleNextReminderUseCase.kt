package com.example.sprout.domain.usecase

import com.example.sprout.domain.model.Plant
import com.example.sprout.domain.model.wateringDueAt
import com.example.sprout.domain.model.fertilizerDueAt
import com.example.sprout.domain.repository.PlantsRepository
import com.example.sprout.notifications.ReminderScheduler
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import javax.inject.Inject

class ScheduleNextReminderUseCase @Inject constructor(
    private val plantsRepository: PlantsRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke(plantId: Long) {
        val plant = plantsRepository.observePlantById(plantId).firstOrNull() ?: return
        scheduleForPlant(plant)
    }

    fun scheduleForPlant(plant: Plant) {
        val wateringDue = plant.wateringDueAt()
        if (wateringDue != null && wateringDue.isAfter(Instant.EPOCH)) {
            reminderScheduler.scheduleWatering(plant.id, wateringDue)
        } else {
            reminderScheduler.cancelWatering(plant.id)
        }
        val fertDue = plant.fertilizerDueAt()
        if (fertDue != null) {
            reminderScheduler.scheduleFertilizer(plant.id, fertDue)
        } else {
            reminderScheduler.cancelFertilizer(plant.id)
        }
    }
}
