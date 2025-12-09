package com.github.ericomonteiro.copilot.data.repository

import com.github.ericomonteiro.copilot.db.Database
import com.github.ericomonteiro.copilot.db.ScreenshotHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

enum class ScreenshotType {
    CODE_CHALLENGE,
    CERTIFICATION,
    GENERIC_EXAM
}

class ScreenshotHistoryRepository(private val database: Database) {
    private val queries = database.databaseQueries
    
    suspend fun saveScreenshot(
        type: ScreenshotType,
        screenshotBase64: String,
        analysisResult: String? = null,
        error: String? = null,
        metadata: String? = null
    ) = withContext(Dispatchers.IO) {
        queries.insertScreenshot(
            timestamp = System.currentTimeMillis(),
            type = type.name,
            screenshotBase64 = screenshotBase64,
            analysisResult = analysisResult,
            error = error,
            metadata = metadata
        )
    }
    
    suspend fun getAllScreenshots(): List<ScreenshotHistory> = withContext(Dispatchers.IO) {
        queries.getAllScreenshots().executeAsList()
    }
    
    suspend fun getRecentScreenshots(limit: Long = 50): List<ScreenshotHistory> = withContext(Dispatchers.IO) {
        queries.getRecentScreenshots(limit).executeAsList()
    }
    
    suspend fun getScreenshotById(id: Long): ScreenshotHistory? = withContext(Dispatchers.IO) {
        queries.getScreenshotById(id).executeAsOneOrNull()
    }
    
    suspend fun deleteScreenshot(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteScreenshot(id)
    }
    
    suspend fun deleteOldScreenshots(olderThanTimestamp: Long) = withContext(Dispatchers.IO) {
        queries.deleteOldScreenshots(olderThanTimestamp)
    }
    
    suspend fun clearAllScreenshots() = withContext(Dispatchers.IO) {
        queries.clearAllScreenshots()
    }
    
    suspend fun cleanupOldScreenshots(keepDays: Int = 7) = withContext(Dispatchers.IO) {
        val cutoffTime = System.currentTimeMillis() - (keepDays * 24 * 60 * 60 * 1000L)
        queries.deleteOldScreenshots(cutoffTime)
    }
}
