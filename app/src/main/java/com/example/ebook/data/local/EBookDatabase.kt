package com.example.ebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ebook.data.model.Bookmark
import com.example.ebook.data.model.Highlight
import com.example.ebook.data.model.ReadingProgress
import com.example.ebook.data.model.Review

@Database(
    entities = [ReadingProgress::class, Bookmark::class, Highlight::class, Review::class],
    version = 3,
    exportSchema = false
)
abstract class EBookDatabase : RoomDatabase() {
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun highlightDao(): HighlightDao
    abstract fun reviewDao(): ReviewDao
}
