package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_collections")
data class BookCollection(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

