package com.github.ericomonteiro.copilot.hotkey

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger

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
            println("GlobalHotkeyManager: ✅ Registered global hotkeys successfully")
            println("  • Cmd+Shift+Opt+S - Capture screenshot & analyze (auto-copy to clipboard)")
            println("  • Cmd+Shift+Opt+B - Toggle stealth mode")
            println("  ⚠️ On macOS: Grant Accessibility permission in System Preferences > Security & Privacy > Privacy > Accessibility")
        } catch (e: Exception) {
            println("GlobalHotkeyManager: ❌ Failed to register - ${e.message}")
            println("  ⚠️ On macOS: Grant Accessibility permission in System Preferences > Security & Privacy > Privacy > Accessibility")
            e.printStackTrace()
        }
    }
    
    fun unregister() {
        try {
            GlobalScreen.removeNativeKeyListener(this)
            GlobalScreen.unregisterNativeHook()
            println("GlobalHotkeyManager: Unregistered global hotkeys")
        } catch (e: Exception) {
            // Ignore
        }
    }
    
    override fun nativeKeyPressed(e: NativeKeyEvent) {
        // Check modifiers using the event's modifiers field
        val hasMeta = (e.modifiers and NativeKeyEvent.META_MASK) != 0
        val hasShift = (e.modifiers and NativeKeyEvent.SHIFT_MASK) != 0
        val hasAlt = (e.modifiers and NativeKeyEvent.ALT_MASK) != 0
        
        // All shortcuts require Cmd+Shift+Opt
        if (hasMeta && hasShift && hasAlt) {
            when (e.keyCode) {
                NativeKeyEvent.VC_S -> {
                    println("⌨️ Global Hotkey: Cmd+Shift+Opt+S - Screenshot")
                    onScreenshotHotkey()
                }
                NativeKeyEvent.VC_B -> {
                    println("⌨️ Global Hotkey: Cmd+Shift+Opt+B - Stealth mode")
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
