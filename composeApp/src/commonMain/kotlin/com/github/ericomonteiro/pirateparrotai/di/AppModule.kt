package com.github.ericomonteiro.pirateparrotai.di

import com.github.ericomonteiro.pirateparrotai.ai.AIService
import com.github.ericomonteiro.pirateparrotai.ai.GeminiService
import com.github.ericomonteiro.pirateparrotai.ai.HttpClientFactory
import com.github.ericomonteiro.pirateparrotai.data.repository.ScreenshotHistoryRepository
import com.github.ericomonteiro.pirateparrotai.data.repository.SettingsRepository
import com.github.ericomonteiro.pirateparrotai.db.Database
import com.github.ericomonteiro.pirateparrotai.ui.certification.CertificationAnalysisViewModel
import com.github.ericomonteiro.pirateparrotai.ui.exam.GenericExamViewModel
import com.github.ericomonteiro.pirateparrotai.ui.history.ScreenshotHistoryViewModel
import com.github.ericomonteiro.pirateparrotai.ui.settings.SettingsViewModel
import com.github.ericomonteiro.pirateparrotai.ui.screenshot.ScreenshotAnalysisViewModel
import com.github.ericomonteiro.pirateparrotai.util.SettingsKeys
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
    // Uses lazy API key loading to avoid blocking the main thread
    single<AIService> {
        val repository = get<SettingsRepository>()
        GeminiService(
            apiKeyProvider = { repository.getSetting(SettingsKeys.API_KEY) ?: System.getenv("GEMINI_API_KEY") ?: "" },
            httpClient = get(),
            settingsRepository = repository
        )
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
