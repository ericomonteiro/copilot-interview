package com.github.ericomonteiro.copilot.data.repository

import com.github.ericomonteiro.copilot.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SettingsRepository(private val database: Database) {
    private val queries = database.databaseQueries
    
    suspend fun getSetting(key: String): String? = withContext(Dispatchers.IO) {
        queries.getSetting(key).executeAsOneOrNull()
    }
    
    suspend fun setSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        queries.setSetting(key, value)
    }
}
