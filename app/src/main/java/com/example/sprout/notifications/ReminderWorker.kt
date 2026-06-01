package com.example.sprout.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.sprout.MainActivity
import com.example.sprout.R
import com.example.sprout.data.prefs.UserPreferencesRepository
import com.example.sprout.domain.model.fertilizerDueAt
import com.example.sprout.domain.model.wateringDueAt
import com.example.sprout.domain.repository.PlantsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val plantsRepository: PlantsRepository,
    private val notificationManager: NotificationManagerCompat,
    private val prefsRepository: UserPreferencesRepository,
    private val workManager: WorkManager,
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_PLANT_ID = "plant_id"
        const val KEY_TYPE = "type"
        const val TYPE_WATERING = "watering"
        const val TYPE_FERTILIZER = "fertilizer"
    }

    override suspend fun doWork(): Result {
        val plantId = inputData.getLong(KEY_PLANT_ID, -1L)
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        if (plantId == -1L) return Result.failure()

        val plant = plantsRepository.observePlantById(plantId).first() ?: return Result.success()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) return Result.success()
        }

        val (channelId, title, body) = when (type) {
            TYPE_WATERING -> Triple(
                NotificationChannels.WATERING,
                "${plant.name} needs water",
                "Time to water your ${plant.species ?: "plant"}",
            )
            TYPE_FERTILIZER -> Triple(
                NotificationChannels.FERTILIZING,
                "${plant.name} needs fertilizing",
                "Time to fertilize your ${plant.species ?: "plant"}",
            )
            else -> return Result.failure()
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_PLANT_ID, plantId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            plantId.toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_sprout)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(plantId.toInt() + type.hashCode(), notification)

        if (prefsRepository.repeatReminders.first()) {
            val refreshedPlant = plantsRepository.observePlantById(plantId).first()
            val stillNeedsCare = when (type) {
                TYPE_WATERING -> refreshedPlant?.wateringDueAt()?.let { !it.isAfter(Instant.now()) } ?: false
                TYPE_FERTILIZER -> refreshedPlant?.fertilizerDueAt()?.let { !it.isAfter(Instant.now()) } ?: false
                else -> false
            }
            if (stillNeedsCare) {
                scheduleRepeat(plantId, type)
            }
        }

        return Result.success()
    }

    private suspend fun scheduleRepeat(plantId: Long, type: String) {
        val hour = prefsRepository.reminderHour.first()
        val minute = prefsRepository.reminderMinute.first()
        val zone = ZoneId.systemDefault()
        val tomorrowFireAt = ZonedDateTime.now(zone)
            .plusDays(1)
            .with(LocalTime.of(hour, minute))
            .toInstant()
        val delayMs = tomorrowFireAt.toEpochMilli() - Instant.now().toEpochMilli()
        if (delayMs <= 0) return

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag("plant_$plantId")
            .build()

        workManager.enqueueUniqueWork("${type}_$plantId", ExistingWorkPolicy.REPLACE, request)
    }
}
