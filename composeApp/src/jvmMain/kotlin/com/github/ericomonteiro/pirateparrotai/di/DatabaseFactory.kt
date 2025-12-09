package com.github.ericomonteiro.pirateparrotai.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.github.ericomonteiro.pirateparrotai.db.Database
import com.github.ericomonteiro.pirateparrotai.util.AppLogger
import java.io.File

actual fun createDatabase(): Database {
    val databasePath = getDatabasePath()
    val dbFile = File(databasePath)
    val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")
    
    if (!dbFile.exists()) {
        // Create new database with full schema
        Database.Schema.create(driver)
    } else {
        // Ensure ScreenshotHistory table exists for existing databases
        ensureScreenshotHistoryTable(driver)
    }
    
    return Database(driver)
}

private fun ensureScreenshotHistoryTable(driver: JdbcSqliteDriver) {
    try {
        driver.execute(null, """
            CREATE TABLE IF NOT EXISTS ScreenshotHistory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp INTEGER NOT NULL,
                type TEXT NOT NULL,
                screenshotBase64 TEXT NOT NULL,
                analysisResult TEXT,
                error TEXT,
                metadata TEXT
            )
        """.trimIndent(), 0)
        AppLogger.debug("ScreenshotHistory table ensured")
    } catch (e: Exception) {
        AppLogger.error("Error ensuring ScreenshotHistory table: ${e.message}", e)
    }
}

private fun getDatabasePath(): String {
    val userHome = System.getProperty("user.home")
    val appDir = File(userHome, ".pirate-parrot")
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    return File(appDir, "database.db").absolutePath
}
