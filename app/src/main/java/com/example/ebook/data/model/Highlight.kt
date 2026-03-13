package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "highlights")
data class Highlight(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: Int,
    val page: Int,
    val startIndex: Int,
    val endIndex: Int,
    val colorHex: Long = 0xFFFFEB3B,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
