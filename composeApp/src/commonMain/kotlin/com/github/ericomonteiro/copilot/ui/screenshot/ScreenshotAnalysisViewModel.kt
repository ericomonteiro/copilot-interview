package com.github.ericomonteiro.copilot.ui.screenshot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ericomonteiro.copilot.ai.AIService
import com.github.ericomonteiro.copilot.ai.SolutionResponse
import com.github.ericomonteiro.copilot.screenshot.captureScreenshot
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
    private val aiService: AIService
) : ViewModel() {
    
    private val _state = MutableStateFlow(ScreenshotAnalysisState())
    val state: StateFlow<ScreenshotAnalysisState> = _state.asStateFlow()
    
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
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }
}
