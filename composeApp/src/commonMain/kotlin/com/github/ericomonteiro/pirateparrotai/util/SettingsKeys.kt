package com.github.ericomonteiro.pirateparrotai.util

/**
 * Constants for settings keys used throughout the application.
 * Centralizes all magic strings for settings storage.
 */
object SettingsKeys {
    const val API_KEY = "api_key"
    const val HIDE_FROM_CAPTURE = "hide_from_capture"
    const val SELECTED_MODEL = "selected_model"
    const val DEFAULT_LANGUAGE = "default_language"
    const val DEFAULT_CERTIFICATION = "default_certification"
    const val DEFAULT_EXAM_TYPE = "default_exam_type"
    
    // Window state
    const val WINDOW_WIDTH = "window_width"
    const val WINDOW_HEIGHT = "window_height"
    const val WINDOW_X = "window_x"
    const val WINDOW_Y = "window_y"
    
    // App language (i18n)
    const val APP_LANGUAGE = "app_language"
    
    // Capture region
    const val CAPTURE_REGION_ENABLED = "capture_region_enabled"
    const val CAPTURE_REGION_X = "capture_region_x"
    const val CAPTURE_REGION_Y = "capture_region_y"
    const val CAPTURE_REGION_WIDTH = "capture_region_width"
    const val CAPTURE_REGION_HEIGHT = "capture_region_height"
}
