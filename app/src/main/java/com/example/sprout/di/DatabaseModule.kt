package com.example.sprout.di

import android.content.Context
import androidx.room.Room
import com.example.sprout.data.db.CareEventDao
import com.example.sprout.data.db.PlantDao
import com.example.sprout.data.db.PlantPhotoDao
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
        Room.databaseBuilder(context, SproutDatabase::class.java, "sprout.db")
            .addMigrations(SproutDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun providePlantDao(db: SproutDatabase): PlantDao = db.plantDao()

    @Provides
    fun provideCareEventDao(db: SproutDatabase): CareEventDao = db.careEventDao()

    @Provides
    fun providePlantPhotoDao(db: SproutDatabase): PlantPhotoDao = db.plantPhotoDao()
}
