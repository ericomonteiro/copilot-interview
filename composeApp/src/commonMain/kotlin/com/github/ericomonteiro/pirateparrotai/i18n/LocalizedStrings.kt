package com.github.ericomonteiro.pirateparrotai.i18n

import androidx.compose.runtime.*

private val LocalStrings = staticCompositionLocalOf<StringResources> { EnglishStrings }

object LocalizedStrings {
    val current: StringResources
        @Composable
        @ReadOnlyComposable
        get() = LocalStrings.current
    
    fun getStrings(language: AppLanguage): StringResources {
        return when (language) {
            AppLanguage.ENGLISH -> EnglishStrings
            AppLanguage.PORTUGUESE_BR -> PortugueseBRStrings
        }
    }
}

@Composable
fun ProvideStrings(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val strings = LocalizedStrings.getStrings(language)
    CompositionLocalProvider(LocalStrings provides strings) {
        content()
    }
}

@Composable
fun strings(): StringResources = LocalizedStrings.current
