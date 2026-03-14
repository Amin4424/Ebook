package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey
    val userId: String = "local_user",
    val currentLevel: Int = 1,
    val currentXp: Int = 0,
    val totalReadingTimeMillis: Long = 0,
    val booksCompleted: Int = 0
)

