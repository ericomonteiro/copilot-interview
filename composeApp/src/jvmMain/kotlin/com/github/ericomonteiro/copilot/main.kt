package com.github.ericomonteiro.copilot

import androidx.compose.runtime.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.ericomonteiro.copilot.data.repository.ProblemRepository
import com.github.ericomonteiro.copilot.data.seedDatabase
import com.github.ericomonteiro.copilot.di.appModule
import com.github.ericomonteiro.copilot.platform.WindowManager
import com.github.ericomonteiro.copilot.ui.App
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
    
    // State for stealth mode (will be loaded from database)
    var stealthModeEnabled by remember { mutableStateOf(true) }
    
    // State for screenshot trigger (increment to trigger)
    var screenshotTrigger by remember { mutableStateOf(0) }
    
    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()
    
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
        alwaysOnTop = true,
        onKeyEvent = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                val isModifierPressed = keyEvent.isMetaPressed || keyEvent.isCtrlPressed
                
                // Handle Cmd+B (or Ctrl+B on Windows) to toggle stealth mode
                if (isModifierPressed && keyEvent.key == Key.B) {
                    // Toggle stealth mode
                    stealthModeEnabled = !stealthModeEnabled
                    windowManager.setHideFromCapture(stealthModeEnabled)
                    
                    // Save to database
                    val repository = get<ProblemRepository>(ProblemRepository::class.java)
                    coroutineScope.launch {
                        repository.setSetting("hide_from_capture", stealthModeEnabled.toString())
                    }
                    
                    println("⌨️ Hotkey: Stealth mode ${if (stealthModeEnabled) "ENABLED" else "DISABLED"} (Cmd+B)")
                    return@Window true // Event consumed
                }
                
                // Handle Cmd+Shift+S (or Ctrl+Shift+S on Windows) to capture screenshot
                if (isModifierPressed && keyEvent.isShiftPressed && keyEvent.key == Key.S) {
                    screenshotTrigger++
                    println("⌨️ Hotkey: Screenshot capture triggered (Cmd+Shift+S)")
                    return@Window true // Event consumed
                }
            }
            false // Event not consumed
        }
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
                val repository = get<ProblemRepository>(ProblemRepository::class.java)
                
                // Load stealth mode setting (default: true/enabled)
                val hideFromCapture = repository.getSetting("hide_from_capture")?.toBoolean() ?: true
                stealthModeEnabled = hideFromCapture // Update state
                windowManager.setHideFromCapture(hideFromCapture)
                println("WindowManager: Initial stealth mode loaded: ${if (hideFromCapture) "ENABLED" else "DISABLED"}")
                println("WindowManager: Keyboard shortcuts:")
                println("  • Cmd+B (or Ctrl+B) - Toggle stealth mode")
                println("  • Cmd+Shift+S (or Ctrl+Shift+S) - Capture screenshot & analyze")
            } else {
                println("WindowManager: ERROR - AWT window not found after $attempts attempts")
            }
        }
        
        // Seed database on first run
        LaunchedEffect(Unit) {
            val repository = get<ProblemRepository>(ProblemRepository::class.java)
            val problems = repository.getAllProblems()
            if (problems.isEmpty()) {
                seedDatabase(repository)
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