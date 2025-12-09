package com.github.ericomonteiro.copilot.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ericomonteiro.copilot.ai.AIService
import com.github.ericomonteiro.copilot.ai.GenericExamResponse
import com.github.ericomonteiro.copilot.ai.GenericExamType
import com.github.ericomonteiro.copilot.data.repository.ScreenshotHistoryRepository
import com.github.ericomonteiro.copilot.data.repository.ScreenshotType
import com.github.ericomonteiro.copilot.data.repository.SettingsRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.github.ericomonteiro.copilot.screenshot.captureScreenshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GenericExamState(
    val isLoading: Boolean = false,
    val response: GenericExamResponse? = null,
    val error: String? = null,
    val selectedExamType: GenericExamType = GenericExamType.ENEM,
    val additionalContext: String = "",
    val screenshotBase64: String? = null,
    val isCapturing: Boolean = false
)

class GenericExamViewModel(
    private val aiService: AIService,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ScreenshotHistoryRepository
) : ViewModel() {
    
    private val json = Json { prettyPrint = true }
    private val _state = MutableStateFlow(GenericExamState())
    val state: StateFlow<GenericExamState> = _state.asStateFlow()
    
    init {
        loadDefaultExamType()
    }
    
    private fun loadDefaultExamType() {
        viewModelScope.launch {
            val defaultExam = settingsRepository.getSetting("default_exam_type")
            if (defaultExam != null) {
                try {
                    val examType = GenericExamType.valueOf(defaultExam)
                    _state.value = _state.value.copy(selectedExamType = examType)
                } catch (e: Exception) {
                    // Use default if invalid
                }
            }
        }
    }
    
    fun selectExamType(examType: GenericExamType) {
        _state.value = _state.value.copy(selectedExamType = examType)
        viewModelScope.launch {
            settingsRepository.setSetting("default_exam_type", examType.name)
        }
        if (_state.value.screenshotBase64 != null) {
            analyzeExam()
        }
    }
    
    fun setAdditionalContext(context: String) {
        _state.value = _state.value.copy(additionalContext = context)
    }
    
    fun retry() {
        analyzeExam()
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
                    analyzeExam()
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
    
    private fun analyzeExam() {
        val screenshot = _state.value.screenshotBase64
        if (screenshot == null) {
            _state.value = _state.value.copy(
                error = "No screenshot available. Please capture a screenshot first."
            )
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val context = _state.value.additionalContext.ifBlank { null }
            
            val result = aiService.analyzeGenericExam(
                imageBase64 = screenshot,
                examType = _state.value.selectedExamType,
                additionalContext = context
            )
            
            result.fold(
                onSuccess = { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        response = response,
                        error = null
                    )
                    saveToHistory(screenshot, response, null)
                },
                onFailure = { exception ->
                    val errorMsg = exception.message ?: "Unknown error occurred"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                    saveToHistory(screenshot, null, errorMsg)
                }
            )
        }
    }
    
    private fun saveToHistory(screenshot: String, response: GenericExamResponse?, error: String?) {
        viewModelScope.launch {
            try {
                val resultJson = response?.let { 
                    try {
                        json.encodeToString(it)
                    } catch (e: Exception) {
                        println("Failed to serialize response: ${e.message}")
                        """{"answers_count": ${it.answers.size}, "studyTips": "${it.studyTips.take(100)}..."}"""
                    }
                }
                historyRepository.saveScreenshot(
                    type = ScreenshotType.GENERIC_EXAM,
                    screenshotBase64 = screenshot,
                    analysisResult = resultJson,
                    error = error,
                    metadata = """{"examType": "${_state.value.selectedExamType.name}", "context": "${_state.value.additionalContext.take(100)}"}"""
                )
            } catch (e: Exception) {
                println("Failed to save screenshot to history: ${e.message}")
            }
        }
    }
    
    fun clearState() {
        _state.value = GenericExamState(
            selectedExamType = _state.value.selectedExamType,
            additionalContext = _state.value.additionalContext
        )
    }
}
