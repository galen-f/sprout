package com.example.sprout.di

import com.example.sprout.data.repository.RoomCareEventsRepository
import com.example.sprout.data.repository.RoomPlantsRepository
import com.example.sprout.domain.repository.CareEventsRepository
import com.example.sprout.domain.repository.PlantsRepository
import com.example.sprout.notifications.ReminderScheduler
import com.example.sprout.notifications.WorkManagerReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlantsRepository(impl: RoomPlantsRepository): PlantsRepository

    @Binds
    @Singleton
    abstract fun bindCareEventsRepository(impl: RoomCareEventsRepository): CareEventsRepository

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: WorkManagerReminderScheduler): ReminderScheduler
}
