package com.github.ericomonteiro.pirateparrotai.hotkey

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface GlobalHotkeyService {
    val screenshotTrigger: SharedFlow<Unit>
    fun start()
    fun stop()
}

class DesktopGlobalHotkeyService : GlobalHotkeyService {
    
    private val _screenshotTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val screenshotTrigger: SharedFlow<Unit> = _screenshotTrigger.asSharedFlow()
    
    private var isRunning = false
    
    override fun start() {
        if (isRunning) return
        isRunning = true
        
        // Note: True global hotkeys on macOS require native code or accessibility permissions
        // For now, we'll use a simple approach that works when the app has focus
        // For production, consider using JNativeHook or similar library
        
        println("Global hotkey service started. Use Cmd+Shift+S to capture screenshot.")
        println("Note: This requires the app window to have focus.")
    }
    
    override fun stop() {
        isRunning = false
    }
    
    fun triggerScreenshot() {
        _screenshotTrigger.tryEmit(Unit)
    }
}
