package com.example.sprout.domain.model

import java.time.Instant

data class Plant(
    val id: Long = 0,
    val name: String,
    val species: String? = null,
    val coverPhotoUri: String? = null,
    val wateringIntervalDays: Int,
    val lastWateredAt: Instant? = null,
    val fertilizerIntervalDays: Int? = null,
    val lastFertilizedAt: Instant? = null,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
    val archivedAt: Instant? = null,
)
