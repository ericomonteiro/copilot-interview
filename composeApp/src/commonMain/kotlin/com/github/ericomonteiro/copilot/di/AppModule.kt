package com.github.ericomonteiro.copilot.di

import com.github.ericomonteiro.copilot.ai.AIService
import com.github.ericomonteiro.copilot.ai.GeminiService
import com.github.ericomonteiro.copilot.ai.HttpClientFactory
import com.github.ericomonteiro.copilot.data.repository.SettingsRepository
import com.github.ericomonteiro.copilot.db.Database
import com.github.ericomonteiro.copilot.ui.settings.SettingsViewModel
import com.github.ericomonteiro.copilot.ui.screenshot.ScreenshotAnalysisViewModel
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val appModule = module {
    // Database
    single { createDatabase() }
    
    // Repository
    single { SettingsRepository(get()) }
    
    // HTTP Client
    single { HttpClientFactory.create() }
    
    // AI Service - Using Gemini (FREE!)
    single<AIService> {
        val repository = get<SettingsRepository>()
        val apiKey = runBlocking { getApiKey(repository) }
        val selectedModel = runBlocking { repository.getSetting("selected_model") ?: "gemini-2.5-flash" }
        GeminiService(apiKey, get(), selectedModel)
    }
    
    // ViewModels
    factory { SettingsViewModel(get()) }
    factory { ScreenshotAnalysisViewModel(get(), get()) }
}

// Platform-specific database creation
expect fun createDatabase(): Database

// Get API key from settings or environment
suspend fun getApiKey(repository: SettingsRepository): String {
    return repository.getSetting("api_key") 
        ?: System.getenv("OPENAI_API_KEY") 
        ?: ""
}
