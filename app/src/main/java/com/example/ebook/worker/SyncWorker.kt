package com.example.ebook.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.ebook.data.local.SyncDao
import com.example.ebook.data.model.PendingSyncOperation

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncDao: SyncDao // Assume injected via Hilt
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("SyncWorker", "Initiating Background Cloud Sync...")

            val pendingOperations = syncDao.getAllPendingOperations()
            
            if (pendingOperations.isEmpty()) {
                Log.d("SyncWorker", "No pending operations.")
                return@withContext Result.success()
            }

            for (operation in pendingOperations) {
                // Mock network push logic
                val isSuccess = pushToServer(operation) 
                
                if (isSuccess) {
                     syncDao.deleteOperation(operation.id)
                }
            }
            
            Log.d("SyncWorker", "Sync Cycle Completed Successfully.")
            Result.success()

        } catch (e: Exception) {
            Log.e("SyncWorker", "Network failure during sync, deferring.", e)
            Result.retry()
        }
    }
    
    // Abstracted mock helper function 
    private suspend fun pushToServer(operation: PendingSyncOperation): Boolean {
        // Mocking Retrofit call
        kotlinx.coroutines.delay(500)
        return true
    }
}
