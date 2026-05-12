package com.example.sprout.domain.model

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun Plant.wateringDueAt(): Instant? {
    val last = lastWateredAt ?: return null
    return last.plus(wateringIntervalDays.toLong(), ChronoUnit.DAYS)
}

fun Plant.fertilizerDueAt(): Instant? {
    val intervalDays = fertilizerIntervalDays ?: return null
    val last = lastFertilizedAt ?: return null
    return last.plus(intervalDays.toLong(), ChronoUnit.DAYS)
}

fun Plant.wateringStatus(now: Instant): WateringStatus {
    val last = lastWateredAt ?: return WateringStatus.NeverWatered
    val dueAt = last.plus(wateringIntervalDays.toLong(), ChronoUnit.DAYS)
    val dueDay = dueAt.atZone(ZoneId.systemDefault()).toLocalDate()
    val today = now.atZone(ZoneId.systemDefault()).toLocalDate()
    val diff = ChronoUnit.DAYS.between(today, dueDay).toInt()
    return when {
        diff > 0 -> WateringStatus.DueIn(diff)
        diff == 0 -> WateringStatus.DueToday
        else -> WateringStatus.OverdueBy(-diff)
    }
}

fun Plant.fertilizerStatus(now: Instant): FertilizerStatus {
    val intervalDays = fertilizerIntervalDays ?: return FertilizerStatus.NotScheduled
    val last = lastFertilizedAt ?: return FertilizerStatus.NeverFertilized
    val dueAt = last.plus(intervalDays.toLong(), ChronoUnit.DAYS)
    val dueDay = dueAt.atZone(ZoneId.systemDefault()).toLocalDate()
    val today = now.atZone(ZoneId.systemDefault()).toLocalDate()
    val diff = ChronoUnit.DAYS.between(today, dueDay).toInt()
    return when {
        diff > 0 -> FertilizerStatus.DueIn(diff)
        diff == 0 -> FertilizerStatus.DueToday
        else -> FertilizerStatus.OverdueBy(-diff)
    }
}
