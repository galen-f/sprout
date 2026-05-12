package com.example.sprout.data.db

import com.example.sprout.domain.model.CareEvent
import com.example.sprout.domain.model.CareEventType
import com.example.sprout.domain.model.Plant
import java.time.Instant

fun PlantEntity.toDomain(): Plant = Plant(
    id = id,
    name = name,
    species = species,
    coverPhotoUri = coverPhotoUri,
    wateringIntervalDays = wateringIntervalDays,
    lastWateredAt = lastWateredAt?.let { Instant.ofEpochMilli(it) },
    fertilizerIntervalDays = fertilizerIntervalDays,
    lastFertilizedAt = lastFertilizedAt?.let { Instant.ofEpochMilli(it) },
    notes = notes,
    createdAt = Instant.ofEpochMilli(createdAt),
    archivedAt = archivedAt?.let { Instant.ofEpochMilli(it) },
)

fun Plant.toEntity(): PlantEntity = PlantEntity(
    id = id,
    name = name,
    species = species,
    coverPhotoUri = coverPhotoUri,
    wateringIntervalDays = wateringIntervalDays,
    lastWateredAt = lastWateredAt?.toEpochMilli(),
    fertilizerIntervalDays = fertilizerIntervalDays,
    lastFertilizedAt = lastFertilizedAt?.toEpochMilli(),
    notes = notes,
    createdAt = createdAt.toEpochMilli(),
    archivedAt = archivedAt?.toEpochMilli(),
)

fun CareEventEntity.toDomain(): CareEvent = CareEvent(
    id = id,
    plantId = plantId,
    type = CareEventType.valueOf(type),
    timestamp = Instant.ofEpochMilli(timestamp),
    note = note,
)

fun CareEvent.toEntity(): CareEventEntity = CareEventEntity(
    id = id,
    plantId = plantId,
    type = type.name,
    timestamp = timestamp.toEpochMilli(),
    note = note,
)
