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
import androidx.work.WorkerParameters
import com.example.sprout.MainActivity
import com.example.sprout.R
import com.example.sprout.domain.repository.PlantsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val plantsRepository: PlantsRepository,
    private val notificationManager: NotificationManagerCompat,
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

        val plant = plantsRepository.observePlantById(plantId).firstOrNull() ?: return Result.success()

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
        return Result.success()
    }
}
