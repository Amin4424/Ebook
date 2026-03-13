package com.example.ebook.data.local

import androidx.room.*
import com.example.ebook.data.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {

    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    fun getProgress(bookId: Int): Flow<ReadingProgress?>

    @Query("SELECT * FROM reading_progress ORDER BY lastReadTimestamp DESC LIMIT 1")
    fun getLastRead(): Flow<ReadingProgress?>

    @Query("SELECT * FROM reading_progress ORDER BY lastReadTimestamp DESC")
    fun getAllProgress(): Flow<List<ReadingProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ReadingProgress)

    @Delete
    suspend fun deleteProgress(progress: ReadingProgress)
}
