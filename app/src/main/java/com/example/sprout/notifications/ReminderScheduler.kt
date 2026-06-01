package com.example.sprout.notifications

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sprout.data.prefs.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface ReminderScheduler {
    suspend fun scheduleWatering(plantId: Long, dueAt: Instant)
    suspend fun scheduleFertilizer(plantId: Long, dueAt: Instant)
    fun cancelWatering(plantId: Long)
    fun cancelFertilizer(plantId: Long)
    fun cancelAll(plantId: Long)
}

@Singleton
class WorkManagerReminderScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val prefsRepository: UserPreferencesRepository,
) : ReminderScheduler {

    override suspend fun scheduleWatering(plantId: Long, dueAt: Instant) {
        schedule(
            uniqueName = "watering_$plantId",
            plantId = plantId,
            type = ReminderWorker.TYPE_WATERING,
            dueAt = dueAt,
        )
    }

    override suspend fun scheduleFertilizer(plantId: Long, dueAt: Instant) {
        schedule(
            uniqueName = "fertilizer_$plantId",
            plantId = plantId,
            type = ReminderWorker.TYPE_FERTILIZER,
            dueAt = dueAt,
        )
    }

    override fun cancelWatering(plantId: Long) {
        workManager.cancelUniqueWork("watering_$plantId")
    }

    override fun cancelFertilizer(plantId: Long) {
        workManager.cancelUniqueWork("fertilizer_$plantId")
    }

    override fun cancelAll(plantId: Long) {
        cancelWatering(plantId)
        cancelFertilizer(plantId)
    }

    private suspend fun schedule(uniqueName: String, plantId: Long, type: String, dueAt: Instant) {
        val hour = prefsRepository.reminderHour.first()
        val minute = prefsRepository.reminderMinute.first()
        val fireAt = dueAt.atConfiguredTime(hour, minute)
        val delayMs = fireAt.toEpochMilli() - Instant.now().toEpochMilli()
        if (delayMs <= 0) return

        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_PLANT_ID, plantId)
            .putString(ReminderWorker.KEY_TYPE, type)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag("plant_$plantId")
            .build()

        workManager.enqueueUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, request)
    }
}

private fun Instant.atConfiguredTime(hour: Int, minute: Int): Instant {
    val zone = ZoneId.systemDefault()
    val day = atZone(zone).toLocalDate()
    return ZonedDateTime.of(day, LocalTime.of(hour, minute), zone).toInstant()
}
