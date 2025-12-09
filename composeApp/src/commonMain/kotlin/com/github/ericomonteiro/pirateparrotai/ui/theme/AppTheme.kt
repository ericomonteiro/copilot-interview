package com.github.ericomonteiro.pirateparrotai.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Modern, friendly color palette inspired by tropical/pirate theme
object AppColors {
    // Primary - Vibrant Teal/Cyan (Ocean)
    val Primary = Color(0xFF00BFA6)
    val PrimaryLight = Color(0xFF5DF2D6)
    val PrimaryDark = Color(0xFF008E76)
    val OnPrimary = Color(0xFF003731)
    
    // Secondary - Warm Orange/Gold (Treasure)
    val Secondary = Color(0xFFFFB74D)
    val SecondaryLight = Color(0xFFFFE97D)
    val SecondaryDark = Color(0xFFC88719)
    val OnSecondary = Color(0xFF442B00)
    
    // Tertiary - Deep Purple (Mystery)
    val Tertiary = Color(0xFFB388FF)
    val TertiaryLight = Color(0xFFE7B9FF)
    val TertiaryDark = Color(0xFF805ACB)
    val OnTertiary = Color(0xFF21005D)
    
    // Background - Rich Dark Blue (Night Sea)
    val Background = Color(0xFF0D1B2A)
    val Surface = Color(0xFF1B2838)
    val SurfaceVariant = Color(0xFF2D3E50)
    val SurfaceContainer = Color(0xFF243447)
    
    // On colors
    val OnBackground = Color(0xFFE8F4F8)
    val OnSurface = Color(0xFFE8F4F8)
    val OnSurfaceVariant = Color(0xFFB8C7D4)
    
    // Error - Coral Red
    val Error = Color(0xFFFF6B6B)
    val ErrorContainer = Color(0xFF93000A)
    val OnError = Color(0xFFFFFFFF)
    val OnErrorContainer = Color(0xFFFFDAD6)
    
    // Success - Emerald Green
    val Success = Color(0xFF4ADE80)
    
    // Outline
    val Outline = Color(0xFF4A6572)
    val OutlineVariant = Color(0xFF3A4F5C)
}

val PirateParrotDarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryDark,
    onPrimaryContainer = AppColors.PrimaryLight,
    
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryDark,
    onSecondaryContainer = AppColors.SecondaryLight,
    
    tertiary = AppColors.Tertiary,
    onTertiary = AppColors.OnTertiary,
    tertiaryContainer = AppColors.TertiaryDark,
    onTertiaryContainer = AppColors.TertiaryLight,
    
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    surfaceContainer = AppColors.SurfaceContainer,
    
    error = AppColors.Error,
    onError = AppColors.OnError,
    errorContainer = AppColors.ErrorContainer,
    onErrorContainer = AppColors.OnErrorContainer,
    
    outline = AppColors.Outline,
    outlineVariant = AppColors.OutlineVariant
)
