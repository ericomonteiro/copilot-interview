package com.github.ericomonteiro.pirateparrotai.util

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Simple logging utility for the application.
 * Provides conditional logging based on debug mode.
 * Also logs to file for debugging installed applications.
 */
object AppLogger {
    private val isDebug: Boolean by lazy {
        System.getenv("DEBUG")?.toBoolean() ?: 
        System.getProperty("app.debug")?.toBoolean() ?: 
        true // Default to true for now, set to false in production
    }
    
    private val logFile: File by lazy {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".pirate-parrot")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        File(appDir, "app.log")
    }
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    private fun writeToFile(level: String, message: String, throwable: Throwable? = null) {
        try {
            FileWriter(logFile, true).use { fw ->
                PrintWriter(fw).use { pw ->
                    val timestamp = LocalDateTime.now().format(dateFormatter)
                    pw.println("[$timestamp] [$level] $message")
                    throwable?.printStackTrace(pw)
                }
            }
        } catch (e: Exception) {
            // Ignore file write errors
        }
    }
    
    fun debug(message: String) {
        if (isDebug) {
            println("[DEBUG] $message")
            writeToFile("DEBUG", message)
        }
    }
    
    fun info(message: String) {
        println("[INFO] $message")
        writeToFile("INFO", message)
    }
    
    fun warn(message: String) {
        println("[WARN] $message")
        writeToFile("WARN", message)
    }
    
    fun error(message: String, throwable: Throwable? = null) {
        println("[ERROR] $message")
        writeToFile("ERROR", message, throwable)
        if (isDebug && throwable != null) {
            throwable.printStackTrace()
        }
    }
}
