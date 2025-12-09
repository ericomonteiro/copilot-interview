package com.github.ericomonteiro.pirateparrotai.ui.screenshot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ericomonteiro.pirateparrotai.ai.AIService
import com.github.ericomonteiro.pirateparrotai.ai.SolutionResponse
import com.github.ericomonteiro.pirateparrotai.data.repository.ScreenshotHistoryRepository
import com.github.ericomonteiro.pirateparrotai.data.repository.ScreenshotType
import com.github.ericomonteiro.pirateparrotai.data.repository.SettingsRepository
import com.github.ericomonteiro.pirateparrotai.screenshot.captureScreenshot
import com.github.ericomonteiro.pirateparrotai.util.AppLogger
import com.github.ericomonteiro.pirateparrotai.util.JsonUtils
import com.github.ericomonteiro.pirateparrotai.util.SettingsKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScreenshotAnalysisState(
    val isLoading: Boolean = false,
    val solution: SolutionResponse? = null,
    val error: String? = null,
    val selectedLanguage: String = "Kotlin",
    val screenshotBase64: String? = null,
    val isCapturing: Boolean = false
)

class ScreenshotAnalysisViewModel(
    private val aiService: AIService,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ScreenshotHistoryRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ScreenshotAnalysisState())
    val state: StateFlow<ScreenshotAnalysisState> = _state.asStateFlow()
    
    init {
        loadDefaultLanguage()
    }
    
    private fun loadDefaultLanguage() {
        viewModelScope.launch {
            val defaultLanguage = settingsRepository.getSetting(SettingsKeys.DEFAULT_LANGUAGE) ?: "Kotlin"
            _state.value = _state.value.copy(selectedLanguage = defaultLanguage)
        }
    }
    
    fun selectLanguage(language: String) {
        _state.value = _state.value.copy(selectedLanguage = language)
        analyzeCodingChallenge()
    }
    
    fun retry() {
        analyzeCodingChallenge()
    }
    
    fun captureAndAnalyze() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCapturing = true, error = null)
            
            captureScreenshot().fold(
                onSuccess = { base64 ->
                    _state.value = _state.value.copy(
                        screenshotBase64 = base64,
                        isCapturing = false
                    )
                    analyzeCodingChallenge()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isCapturing = false,
                        error = "Failed to capture screenshot: ${error.message}"
                    )
                }
            )
        }
    }
    
    private fun analyzeCodingChallenge() {
        val screenshot = _state.value.screenshotBase64
        if (screenshot == null) {
            _state.value = _state.value.copy(
                error = "No screenshot available. Please capture a screenshot first."
            )
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val result = aiService.analyzeCodingChallenge(
                imageBase64 = screenshot,
                language = _state.value.selectedLanguage
            )
            
            result.fold(
                onSuccess = { solution ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        solution = solution,
                        error = null
                    )
                    // Save to history
                    saveToHistory(screenshot, solution, null)
                },
                onFailure = { exception ->
                    val errorMsg = exception.message ?: "Unknown error occurred"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                    // Save error to history
                    saveToHistory(screenshot, null, errorMsg)
                }
            )
        }
    }
    
    private fun saveToHistory(screenshot: String, solution: SolutionResponse?, error: String?) {
        viewModelScope.launch {
            try {
                historyRepository.saveScreenshot(
                    type = ScreenshotType.CODE_CHALLENGE,
                    screenshotBase64 = screenshot,
                    analysisResult = solution?.let { JsonUtils.json.encodeToString(SolutionResponse.serializer(), it) },
                    error = error,
                    metadata = """{"language": "${_state.value.selectedLanguage}"}"""
                )
            } catch (e: Exception) {
                AppLogger.error("Failed to save screenshot to history", e)
            }
        }
    }
}
