package com.example.ebook.data.model

import androidx.room.Entity

@Entity(
    tableName = "book_collection_cross_ref",
    primaryKeys = ["collectionId", "bookId"]
)
data class BookCollectionCrossRef(
    val collectionId: Long,
    val bookId: String
)

