package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_analytics")
data class ReadingAnalytics(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val pagesRead: Int = 0,
    val durationMinutes: Int = 0
)

