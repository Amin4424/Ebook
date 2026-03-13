package com.example.ebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ebook.data.model.Bookmark
import com.example.ebook.data.model.ReadingProgress

@Database(
    entities = [ReadingProgress::class, Bookmark::class],
    version = 1,
    exportSchema = false
)
abstract class EBookDatabase : RoomDatabase() {
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun bookmarkDao(): BookmarkDao
}
