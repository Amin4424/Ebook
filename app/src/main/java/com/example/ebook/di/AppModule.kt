package com.example.ebook.di

import android.content.Context
import androidx.room.Room
import com.example.ebook.data.local.BookmarkDao
import com.example.ebook.data.local.EBookDatabase
import com.example.ebook.data.local.HighlightDao
import com.example.ebook.data.local.ReadingProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EBookDatabase {
        return Room.databaseBuilder(
            context,
            EBookDatabase::class.java,
            "ebook_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideReadingProgressDao(database: EBookDatabase): ReadingProgressDao {
        return database.readingProgressDao()
    }

    @Provides
    fun provideBookmarkDao(database: EBookDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    fun provideHighlightDao(database: EBookDatabase): HighlightDao {
        return database.highlightDao()
    }
}
