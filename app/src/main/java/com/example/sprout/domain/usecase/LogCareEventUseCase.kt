package com.example.sprout.domain.usecase

import com.example.sprout.domain.model.CareEvent
import com.example.sprout.domain.model.CareEventType
import com.example.sprout.domain.repository.CareEventsRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class LogCareEventUseCase @Inject constructor(
    private val careEventsRepository: CareEventsRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(
        plantId: Long,
        type: CareEventType,
        note: String? = null,
        timestamp: Instant = Instant.now(clock),
    ) {
        require(type != CareEventType.WATERED && type != CareEventType.FERTILIZED) {
            "Use LogWateringUseCase or LogFertilizingUseCase for $type"
        }
        careEventsRepository.insert(
            CareEvent(plantId = plantId, type = type, timestamp = timestamp, note = note)
        )
    }
}
