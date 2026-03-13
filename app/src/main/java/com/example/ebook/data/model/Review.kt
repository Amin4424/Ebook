package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: Int,
    val authorName: String,
    val stars: Int,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)
