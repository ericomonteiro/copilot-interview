package com.github.ericomonteiro.copilot

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.ericomonteiro.copilot.data.repository.SettingsRepository
import com.github.ericomonteiro.copilot.di.appModule
import com.github.ericomonteiro.copilot.hotkey.GlobalHotkeyManager
import com.github.ericomonteiro.copilot.platform.WindowManager
import com.github.ericomonteiro.copilot.ui.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import java.awt.Window as AwtWindow

fun main() = application {
    // Initialize Koin
    startKoin {
        modules(appModule)
    }
    
    // Create WindowManager for native window operations
    val windowManager = WindowManager()
    
    // Global coroutine scope
    val globalScope = remember { CoroutineScope(Dispatchers.Default) }
    
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
                println("⌨️ Stealth mode ${if (stealthModeEnabled) "ENABLED" else "DISABLED"}")
                // Save to database
                val repository = get<SettingsRepository>(SettingsRepository::class.java)
                globalScope.launch {
                    repository.setSetting("hide_from_capture", stealthModeEnabled.toString())
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
    
    // Window state for dragging
    val windowState = rememberWindowState(
        width = 800.dp,
        height = 600.dp,
        position = WindowPosition(100.dp, 100.dp)
    )
    
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
                val repository = get<SettingsRepository>(SettingsRepository::class.java)
                
                // Load stealth mode setting (default: true/enabled)
                val hideFromCapture = repository.getSetting("hide_from_capture")?.toBoolean() ?: true
                stealthModeEnabled = hideFromCapture // Update state
                windowManager.setHideFromCapture(hideFromCapture)
                println("WindowManager: Initial stealth mode loaded: ${if (hideFromCapture) "ENABLED" else "DISABLED"}")
            } else {
                println("WindowManager: ERROR - AWT window not found after $attempts attempts")
            }
        }
        
        App(
            onHideFromCaptureChanged = { hide ->
                windowManager.setHideFromCapture(hide)
                println("Stealth mode ${if (hide) "enabled" else "disabled"}")
            },
            screenshotTrigger = screenshotTrigger
        )
    }
}