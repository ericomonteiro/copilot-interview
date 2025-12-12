package com.github.ericomonteiro.pirateparrotai.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ericomonteiro.pirateparrotai.ai.GeminiService
import com.github.ericomonteiro.pirateparrotai.ai.HttpClientFactory
import com.github.ericomonteiro.pirateparrotai.data.repository.SettingsRepository
import com.github.ericomonteiro.pirateparrotai.util.AppLogger
import com.github.ericomonteiro.pirateparrotai.screenshot.CaptureRegion
import com.github.ericomonteiro.pirateparrotai.util.SettingsKeys
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
            val apiKey = repository.getSetting(SettingsKeys.API_KEY) ?: ""
            val hideFromCapture = repository.getSetting(SettingsKeys.HIDE_FROM_CAPTURE)?.toBoolean() ?: true
            val selectedModel = repository.getSetting(SettingsKeys.SELECTED_MODEL) ?: "gemini-2.5-flash"
            val defaultLanguage = repository.getSetting(SettingsKeys.DEFAULT_LANGUAGE) ?: "Kotlin"
            
            // Load capture region settings
            val captureRegionEnabled = repository.getSetting(SettingsKeys.CAPTURE_REGION_ENABLED)?.toBoolean() ?: false
            val captureRegion = if (captureRegionEnabled) {
                CaptureRegion(
                    x = repository.getSetting(SettingsKeys.CAPTURE_REGION_X)?.toIntOrNull() ?: 0,
                    y = repository.getSetting(SettingsKeys.CAPTURE_REGION_Y)?.toIntOrNull() ?: 0,
                    width = repository.getSetting(SettingsKeys.CAPTURE_REGION_WIDTH)?.toIntOrNull() ?: 0,
                    height = repository.getSetting(SettingsKeys.CAPTURE_REGION_HEIGHT)?.toIntOrNull() ?: 0
                )
            } else null
            
            _state.update {
                it.copy(
                    apiKey = apiKey,
                    hideFromCapture = hideFromCapture,
                    selectedModel = selectedModel,
                    defaultLanguage = defaultLanguage,
                    captureRegionEnabled = captureRegionEnabled,
                    captureRegion = captureRegion
                )
            }
        }
    }
    
    fun setApiKey(apiKey: String) {
        _state.update { it.copy(apiKey = apiKey) }
        viewModelScope.launch {
            repository.setSetting(SettingsKeys.API_KEY, apiKey)
        }
    }
    
    fun setHideFromCapture(hide: Boolean) {
        _state.update { it.copy(hideFromCapture = hide) }
        viewModelScope.launch {
            repository.setSetting(SettingsKeys.HIDE_FROM_CAPTURE, hide.toString())
        }
    }
    
    fun setSelectedModel(modelId: String) {
        _state.update { it.copy(selectedModel = modelId) }
        viewModelScope.launch {
            repository.setSetting(SettingsKeys.SELECTED_MODEL, modelId)
        }
    }
    
    fun setDefaultLanguage(language: String) {
        _state.update { it.copy(defaultLanguage = language) }
        viewModelScope.launch {
            repository.setSetting(SettingsKeys.DEFAULT_LANGUAGE, language)
        }
    }
    
    fun setCaptureRegionEnabled(enabled: Boolean) {
        _state.update { it.copy(captureRegionEnabled = enabled) }
        viewModelScope.launch {
            repository.setSetting(SettingsKeys.CAPTURE_REGION_ENABLED, enabled.toString())
        }
    }
    
    fun setCaptureRegion(region: CaptureRegion?) {
        _state.update { 
            it.copy(
                captureRegion = region,
                captureRegionEnabled = region != null && region.isValid()
            ) 
        }
        viewModelScope.launch {
            if (region != null && region.isValid()) {
                repository.setSetting(SettingsKeys.CAPTURE_REGION_ENABLED, "true")
                repository.setSetting(SettingsKeys.CAPTURE_REGION_X, region.x.toString())
                repository.setSetting(SettingsKeys.CAPTURE_REGION_Y, region.y.toString())
                repository.setSetting(SettingsKeys.CAPTURE_REGION_WIDTH, region.width.toString())
                repository.setSetting(SettingsKeys.CAPTURE_REGION_HEIGHT, region.height.toString())
            } else {
                repository.setSetting(SettingsKeys.CAPTURE_REGION_ENABLED, "false")
            }
        }
    }
    
    fun clearCaptureRegion() {
        _state.update { it.copy(captureRegion = null, captureRegionEnabled = false) }
        viewModelScope.launch {
            repository.setSetting(SettingsKeys.CAPTURE_REGION_ENABLED, "false")
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
                val geminiService = GeminiService(
                    apiKeyProvider = { apiKey },
                    httpClient = httpClient,
                    settingsRepository = repository
                )
                
                geminiService.listAvailableModels()
                    .onSuccess { modelsJson ->
                        val models = parseModelsFromJson(modelsJson)
                        _state.update { it.copy(availableModels = models, isLoadingModels = false) }
                    }
                    .onFailure { error ->
                        AppLogger.error("Failed to load models", error)
                        _state.update { it.copy(isLoadingModels = false) }
                    }
            } catch (e: Exception) {
                AppLogger.error("Failed to load models", e)
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
                val geminiService = GeminiService(
                    apiKeyProvider = { apiKey },
                    httpClient = httpClient,
                    settingsRepository = repository
                )
                
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
                AppLogger.error("API connection test failed", e)
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
    val defaultLanguage: String = "Kotlin",
    val captureRegionEnabled: Boolean = false,
    val captureRegion: CaptureRegion? = null
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
