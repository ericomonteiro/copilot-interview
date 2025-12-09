package com.github.ericomonteiro.pirateparrotai.util

import kotlinx.serialization.json.Json

/**
 * Shared JSON configuration and utilities for the application.
 */
object JsonUtils {
    /**
     * Shared JSON instance configured for API responses.
     * - ignoreUnknownKeys: Allows parsing responses with extra fields
     * - isLenient: Handles minor JSON format issues
     * - prettyPrint: For readable output when encoding
     */
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }
    
    /**
     * Extracts JSON content from a string that may be wrapped in markdown code blocks.
     * 
     * Handles formats like:
     * - ```json { ... } ```
     * - ``` { ... } ```
     * - { ... } (plain JSON)
     * 
     * @param content The raw content that may contain markdown
     * @return The extracted JSON string
     */
    fun extractJsonFromMarkdown(content: String): String {
        return when {
            content.contains("```json") -> {
                content.substringAfter("```json").substringBefore("```").trim()
            }
            content.contains("```") -> {
                content.substringAfter("```").substringBefore("```").trim()
            }
            else -> content.trim()
        }
    }
    
    /**
     * Parses JSON content that may be wrapped in markdown code blocks.
     * 
     * @param content The raw content from AI response
     * @return The parsed object of type T
     */
    inline fun <reified T> parseJsonResponse(content: String): T {
        val jsonContent = extractJsonFromMarkdown(content)
        return json.decodeFromString(jsonContent)
    }
}
