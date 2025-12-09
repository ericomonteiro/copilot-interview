package com.github.ericomonteiro.pirateparrotai.screenshot

import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ScreenshotService {
    suspend fun captureScreen(): Result<ByteArray>
    suspend fun captureScreenToFile(outputPath: String): Result<File>
}

class DesktopScreenshotService : ScreenshotService {
    
    override suspend fun captureScreen(): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val robot = Robot()
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val screenRect = Rectangle(screenSize)
            val screenshot = robot.createScreenCapture(screenRect)
            
            // Convert to PNG bytes
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(screenshot, "png", outputStream)
            outputStream.toByteArray()
        }
    }
    
    override suspend fun captureScreenToFile(outputPath: String): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val robot = Robot()
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val screenRect = Rectangle(screenSize)
            val screenshot = robot.createScreenCapture(screenRect)
            
            val file = File(outputPath)
            file.parentFile?.mkdirs()
            ImageIO.write(screenshot, "png", file)
            file
        }
    }
    
    suspend fun captureRegion(x: Int, y: Int, width: Int, height: Int): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val robot = Robot()
            val region = Rectangle(x, y, width, height)
            val screenshot = robot.createScreenCapture(region)
            
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(screenshot, "png", outputStream)
            outputStream.toByteArray()
        }
    }
}
