package com.github.ericomonteiro.pirateparrotai.screenshot

data class CaptureRegion(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {
    fun isValid(): Boolean = width > 0 && height > 0
    
    companion object {
        val FULL_SCREEN = CaptureRegion(0, 0, 0, 0)
    }
}
