package com.example.ebook.data.local

import androidx.room.*
import com.example.ebook.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getReviews(bookId: Int): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReview(review: Review)

    @Delete
    suspend fun deleteReview(review: Review)
}
