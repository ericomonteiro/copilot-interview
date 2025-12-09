package com.github.ericomonteiro.copilot.di

import com.github.ericomonteiro.copilot.ai.AIService
import com.github.ericomonteiro.copilot.ai.GeminiService
import com.github.ericomonteiro.copilot.ai.HttpClientFactory
import com.github.ericomonteiro.copilot.data.repository.ScreenshotHistoryRepository
import com.github.ericomonteiro.copilot.data.repository.SettingsRepository
import com.github.ericomonteiro.copilot.db.Database
import com.github.ericomonteiro.copilot.ui.certification.CertificationAnalysisViewModel
import com.github.ericomonteiro.copilot.ui.exam.GenericExamViewModel
import com.github.ericomonteiro.copilot.ui.history.ScreenshotHistoryViewModel
import com.github.ericomonteiro.copilot.ui.settings.SettingsViewModel
import com.github.ericomonteiro.copilot.ui.screenshot.ScreenshotAnalysisViewModel
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val appModule = module {
    // Database
    single { createDatabase() }
    
    // Repositories
    single { SettingsRepository(get()) }
    single { ScreenshotHistoryRepository(get()) }
    
    // HTTP Client
    single { HttpClientFactory.create() }
    
    // AI Service - Using Gemini (FREE!)
    single<AIService> {
        val repository = get<SettingsRepository>()
        val apiKey = runBlocking { getApiKey(repository) }
        GeminiService(apiKey, get(), repository)
    }
    
    // ViewModels
    factory { SettingsViewModel(get()) }
    factory { ScreenshotAnalysisViewModel(get(), get(), get()) }
    factory { CertificationAnalysisViewModel(get(), get(), get()) }
    factory { GenericExamViewModel(get(), get(), get()) }
    factory { ScreenshotHistoryViewModel(get()) }
}

// Platform-specific database creation
expect fun createDatabase(): Database

// Get API key from settings or environment
suspend fun getApiKey(repository: SettingsRepository): String {
    return repository.getSetting("api_key") 
        ?: System.getenv("OPENAI_API_KEY") 
        ?: ""
}
