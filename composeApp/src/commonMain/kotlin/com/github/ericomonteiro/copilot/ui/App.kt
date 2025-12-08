package com.github.ericomonteiro.copilot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.ericomonteiro.copilot.ui.settings.SettingsScreen
import com.github.ericomonteiro.copilot.ui.screenshot.ScreenshotAnalysisScreen

enum class Screen {
    SCREENSHOT_ANALYSIS, SETTINGS
}

@Composable
fun App(
    onHideFromCaptureChanged: (Boolean) -> Unit = {},
    screenshotTrigger: Int = 0
) {
    var currentScreen by remember { mutableStateOf(Screen.SCREENSHOT_ANALYSIS) }
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
                Screen.SCREENSHOT_ANALYSIS -> {
                    ScreenshotAnalysisScreen(
                        autoCapture = autoCapture,
                        onSettingsClick = {
                            currentScreen = Screen.SETTINGS
                        },
                        onAutoCaptureConsumed = {
                            autoCapture = false
                        }
                    )
                }
                Screen.SETTINGS -> SettingsScreen(
                    onCloseClick = {
                        currentScreen = Screen.SCREENSHOT_ANALYSIS
                    },
                    onHideFromCaptureChanged = { hide ->
                        onHideFromCaptureChanged(hide)
                    }
                )
            }
        }
    }
}
