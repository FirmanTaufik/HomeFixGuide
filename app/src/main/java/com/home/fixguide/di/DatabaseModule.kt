package com.home.fixguide.di

import android.content.Context
import androidx.room.Room
import com.home.fixguide.data.local.AppDatabase
import com.home.fixguide.data.local.SavedGuideDao
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
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fixguide_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSavedGuideDao(
        database: AppDatabase
    ): SavedGuideDao {
        return database.savedGuideDao()
    }
}
