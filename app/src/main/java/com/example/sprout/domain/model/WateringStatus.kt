package com.example.sprout.domain.model

sealed interface WateringStatus {
    object NeverWatered : WateringStatus
    data class DueIn(val days: Int) : WateringStatus
    object DueToday : WateringStatus
    data class OverdueBy(val days: Int) : WateringStatus
}
