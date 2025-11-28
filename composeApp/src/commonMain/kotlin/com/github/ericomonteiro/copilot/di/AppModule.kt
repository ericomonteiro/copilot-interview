package com.github.ericomonteiro.copilot.di

import com.github.ericomonteiro.copilot.ai.AIService
import com.github.ericomonteiro.copilot.ai.GeminiService
import com.github.ericomonteiro.copilot.ai.HttpClientFactory
import com.github.ericomonteiro.copilot.ai.OpenAIService
import com.github.ericomonteiro.copilot.data.repository.ProblemRepository
import com.github.ericomonteiro.copilot.db.Database
import com.github.ericomonteiro.copilot.ui.search.SearchViewModel
import com.github.ericomonteiro.copilot.ui.settings.SettingsViewModel
import com.github.ericomonteiro.copilot.ui.solution.SolutionViewModel
import com.github.ericomonteiro.copilot.ui.screenshot.ScreenshotAnalysisViewModel
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    // Database
    single { createDatabase() }
    
    // Repository
    single { ProblemRepository(get()) }
    
    // HTTP Client
    single { HttpClientFactory.create() }
    
    // AI Service - Using Gemini (FREE!)
    single<AIService> {
        val repository = get<ProblemRepository>()
        val apiKey = runBlocking { getApiKey(repository) }
        val selectedModel = runBlocking { repository.getSetting("selected_model") ?: "gemini-2.5-flash" }
        // Use Gemini with user-selected model
        GeminiService(apiKey, get(), selectedModel)
        // To use OpenAI instead, uncomment below and comment Gemini:
        // OpenAIService(apiKey, get())
    }
    
    // ViewModels
    factory { SearchViewModel(get()) }
    factory { (problemId: Long) -> 
        SolutionViewModel(problemId, get(), get())
    }
    factory { SettingsViewModel(get()) }
    factory { ScreenshotAnalysisViewModel(get()) }
}

// Platform-specific database creation
expect fun createDatabase(): Database

// Get API key from settings or environment
suspend fun getApiKey(repository: ProblemRepository): String {
    return repository.getSetting("api_key") 
        ?: System.getenv("OPENAI_API_KEY") 
        ?: ""
}
