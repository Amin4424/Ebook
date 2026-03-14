package com.example.ebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_sync_operations")
data class PendingSyncOperation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityId: String,
    val entityType: String, // String for simplicity, or use specific Enum Converter
    val operationType: String,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
)
