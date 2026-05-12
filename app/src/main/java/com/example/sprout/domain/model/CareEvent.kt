package com.example.sprout.domain.model

import java.time.Instant

data class CareEvent(
    val id: Long = 0,
    val plantId: Long,
    val type: CareEventType,
    val timestamp: Instant,
    val note: String? = null,
)

enum class CareEventType {
    WATERED, FERTILIZED, PH_MEASURED, REPOTTED, NOTE
}
