package com.github.ericomonteiro.pirateparrotai.ui.settings

import com.github.ericomonteiro.pirateparrotai.screenshot.CaptureRegion
import com.github.ericomonteiro.pirateparrotai.util.AppLogger
import java.awt.*
import java.awt.event.*
import javax.swing.*

class RegionSelectorWindow(
    private val onRegionSelected: (CaptureRegion) -> Unit,
    private val onCancelled: () -> Unit
) {
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var frame: JFrame? = null
    private var overlay: SelectionOverlay? = null
    
    fun show() {
        SwingUtilities.invokeLater {
            createAndShowWindow()
        }
    }
    
    private fun createAndShowWindow() {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        
        frame = JFrame().apply {
            isUndecorated = true
            background = Color(0, 0, 0, 1) // Almost transparent
            bounds = Rectangle(0, 0, screenSize.width, screenSize.height)
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            isAlwaysOnTop = true
            
            // Make it a utility window to avoid taskbar
            type = Window.Type.UTILITY
        }
        
        overlay = SelectionOverlay()
        frame?.contentPane = overlay
        
        // Add mouse listeners
        overlay?.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                startPoint = e.point
                endPoint = e.point
                overlay?.setSelection(startPoint, endPoint)
            }
            
            override fun mouseReleased(e: MouseEvent) {
                endPoint = e.point
                finalizeSelection()
            }
        })
        
        overlay?.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                endPoint = e.point
                overlay?.setSelection(startPoint, endPoint)
            }
        })
        
        // Add keyboard listener for ESC to cancel
        frame?.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    cancel()
                }
            }
        })
        
        frame?.isVisible = true
        frame?.requestFocus()
    }
    
    private fun finalizeSelection() {
        val start = startPoint
        val end = endPoint
        
        if (start != null && end != null) {
            val x = minOf(start.x, end.x)
            val y = minOf(start.y, end.y)
            val width = kotlin.math.abs(end.x - start.x)
            val height = kotlin.math.abs(end.y - start.y)
            
            if (width > 10 && height > 10) {
                val region = CaptureRegion(x, y, width, height)
                AppLogger.info("Region selected: $region")
                close()
                onRegionSelected(region)
            } else {
                // Selection too small, cancel
                cancel()
            }
        } else {
            cancel()
        }
    }
    
    private fun cancel() {
        close()
        onCancelled()
    }
    
    private fun close() {
        frame?.dispose()
        frame = null
        overlay = null
    }
    
    private inner class SelectionOverlay : JPanel() {
        private var selectionStart: Point? = null
        private var selectionEnd: Point? = null
        
        init {
            isOpaque = false
            cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
        }
        
        fun setSelection(start: Point?, end: Point?) {
            selectionStart = start
            selectionEnd = end
            repaint()
        }
        
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2d = g as Graphics2D
            
            // Draw semi-transparent overlay
            g2d.color = Color(0, 0, 0, 100)
            g2d.fillRect(0, 0, width, height)
            
            // Draw selection rectangle
            val start = selectionStart
            val end = selectionEnd
            
            if (start != null && end != null) {
                val x = minOf(start.x, end.x)
                val y = minOf(start.y, end.y)
                val w = kotlin.math.abs(end.x - start.x)
                val h = kotlin.math.abs(end.y - start.y)
                
                // Clear the selection area (make it transparent)
                g2d.composite = AlphaComposite.Clear
                g2d.fillRect(x, y, w, h)
                
                // Reset composite
                g2d.composite = AlphaComposite.SrcOver
                
                // Draw border around selection
                g2d.color = Color(0, 191, 166) // AppColors.Primary
                g2d.stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, floatArrayOf(5f, 5f), 0f)
                g2d.drawRect(x, y, w, h)
                
                // Draw solid corners
                g2d.stroke = BasicStroke(3f)
                val cornerSize = 15
                // Top-left
                g2d.drawLine(x, y, x + cornerSize, y)
                g2d.drawLine(x, y, x, y + cornerSize)
                // Top-right
                g2d.drawLine(x + w - cornerSize, y, x + w, y)
                g2d.drawLine(x + w, y, x + w, y + cornerSize)
                // Bottom-left
                g2d.drawLine(x, y + h - cornerSize, x, y + h)
                g2d.drawLine(x, y + h, x + cornerSize, y + h)
                // Bottom-right
                g2d.drawLine(x + w - cornerSize, y + h, x + w, y + h)
                g2d.drawLine(x + w, y + h - cornerSize, x + w, y + h)
                
                // Draw size info
                val sizeText = "${w} x ${h}"
                g2d.font = Font("SansSerif", Font.BOLD, 14)
                val fm = g2d.fontMetrics
                val textWidth = fm.stringWidth(sizeText)
                val textHeight = fm.height
                
                val textX = x + (w - textWidth) / 2
                val textY = if (y > 30) y - 10 else y + h + textHeight + 5
                
                // Background for text
                g2d.color = Color(0, 0, 0, 180)
                g2d.fillRoundRect(textX - 5, textY - textHeight + 3, textWidth + 10, textHeight + 4, 5, 5)
                
                // Text
                g2d.color = Color.WHITE
                g2d.drawString(sizeText, textX, textY)
            }
            
            // Draw instructions
            g2d.font = Font("SansSerif", Font.BOLD, 16)
            val instructions = "Arraste para selecionar a área de captura • ESC para cancelar"
            val fm = g2d.fontMetrics
            val textWidth = fm.stringWidth(instructions)
            
            val textX = (width - textWidth) / 2
            val textY = 40
            
            // Background for instructions
            g2d.color = Color(0, 0, 0, 200)
            g2d.fillRoundRect(textX - 15, textY - fm.height + 3, textWidth + 30, fm.height + 10, 10, 10)
            
            // Instructions text
            g2d.color = Color.WHITE
            g2d.drawString(instructions, textX, textY)
        }
    }
}
