package com.example.sprout.di

import android.content.Context
import androidx.room.Room
import com.example.sprout.data.db.CareEventDao
import com.example.sprout.data.db.PlantDao
import com.example.sprout.data.db.SproutDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SproutDatabase =
        Room.databaseBuilder(context, SproutDatabase::class.java, "sprout.db").build()

    @Provides
    fun providePlantDao(db: SproutDatabase): PlantDao = db.plantDao()

    @Provides
    fun provideCareEventDao(db: SproutDatabase): CareEventDao = db.careEventDao()
}
