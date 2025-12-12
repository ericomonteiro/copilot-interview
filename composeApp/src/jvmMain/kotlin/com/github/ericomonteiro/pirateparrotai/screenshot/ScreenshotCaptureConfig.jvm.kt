package com.github.ericomonteiro.pirateparrotai.screenshot

import com.github.ericomonteiro.pirateparrotai.platform.WindowManager

actual object ScreenshotCaptureConfig {
    var windowManager: WindowManager? = null
    var wasStealthEnabled: Boolean = false
    actual var captureRegion: CaptureRegion? = null
}
