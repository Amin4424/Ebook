package com.example.ebook.data.local

import androidx.room.*
import com.example.ebook.data.model.Highlight
import kotlinx.coroutines.flow.Flow

@Dao
interface HighlightDao {

    @Query("SELECT * FROM highlights WHERE bookId = :bookId AND page = :page ORDER BY startIndex ASC")
    fun getHighlights(bookId: Int, page: Int): Flow<List<Highlight>>

    @Query("SELECT * FROM highlights WHERE bookId = :bookId ORDER BY page ASC, startIndex ASC")
    fun getAllHighlights(bookId: Int): Flow<List<Highlight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHighlight(highlight: Highlight)

    @Delete
    suspend fun removeHighlight(highlight: Highlight)
}
