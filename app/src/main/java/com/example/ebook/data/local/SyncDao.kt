package com.example.ebook.data.local

import androidx.room.*
import com.example.ebook.data.model.PendingSyncOperation
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: PendingSyncOperation)

    @Query("SELECT * FROM pending_sync_operations ORDER BY timestamp ASC")
    suspend fun getAllPendingOperations(): List<PendingSyncOperation>

    @Query("SELECT * FROM pending_sync_operations ORDER BY timestamp ASC")
    fun observePendingOperations(): Flow<List<PendingSyncOperation>>

    @Query("DELETE FROM pending_sync_operations WHERE id = :id")
    suspend fun deleteOperation(id: Long)
    
    @Query("DELETE FROM pending_sync_operations")
    suspend fun clearAll()
}
