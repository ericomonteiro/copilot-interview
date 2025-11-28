package com.github.ericomonteiro.copilot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.ericomonteiro.copilot.ui.search.SearchScreen
import com.github.ericomonteiro.copilot.ui.solution.SolutionScreen
import com.github.ericomonteiro.copilot.ui.settings.SettingsScreen
import com.github.ericomonteiro.copilot.ui.screenshot.ScreenshotAnalysisScreen

enum class Screen {
    SEARCH, SOLUTION, SETTINGS, SCREENSHOT_ANALYSIS
}

@Composable
fun App(
    onHideFromCaptureChanged: (Boolean) -> Unit = {},
    screenshotTrigger: Int = 0
) {
    var currentScreen by remember { mutableStateOf(Screen.SEARCH) }
    var selectedProblemId by remember { mutableStateOf<Long?>(null) }
    var autoCapture by remember { mutableStateOf(false) }
    
    // Handle screenshot request from keyboard shortcut
    LaunchedEffect(screenshotTrigger) {
        if (screenshotTrigger > 0) {
            autoCapture = true
            currentScreen = Screen.SCREENSHOT_ANALYSIS
        }
    }
    
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.SEARCH -> SearchScreen(
                    onProblemSelected = { problemId ->
                        selectedProblemId = problemId
                        currentScreen = Screen.SOLUTION
                    },
                    onSettingsClick = {
                        currentScreen = Screen.SETTINGS
                    },
                    onScreenshotClick = {
                        currentScreen = Screen.SCREENSHOT_ANALYSIS
                    }
                )
                Screen.SOLUTION -> SolutionScreen(
                    problemId = selectedProblemId ?: 0,
                    onBackClick = {
                        currentScreen = Screen.SEARCH
                    }
                )
                Screen.SETTINGS -> SettingsScreen(
                    onCloseClick = {
                        currentScreen = Screen.SEARCH
                    },
                    onHideFromCaptureChanged = { hide ->
                        onHideFromCaptureChanged(hide)
                    }
                )
                Screen.SCREENSHOT_ANALYSIS -> {
                    ScreenshotAnalysisScreen(
                        autoCapture = autoCapture,
                        onBackClick = {
                            autoCapture = false
                            currentScreen = Screen.SEARCH
                        }
                    )
                }
            }
        }
    }
}
