package com.github.ericomonteiro.pirateparrotai.i18n

import java.util.Locale

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English"),
    PORTUGUESE_BR("pt-BR", "Portuguese (Brazil)", "Português (Brasil)");
    
    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
        
        fun getSystemLanguage(): AppLanguage {
            val locale = Locale.getDefault()
            val language = locale.language
            val country = locale.country
            
            return when {
                language == "pt" && country == "BR" -> PORTUGUESE_BR
                language == "pt" -> PORTUGUESE_BR
                else -> ENGLISH
            }
        }
    }
}
