package com.example.ebook.di

import android.content.Context
import androidx.room.Room
import com.example.ebook.data.local.*
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
    fun provideReadingProgressDao(database: EBookDatabase): ReadingProgressDao = database.readingProgressDao()

    @Provides
    fun provideBookmarkDao(database: EBookDatabase): BookmarkDao = database.bookmarkDao()

    @Provides
    fun provideHighlightDao(database: EBookDatabase): HighlightDao = database.highlightDao()

    @Provides
    fun provideReviewDao(database: EBookDatabase): ReviewDao = database.reviewDao()
}
