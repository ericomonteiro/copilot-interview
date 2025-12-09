package com.github.ericomonteiro.pirateparrotai.screenshot

import com.github.ericomonteiro.pirateparrotai.platform.WindowManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Singleton to hold reference to WindowManager for screenshot capture
 */
object ScreenshotCaptureConfig {
    var windowManager: WindowManager? = null
    var wasStealthEnabled: Boolean = false
}

actual suspend fun captureScreenshot(): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
        val windowManager = ScreenshotCaptureConfig.windowManager
        
        // Save current stealth state
        val wasStealthEnabled = ScreenshotCaptureConfig.wasStealthEnabled
        
        // Enable stealth mode before capture (makes app invisible to screen capture)
        if (!wasStealthEnabled) {
            windowManager?.setHideFromCapture(true)
            // Wait for stealth mode to apply
            delay(200)
        }
        
        try {
            val robot = Robot()
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val screenRect = Rectangle(screenSize)
            val screenshot = robot.createScreenCapture(screenRect)
            
            // Convert to PNG bytes
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(screenshot, "png", outputStream)
            val bytes = outputStream.toByteArray()
            
            // Convert to base64
            bytes.toBase64()
        } finally {
            // Restore previous stealth mode state if it was disabled
            if (!wasStealthEnabled) {
                windowManager?.setHideFromCapture(false)
            }
        }
    }
}
