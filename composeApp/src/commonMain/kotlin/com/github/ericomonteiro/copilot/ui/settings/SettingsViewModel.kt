package com.github.ericomonteiro.copilot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ericomonteiro.copilot.ai.GeminiService
import com.github.ericomonteiro.copilot.ai.HttpClientFactory
import com.github.ericomonteiro.copilot.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            val apiKey = repository.getSetting("api_key") ?: ""
            val hideFromCapture = repository.getSetting("hide_from_capture")?.toBoolean() ?: true
            val selectedModel = repository.getSetting("selected_model") ?: "gemini-2.5-flash"
            val defaultLanguage = repository.getSetting("default_language") ?: "Kotlin"
            
            _state.update {
                it.copy(
                    apiKey = apiKey,
                    hideFromCapture = hideFromCapture,
                    selectedModel = selectedModel,
                    defaultLanguage = defaultLanguage
                )
            }
        }
    }
    
    fun setApiKey(apiKey: String) {
        _state.update { it.copy(apiKey = apiKey) }
        viewModelScope.launch {
            repository.setSetting("api_key", apiKey)
        }
    }
    
    
    fun setHideFromCapture(hide: Boolean) {
        _state.update { it.copy(hideFromCapture = hide) }
        viewModelScope.launch {
            repository.setSetting("hide_from_capture", hide.toString())
        }
    }
    
    fun setSelectedModel(modelId: String) {
        _state.update { it.copy(selectedModel = modelId) }
        viewModelScope.launch {
            repository.setSetting("selected_model", modelId)
        }
    }
    
    fun setDefaultLanguage(language: String) {
        _state.update { it.copy(defaultLanguage = language) }
        viewModelScope.launch {
            repository.setSetting("default_language", language)
        }
    }
    
    fun loadAvailableModels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingModels = true) }
            
            val apiKey = _state.value.apiKey
            if (apiKey.isBlank()) {
                _state.update { it.copy(isLoadingModels = false) }
                return@launch
            }
            
            try {
                val httpClient = HttpClientFactory.create()
                val geminiService = GeminiService(apiKey, httpClient)
                
                geminiService.listAvailableModels()
                    .onSuccess { modelsJson ->
                        val models = parseModelsFromJson(modelsJson)
                        _state.update { it.copy(availableModels = models, isLoadingModels = false) }
                    }
                    .onFailure { error ->
                        // Keep default models on error
                        _state.update { it.copy(isLoadingModels = false) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingModels = false) }
            }
        }
    }
    
    fun testApiConnection() {
        viewModelScope.launch {
            _state.update { it.copy(testResult = "Testing...") }
            
            val apiKey = _state.value.apiKey
            if (apiKey.isBlank()) {
                _state.update { it.copy(testResult = "Error: API key is not set") }
                return@launch
            }
            
            try {
                val httpClient = HttpClientFactory.create()
                val geminiService = GeminiService(apiKey, httpClient)
                
                geminiService.listAvailableModels()
                    .onSuccess { models ->
                        _state.update { it.copy(testResult = "Available models:\n$models") }
                        // Also update available models
                        loadAvailableModels()
                    }
                    .onFailure { error ->
                        _state.update { it.copy(testResult = "Error: ${error.message}") }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(testResult = "Error: ${e.message}") }
            }
        }
    }
    
    private fun parseModelsFromJson(json: String): List<GeminiModel> {
        return try {
            val recommendedModels = setOf(
                "gemini-2.5-flash",
                "gemini-2.5-pro",
                "gemini-2.5-flash-lite",
                "gemini-flash-latest",
                "gemini-pro-latest"
            )
            
            // Simple JSON parsing to extract model names that support generateContent
            val models = mutableListOf<GeminiModel>()
            val modelPattern = """"name":\s*"models/([^"]+)".*?"supportedGenerationMethods":\s*\[([^\]]+)\]""".toRegex(RegexOption.DOT_MATCHES_ALL)
            
            modelPattern.findAll(json).forEach { match ->
                val modelName = match.groupValues[1]
                val methods = match.groupValues[2]
                
                // Only include models that support generateContent
                if (methods.contains("generateContent")) {
                    val description = when {
                        modelName == "gemini-2.5-flash" -> "⭐ Recommended - Fast and efficient"
                        modelName.contains("2.5-pro") -> "Best quality, slower"
                        modelName.contains("flash-lite") -> "Faster, lower quality"
                        modelName.contains("latest") -> "Always the latest version"
                        else -> "Available model"
                    }
                    
                    models.add(GeminiModel(
                        id = modelName,
                        displayName = modelName.replace("-", " ").split(" ")
                            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                        description = description
                    ))
                }
            }
            
            // Sort: recommended first, then alphabetically
            models.sortedWith(compareBy(
                { !recommendedModels.contains(it.id) },
                { it.id }
            ))
        } catch (e: Exception) {
            getDefaultModels()
        }
    }
}

data class SettingsState(
    val apiKey: String = "",
    val hideFromCapture: Boolean = true,
    val testResult: String? = null,
    val selectedModel: String = "gemini-2.5-flash",
    val availableModels: List<GeminiModel> = getDefaultModels(),
    val isLoadingModels: Boolean = false,
    val defaultLanguage: String = "Kotlin"
)

val AVAILABLE_LANGUAGES = listOf(
    "Kotlin", "Java", "Python", "JavaScript", "C++", "Go", "Rust",
    "MySQL", "DB2", "Oracle", "MS SQL Server"
)

data class GeminiModel(
    val id: String,
    val displayName: String,
    val description: String
)

// Default models (fallback if API call fails)
private fun getDefaultModels() = listOf(
    GeminiModel("gemini-2.5-flash", "Gemini 2.5 Flash", "⭐ Recommended - Fast and efficient"),
    GeminiModel("gemini-2.5-pro", "Gemini 2.5 Pro", "Best quality, slower"),
    GeminiModel("gemini-2.5-flash-lite", "Gemini 2.5 Flash Lite", "Faster, good quality"),
    GeminiModel("gemini-flash-latest", "Gemini Flash Latest", "Always the latest version"),
    GeminiModel("gemini-pro-latest", "Gemini Pro Latest", "Latest Pro version"),
    GeminiModel("gemini-2.0-flash", "Gemini 2.0 Flash", "Previous stable version")
)
