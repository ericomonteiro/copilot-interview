package com.github.ericomonteiro.pirateparrotai.platform

/**
 * Platform-specific window management interface.
 * Provides access to native window features like screen capture hiding.
 */
expect class WindowManager {
    /**
     * Hides the window from screen capture/recording.
     * On macOS: Uses NSWindow sharingType
     * On Windows: Uses SetWindowDisplayAffinity
     */
    fun setHideFromCapture(hide: Boolean)
}
