package com.github.ericomonteiro.pirateparrotai.ui.certification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ericomonteiro.pirateparrotai.ai.AIService
import com.github.ericomonteiro.pirateparrotai.ai.CertificationResponse
import com.github.ericomonteiro.pirateparrotai.ai.CertificationType
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

data class CertificationAnalysisState(
    val isLoading: Boolean = false,
    val response: CertificationResponse? = null,
    val error: String? = null,
    val selectedCertification: CertificationType = CertificationType.AWS_SOLUTIONS_ARCHITECT_ASSOCIATE,
    val screenshotBase64: String? = null,
    val isCapturing: Boolean = false
)

class CertificationAnalysisViewModel(
    private val aiService: AIService,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ScreenshotHistoryRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CertificationAnalysisState())
    val state: StateFlow<CertificationAnalysisState> = _state.asStateFlow()
    
    init {
        loadDefaultCertification()
    }
    
    private fun loadDefaultCertification() {
        viewModelScope.launch {
            val defaultCert = settingsRepository.getSetting(SettingsKeys.DEFAULT_CERTIFICATION)
            if (defaultCert != null) {
                try {
                    val certType = CertificationType.valueOf(defaultCert)
                    _state.value = _state.value.copy(selectedCertification = certType)
                } catch (e: Exception) {
                    AppLogger.debug("Invalid certification type in settings: $defaultCert")
                }
            }
        }
    }
    
    fun selectCertification(certification: CertificationType) {
        _state.value = _state.value.copy(selectedCertification = certification)
        viewModelScope.launch {
            settingsRepository.setSetting(SettingsKeys.DEFAULT_CERTIFICATION, certification.name)
        }
        if (_state.value.screenshotBase64 != null) {
            analyzeCertificationQuestion()
        }
    }
    
    fun retry() {
        analyzeCertificationQuestion()
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
                    analyzeCertificationQuestion()
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
    
    private fun analyzeCertificationQuestion() {
        val screenshot = _state.value.screenshotBase64
        if (screenshot == null) {
            _state.value = _state.value.copy(
                error = "No screenshot available. Please capture a screenshot first."
            )
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val result = aiService.analyzeCertificationQuestion(
                imageBase64 = screenshot,
                certificationType = _state.value.selectedCertification
            )
            
            result.fold(
                onSuccess = { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        response = response,
                        error = null
                    )
                    // Save to history
                    saveToHistory(screenshot, response, null)
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
    
    private fun saveToHistory(screenshot: String, response: CertificationResponse?, error: String?) {
        viewModelScope.launch {
            try {
                val resultJson = response?.let { 
                    try {
                        JsonUtils.json.encodeToString(CertificationResponse.serializer(), it)
                    } catch (e: Exception) {
                        AppLogger.error("Failed to serialize response", e)
                        // Fallback: create a simple JSON representation
                        """{"answers_count": ${it.answers.size}, "examTips": "${it.examTips.take(100)}..."}"""
                    }
                }
                AppLogger.debug("Saving certification screenshot to history. Has response: ${response != null}, Has error: ${error != null}")
                historyRepository.saveScreenshot(
                    type = ScreenshotType.CERTIFICATION,
                    screenshotBase64 = screenshot,
                    analysisResult = resultJson,
                    error = error,
                    metadata = """{"certification": "${_state.value.selectedCertification.name}"}"""
                )
                AppLogger.debug("Screenshot saved to history successfully")
            } catch (e: Exception) {
                AppLogger.error("Failed to save screenshot to history", e)
            }
        }
    }
    
    fun clearState() {
        _state.value = CertificationAnalysisState(
            selectedCertification = _state.value.selectedCertification
        )
    }
}
