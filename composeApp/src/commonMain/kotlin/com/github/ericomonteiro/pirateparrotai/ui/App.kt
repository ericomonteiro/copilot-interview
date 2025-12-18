package com.github.ericomonteiro.pirateparrotai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.ericomonteiro.pirateparrotai.ui.certification.CertificationAnalysisScreen
import com.github.ericomonteiro.pirateparrotai.ui.exam.GenericExamScreen
import com.github.ericomonteiro.pirateparrotai.ui.history.ScreenshotHistoryScreen
import com.github.ericomonteiro.pirateparrotai.ui.home.HomeScreen
import com.github.ericomonteiro.pirateparrotai.ui.settings.SettingsScreen
import com.github.ericomonteiro.pirateparrotai.ui.settings.SettingsViewModel
import com.github.ericomonteiro.pirateparrotai.ui.screenshot.ScreenshotAnalysisScreen
import com.github.ericomonteiro.pirateparrotai.ui.theme.PirateParrotDarkColorScheme
import com.github.ericomonteiro.pirateparrotai.screenshot.showRegionSelector
import com.github.ericomonteiro.pirateparrotai.screenshot.ScreenshotCaptureConfig
import com.github.ericomonteiro.pirateparrotai.i18n.ProvideStrings
import org.koin.compose.koinInject

enum class Screen {
    HOME, SCREENSHOT_ANALYSIS, CERTIFICATION_ANALYSIS, GENERIC_EXAM, SETTINGS, HISTORY
}

@Composable
fun App(
    onHideFromCaptureChanged: (Boolean) -> Unit = {},
    screenshotTrigger: Int = 0
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var autoCapture by remember { mutableStateOf(false) }
    
    // Get settings for language
    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsState by settingsViewModel.state.collectAsState()
    
    // Handle screenshot request from keyboard shortcut - respects current screen context
    LaunchedEffect(screenshotTrigger) {
        if (screenshotTrigger > 0) {
            autoCapture = true
            // Don't change screen - trigger capture in current context (code or certification)
            // If on home, settings or history, go to screenshot analysis
            if (currentScreen == Screen.HOME || currentScreen == Screen.SETTINGS || currentScreen == Screen.HISTORY) {
                currentScreen = Screen.SCREENSHOT_ANALYSIS
            }
        }
    }
    
    ProvideStrings(language = settingsState.appLanguage) {
        MaterialTheme(
            colorScheme = PirateParrotDarkColorScheme
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    onCodeChallengeClick = {
                        currentScreen = Screen.SCREENSHOT_ANALYSIS
                    },
                    onCertificationClick = {
                        currentScreen = Screen.CERTIFICATION_ANALYSIS
                    },
                    onGenericExamClick = {
                        currentScreen = Screen.GENERIC_EXAM
                    },
                    onSettingsClick = {
                        currentScreen = Screen.SETTINGS
                    }
                )
                Screen.SCREENSHOT_ANALYSIS -> {
                    ScreenshotAnalysisScreen(
                        autoCapture = autoCapture,
                        onSettingsClick = {
                            currentScreen = Screen.SETTINGS
                        },
                        onCertificationClick = {
                            currentScreen = Screen.CERTIFICATION_ANALYSIS
                        },
                        onHomeClick = {
                            currentScreen = Screen.HOME
                        },
                        onAutoCaptureConsumed = {
                            autoCapture = false
                        }
                    )
                }
                Screen.CERTIFICATION_ANALYSIS -> {
                    CertificationAnalysisScreen(
                        autoCapture = autoCapture,
                        onSettingsClick = {
                            currentScreen = Screen.SETTINGS
                        },
                        onCodeChallengeClick = {
                            currentScreen = Screen.SCREENSHOT_ANALYSIS
                        },
                        onHomeClick = {
                            currentScreen = Screen.HOME
                        },
                        onAutoCaptureConsumed = {
                            autoCapture = false
                        }
                    )
                }
                Screen.GENERIC_EXAM -> {
                    GenericExamScreen(
                        autoCapture = autoCapture,
                        onSettingsClick = {
                            currentScreen = Screen.SETTINGS
                        },
                        onHomeClick = {
                            currentScreen = Screen.HOME
                        },
                        onAutoCaptureConsumed = {
                            autoCapture = false
                        }
                    )
                }
                Screen.SETTINGS -> {
                    val settingsViewModel: SettingsViewModel = koinInject()
                    SettingsScreen(
                        onCloseClick = {
                            currentScreen = Screen.HOME
                        },
                        onHideFromCaptureChanged = { hide ->
                            onHideFromCaptureChanged(hide)
                        },
                        onHistoryClick = {
                            currentScreen = Screen.HISTORY
                        },
                        onSelectCaptureRegion = {
                            showRegionSelector(
                                onRegionSelected = { region ->
                                    settingsViewModel.setCaptureRegion(region)
                                    ScreenshotCaptureConfig.captureRegion = region
                                },
                                onCancelled = {}
                            )
                        },
                        viewModel = settingsViewModel
                    )
                }
                    Screen.HISTORY -> ScreenshotHistoryScreen(
                        onBackClick = {
                            currentScreen = Screen.SETTINGS
                        }
                    )
                }
            }
        }
    }
}
