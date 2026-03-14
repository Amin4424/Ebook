package com.example.ebook.data.local

import androidx.room.*
import com.example.ebook.data.model.ReadingAnalytics
import com.example.ebook.data.model.UserStats
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingSession(analytics: ReadingAnalytics)

    @Query("SELECT * FROM reading_analytics ORDER BY date DESC")
    fun getAllSessions(): Flow<List<ReadingAnalytics>>

    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    fun getUserStats(userId: String = "local_user"): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Update
    suspend fun updateUserStats(stats: UserStats)
}

