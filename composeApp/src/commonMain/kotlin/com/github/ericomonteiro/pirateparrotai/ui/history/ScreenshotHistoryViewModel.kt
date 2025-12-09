package com.github.ericomonteiro.pirateparrotai.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ericomonteiro.pirateparrotai.data.repository.ScreenshotHistoryRepository
import com.github.ericomonteiro.pirateparrotai.db.ScreenshotHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScreenshotHistoryState(
    val isLoading: Boolean = false,
    val screenshots: List<ScreenshotHistory> = emptyList(),
    val selectedScreenshot: ScreenshotHistory? = null,
    val error: String? = null
)

class ScreenshotHistoryViewModel(
    private val historyRepository: ScreenshotHistoryRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ScreenshotHistoryState())
    val state: StateFlow<ScreenshotHistoryState> = _state.asStateFlow()
    
    init {
        loadHistory()
    }
    
    fun loadHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val screenshots = historyRepository.getRecentScreenshots(100)
                _state.value = _state.value.copy(
                    isLoading = false,
                    screenshots = screenshots,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load history: ${e.message}"
                )
            }
        }
    }
    
    fun selectScreenshot(screenshot: ScreenshotHistory?) {
        _state.value = _state.value.copy(selectedScreenshot = screenshot)
    }
    
    fun deleteScreenshot(id: Long) {
        viewModelScope.launch {
            try {
                historyRepository.deleteScreenshot(id)
                loadHistory()
                if (_state.value.selectedScreenshot?.id == id) {
                    _state.value = _state.value.copy(selectedScreenshot = null)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to delete screenshot: ${e.message}"
                )
            }
        }
    }
    
    fun clearAllHistory() {
        viewModelScope.launch {
            try {
                historyRepository.clearAllScreenshots()
                _state.value = _state.value.copy(
                    screenshots = emptyList(),
                    selectedScreenshot = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to clear history: ${e.message}"
                )
            }
        }
    }
    
    fun cleanupOldScreenshots(days: Int = 7) {
        viewModelScope.launch {
            try {
                historyRepository.cleanupOldScreenshots(days)
                loadHistory()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to cleanup old screenshots: ${e.message}"
                )
            }
        }
    }
}
