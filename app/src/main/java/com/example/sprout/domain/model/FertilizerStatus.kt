package com.example.sprout.domain.model

sealed interface FertilizerStatus {
    object NotScheduled : FertilizerStatus
    object NeverFertilized : FertilizerStatus
    data class DueIn(val days: Int) : FertilizerStatus
    object DueToday : FertilizerStatus
    data class OverdueBy(val days: Int) : FertilizerStatus
}
