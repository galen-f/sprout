package com.example.sprout.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val WATERING = "watering_reminders"
    const val FERTILIZING = "fertilizing_reminders"

    fun createAll(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                WATERING,
                "Watering Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = "Reminds you when plants need watering" }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                FERTILIZING,
                "Fertilizing Reminders",
                NotificationManager.IMPORTANCE_LOW,
            ).apply { description = "Reminds you when plants need fertilizing" }
        )
    }
}
