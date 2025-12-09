package com.github.ericomonteiro.pirateparrotai.hotkey

import com.github.ericomonteiro.pirateparrotai.util.AppLogger
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger

private val isMacOS = System.getProperty("os.name").lowercase().contains("mac")
private val isWindows = System.getProperty("os.name").lowercase().contains("win")

class GlobalHotkeyManager(
    private val onScreenshotHotkey: () -> Unit,
    private val onStealthHotkey: () -> Unit
) : NativeKeyListener {
    
    fun register() {
        try {
            // Disable JNativeHook logging
            val logger = Logger.getLogger(GlobalScreen::class.java.`package`.name)
            logger.level = Level.OFF
            logger.useParentHandlers = false
            
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(this)
            AppLogger.info("GlobalHotkeyManager: ✅ Registered global hotkeys successfully")
            if (isMacOS) {
                AppLogger.info("  • Cmd+Shift+Opt+S - Capture screenshot & analyze (auto-copy to clipboard)")
                AppLogger.info("  • Cmd+Shift+Opt+B - Toggle stealth mode")
                AppLogger.info("  ⚠️ On macOS: Grant Accessibility permission in System Preferences > Security & Privacy > Privacy > Accessibility")
            } else if (isWindows) {
                AppLogger.info("  • Ctrl+Shift+Alt+S - Capture screenshot & analyze (auto-copy to clipboard)")
                AppLogger.info("  • Ctrl+Shift+Alt+B - Toggle stealth mode")
            }
        } catch (e: Exception) {
            AppLogger.error("GlobalHotkeyManager: ❌ Failed to register - ${e.message}", e)
            if (isMacOS) {
                AppLogger.warn("  ⚠️ On macOS: Grant Accessibility permission in System Preferences > Security & Privacy > Privacy > Accessibility")
            }
        }
    }
    
    fun unregister() {
        try {
            GlobalScreen.removeNativeKeyListener(this)
            GlobalScreen.unregisterNativeHook()
            AppLogger.debug("GlobalHotkeyManager: Unregistered global hotkeys")
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
    
    override fun nativeKeyPressed(e: NativeKeyEvent) {
        // Check modifiers using the event's modifiers field
        val hasCtrl = (e.modifiers and NativeKeyEvent.CTRL_MASK) != 0
        val hasMeta = (e.modifiers and NativeKeyEvent.META_MASK) != 0
        val hasShift = (e.modifiers and NativeKeyEvent.SHIFT_MASK) != 0
        val hasAlt = (e.modifiers and NativeKeyEvent.ALT_MASK) != 0
        
        // Platform-specific modifier: Cmd on macOS, Ctrl on Windows
        val hasPrimaryModifier = if (isMacOS) hasMeta else hasCtrl
        
        // All shortcuts require Cmd+Shift+Opt (macOS) or Ctrl+Shift+Alt (Windows)
        if (hasPrimaryModifier && hasShift && hasAlt) {
            when (e.keyCode) {
                NativeKeyEvent.VC_S -> {
                    val hotkeyName = if (isMacOS) "Cmd+Shift+Opt+S" else "Ctrl+Shift+Alt+S"
                    AppLogger.debug("⌨️ Global Hotkey: $hotkeyName - Screenshot")
                    onScreenshotHotkey()
                }
                NativeKeyEvent.VC_B -> {
                    val hotkeyName = if (isMacOS) "Cmd+Shift+Opt+B" else "Ctrl+Shift+Alt+B"
                    AppLogger.debug("⌨️ Global Hotkey: $hotkeyName - Stealth mode")
                    onStealthHotkey()
                }
            }
        }
    }
    
    override fun nativeKeyReleased(e: NativeKeyEvent) {
        // Not used
    }
    
    override fun nativeKeyTyped(e: NativeKeyEvent) {
        // Not used
    }
}
