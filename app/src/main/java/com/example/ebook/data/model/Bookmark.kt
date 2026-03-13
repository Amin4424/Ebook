package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: Int,
    val page: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
