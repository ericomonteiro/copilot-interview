package com.github.ericomonteiro.copilot

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.ericomonteiro.copilot.data.repository.SettingsRepository
import com.github.ericomonteiro.copilot.di.appModule
import com.github.ericomonteiro.copilot.hotkey.GlobalHotkeyManager
import com.github.ericomonteiro.copilot.platform.WindowManager
import com.github.ericomonteiro.copilot.screenshot.ScreenshotCaptureConfig
import com.github.ericomonteiro.copilot.ui.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import java.awt.Window as AwtWindow

// Initialize Koin once at startup (outside composable)
private val koinApp by lazy {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(appModule)
        }
    }
}

// Load saved window state once at startup (outside composable)
private val savedWindowState: WindowSavedState by lazy {
    koinApp // Ensure Koin is initialized first
    val repository = get<SettingsRepository>(SettingsRepository::class.java)
    runBlocking {
        val width = repository.getSetting("window_width")?.toFloatOrNull() ?: 800f
        val height = repository.getSetting("window_height")?.toFloatOrNull() ?: 600f
        val x = repository.getSetting("window_x")?.toFloatOrNull() ?: 100f
        val y = repository.getSetting("window_y")?.toFloatOrNull() ?: 100f
        println("Loading window state: ${width}x${height} at ($x, $y)")
        WindowSavedState(width, height, x, y)
    }
}

fun main() {
    // Initialize Koin and load window state before application starts
    koinApp
    val initialWindowState = savedWindowState
    
    application {
        // Create WindowManager for native window operations
        val windowManager = WindowManager()
        
        // Configure screenshot capture to use WindowManager
        ScreenshotCaptureConfig.windowManager = windowManager
        
        // Global coroutine scope
        val globalScope = remember { CoroutineScope(Dispatchers.Default) }
        
        // Get repository for saving
        val repository = get<SettingsRepository>(SettingsRepository::class.java)
        
        // State for stealth mode (will be loaded from database)
        var stealthModeEnabled by remember { mutableStateOf(true) }
        
        // State for screenshot trigger (increment to trigger)
        var screenshotTrigger by remember { mutableStateOf(0) }
        
        // Global hotkey manager for all shortcuts
        val globalHotkeyManager = remember {
            GlobalHotkeyManager(
                onScreenshotHotkey = {
                    screenshotTrigger++
                },
                onStealthHotkey = {
                    stealthModeEnabled = !stealthModeEnabled
                    windowManager.setHideFromCapture(stealthModeEnabled)
                    ScreenshotCaptureConfig.wasStealthEnabled = stealthModeEnabled
                    println("⌨️ Stealth mode ${if (stealthModeEnabled) "ENABLED" else "DISABLED"}")
                    // Save to database
                    val repo = get<SettingsRepository>(SettingsRepository::class.java)
                    globalScope.launch {
                        repo.setSetting("hide_from_capture", stealthModeEnabled.toString())
                    }
                }
            )
        }
        
        // Register global hotkeys on startup
        LaunchedEffect(Unit) {
            globalHotkeyManager.register()
        }
        
        // Cleanup on exit
        DisposableEffect(Unit) {
            onDispose {
                globalHotkeyManager.unregister()
            }
        }
        
        // Window state for dragging - use saved values
        val windowState = rememberWindowState(
            size = DpSize(initialWindowState.width.dp, initialWindowState.height.dp),
            position = WindowPosition(initialWindowState.x.dp, initialWindowState.y.dp)
        )
        
        // Save window state when it changes
        LaunchedEffect(windowState.size, windowState.position) {
            // Debounce saving to avoid too many writes
            kotlinx.coroutines.delay(500)
            globalScope.launch {
                repository.setSetting("window_width", windowState.size.width.value.toString())
                repository.setSetting("window_height", windowState.size.height.value.toString())
                if (windowState.position is WindowPosition.Absolute) {
                    val pos = windowState.position as WindowPosition.Absolute
                    repository.setSetting("window_x", pos.x.value.toString())
                    repository.setSetting("window_y", pos.y.value.toString())
                }
            }
        }
        
        Window(
            onCloseRequest = ::exitApplication,
            title = "Interview Assistant",
            state = windowState,
            undecorated = false,
            transparent = false,
            resizable = true,
            alwaysOnTop = true
        ) {
            // Initialize WindowManager with the AWT window
            LaunchedEffect(Unit) {
                // Wait for AWT window to be ready (retry up to 10 times)
                var awtWindow: AwtWindow? = null
                var attempts = 0
                while (awtWindow == null && attempts < 10) {
                    awtWindow = AwtWindow.getWindows().firstOrNull { it.isShowing }
                    if (awtWindow == null) {
                        kotlinx.coroutines.delay(100) // Wait 100ms before retry
                        attempts++
                    }
                }
                
                if (awtWindow != null) {
                    windowManager.setWindow(awtWindow)
                    println("WindowManager: AWT window set successfully (attempt ${attempts + 1})")
                    
                    // Load and apply initial settings from database
                    val repo = get<SettingsRepository>(SettingsRepository::class.java)
                    
                    // Load stealth mode setting (default: true/enabled)
                    val hideFromCapture = repo.getSetting("hide_from_capture")?.toBoolean() ?: true
                    stealthModeEnabled = hideFromCapture // Update state
                    ScreenshotCaptureConfig.wasStealthEnabled = hideFromCapture
                    windowManager.setHideFromCapture(hideFromCapture)
                    println("WindowManager: Initial stealth mode loaded: ${if (hideFromCapture) "ENABLED" else "DISABLED"}")
                } else {
                    println("WindowManager: ERROR - AWT window not found after $attempts attempts")
                }
            }
            
            App(
                onHideFromCaptureChanged = { hide ->
                    windowManager.setHideFromCapture(hide)
                    ScreenshotCaptureConfig.wasStealthEnabled = hide
                    stealthModeEnabled = hide
                    println("Stealth mode ${if (hide) "enabled" else "disabled"}")
                },
                screenshotTrigger = screenshotTrigger
            )
        }
    }
}

private data class WindowSavedState(
    val width: Float,
    val height: Float,
    val x: Float,
    val y: Float
)