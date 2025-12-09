package com.github.ericomonteiro.pirateparrotai.util

/**
 * Simple logging utility for the application.
 * Provides conditional logging based on debug mode.
 */
object AppLogger {
    private val isDebug: Boolean by lazy {
        System.getenv("DEBUG")?.toBoolean() ?: 
        System.getProperty("app.debug")?.toBoolean() ?: 
        true // Default to true for now, set to false in production
    }
    
    fun debug(message: String) {
        if (isDebug) {
            println("[DEBUG] $message")
        }
    }
    
    fun info(message: String) {
        println("[INFO] $message")
    }
    
    fun warn(message: String) {
        println("[WARN] $message")
    }
    
    fun error(message: String, throwable: Throwable? = null) {
        println("[ERROR] $message")
        if (isDebug && throwable != null) {
            throwable.printStackTrace()
        }
    }
}
