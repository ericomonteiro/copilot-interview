package com.github.ericomonteiro.pirateparrotai.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Utility object for clipboard operations.
 */
object ClipboardUtils {
    /**
     * Copies the given text to the system clipboard.
     * 
     * @param text The text to copy
     * @return true if successful, false otherwise
     */
    fun copyToClipboard(text: String): Boolean {
        return try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val stringSelection = StringSelection(text)
            clipboard.setContents(stringSelection, null)
            true
        } catch (e: Exception) {
            AppLogger.error("Failed to copy to clipboard", e)
            false
        }
    }
}
