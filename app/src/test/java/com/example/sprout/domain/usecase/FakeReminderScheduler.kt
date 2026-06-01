package com.example.sprout.domain.usecase

import com.example.sprout.notifications.ReminderScheduler
import java.time.Instant

class FakeReminderScheduler : ReminderScheduler {
    val scheduledWatering = mutableMapOf<Long, Instant>()
    val scheduledFertilizer = mutableMapOf<Long, Instant>()
    val cancelledWatering = mutableListOf<Long>()
    val cancelledFertilizer = mutableListOf<Long>()

    override suspend fun scheduleWatering(plantId: Long, dueAt: Instant) {
        scheduledWatering[plantId] = dueAt
    }

    override suspend fun scheduleFertilizer(plantId: Long, dueAt: Instant) {
        scheduledFertilizer[plantId] = dueAt
    }

    override fun cancelWatering(plantId: Long) {
        cancelledWatering += plantId
        scheduledWatering.remove(plantId)
    }

    override fun cancelFertilizer(plantId: Long) {
        cancelledFertilizer += plantId
        scheduledFertilizer.remove(plantId)
    }

    override fun cancelAll(plantId: Long) {
        cancelWatering(plantId)
        cancelFertilizer(plantId)
    }
}
