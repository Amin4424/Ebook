package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_progress")
data class ReadingProgress(
    @PrimaryKey
    val bookId: Int,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val chapter: String = "",
    val lastReadTimestamp: Long = System.currentTimeMillis()
)
