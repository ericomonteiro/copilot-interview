package com.github.ericomonteiro.pirateparrotai.screenshot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

actual suspend fun captureScreenshot(region: CaptureRegion?): Result<String> = withContext(Dispatchers.IO) {
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
            
            // Use region if provided and valid, otherwise capture full screen
            val screenRect = if (region != null && region.isValid()) {
                Rectangle(region.x, region.y, region.width, region.height)
            } else {
                val screenSize = Toolkit.getDefaultToolkit().screenSize
                Rectangle(screenSize)
            }
            
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
