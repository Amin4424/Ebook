package com.example.ebook.data.local

import androidx.room.*
import com.example.ebook.data.model.BookCollection
import com.example.ebook.data.model.BookCollectionCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: BookCollection): Long

    @Delete
    suspend fun deleteCollection(collection: BookCollection)

    @Query("SELECT * FROM book_collections ORDER BY createdAt DESC")
    fun getAllCollections(): Flow<List<BookCollection>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBookToCollection(crossRef: BookCollectionCrossRef)

    @Delete
    suspend fun removeBookFromCollection(crossRef: BookCollectionCrossRef)

    @Query("SELECT bookId FROM book_collection_cross_ref WHERE collectionId = :collectionId")
    fun getBookIdsInCollection(collectionId: Long): Flow<List<String>>
}

