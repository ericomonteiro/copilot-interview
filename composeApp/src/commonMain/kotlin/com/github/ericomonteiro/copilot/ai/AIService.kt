package com.github.ericomonteiro.copilot.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.encodeToString

interface AIService {
    suspend fun generateSolution(
        problemDescription: String,
        language: String
    ): Result<SolutionResponse>
    
    suspend fun analyzeCodingChallenge(
        imageBase64: String,
        language: String
    ): Result<SolutionResponse>
}

@Serializable
data class SolutionResponse(
    val code: String,
    val explanation: String,
    val timeComplexity: String,
    val spaceComplexity: String
)

enum class AIProvider {
    OPENAI,
    GEMINI
}

class OpenAIService(
    private val apiKey: String,
    private val httpClient: HttpClient
) : AIService {
    
    private val baseUrl = "https://api.openai.com/v1"
    private val model = "gpt-3.5-turbo" // Free tier compatible model
    
    override suspend fun generateSolution(
        problemDescription: String,
        language: String
    ): Result<SolutionResponse> = runCatching {
        if (apiKey.isBlank()) {
            throw IllegalStateException("OpenAI API key is not configured. Please set it in Settings.")
        }
        
        val prompt = buildPrompt(problemDescription, language)
        
        val httpResponse = httpClient.post("$baseUrl/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(OpenAIRequest(
                model = model,
                messages = listOf(
                    Message("system", SYSTEM_PROMPT),
                    Message("user", prompt)
                ),
                temperature = 0.3,
                maxTokens = 1500
            ))
        }
        
        // Check for HTTP errors
        if (httpResponse.status.value !in 200..299) {
            val errorBody = httpResponse.body<String>()
            throw Exception("OpenAI API error (${httpResponse.status.value}): $errorBody")
        }
        
        val response = httpResponse.body<OpenAIResponse>()
        
        if (response.choices.isEmpty()) {
            throw Exception("OpenAI returned no choices in response")
        }
        
        parseResponse(response.choices.first().message.content)
    }
    
    override suspend fun analyzeCodingChallenge(
        imageBase64: String,
        language: String
    ): Result<SolutionResponse> = runCatching {
        throw UnsupportedOperationException("OpenAI vision API requires GPT-4 Vision which is not in free tier. Please use Gemini for image analysis.")
    }
    
    private fun buildPrompt(problem: String, language: String): String = """
        Solve this coding problem in $language:
        
        $problem
        
        Provide your response in JSON format:
        {
          "code": "complete solution code",
          "explanation": "brief explanation of the approach",
          "timeComplexity": "O(...)",
          "spaceComplexity": "O(...)"
        }
    """.trimIndent()
    
    private fun parseResponse(content: String): SolutionResponse {
        // Extract JSON from markdown code blocks if present
        val jsonContent = if (content.contains("```json")) {
            content.substringAfter("```json").substringBefore("```").trim()
        } else if (content.contains("```")) {
            content.substringAfter("```").substringBefore("```").trim()
        } else {
            content.trim()
        }
        
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(jsonContent)
    }
    
    companion object {
        private const val SYSTEM_PROMPT = """
            You are an expert software engineer. Generate clean, optimal code solutions.
            Always respond with valid JSON in the exact format requested.
        """
    }
}

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double,
    @SerialName("max_tokens") val maxTokens: Int
)

@Serializable
data class Message(val role: String, val content: String)

@Serializable
data class OpenAIResponse(val choices: List<Choice>)

@Serializable
data class Choice(val message: Message)

// Google Gemini Service
class GeminiService(
    private val apiKey: String,
    private val httpClient: HttpClient,
    private val model: String = "gemini-2.5-flash" // Default model
) : AIService {
    
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    
    override suspend fun generateSolution(
        problemDescription: String,
        language: String
    ): Result<SolutionResponse> = runCatching {
        if (apiKey.isBlank()) {
            throw IllegalStateException("Gemini API key is not configured. Please set it in Settings.")
        }
        
        val prompt = buildPrompt(problemDescription, language)
        
        val httpResponse = httpClient.post("$baseUrl/models/$model:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                )
            ))
        }
        
        if (httpResponse.status.value !in 200..299) {
            val errorBody = httpResponse.body<String>()
            throw Exception("Gemini API error (${httpResponse.status.value}): $errorBody")
        }
        
        val response = httpResponse.body<GeminiResponse>()
        
        if (response.candidates.isEmpty()) {
            throw Exception("Gemini returned no candidates in response")
        }
        
        val content = response.candidates.first().content.parts.first().text
        parseResponse(content)
    }
    
    private fun buildPrompt(problem: String, language: String): String = """
        Solve this coding problem in $language:
        
        $problem
        
        Provide your response in JSON format:
        {
          "code": "complete solution code",
          "explanation": "brief explanation of the approach",
          "timeComplexity": "O(...)",
          "spaceComplexity": "O(...)"
        }
        
        Important: Return ONLY the JSON, no markdown code blocks or additional text.
    """.trimIndent()
    
    private fun parseResponse(content: String): SolutionResponse {
        val jsonContent = if (content.contains("```json")) {
            content.substringAfter("```json").substringBefore("```").trim()
        } else if (content.contains("```")) {
            content.substringAfter("```").substringBefore("```").trim()
        } else {
            content.trim()
        }
        
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(jsonContent)
    }
    
    override suspend fun analyzeCodingChallenge(
        imageBase64: String,
        language: String
    ): Result<SolutionResponse> = runCatching {
        if (apiKey.isBlank()) {
            throw IllegalStateException("Gemini API key is not configured. Please set it in Settings.")
        }
        
        val prompt = buildImageAnalysisPrompt(language)
        
        val requestBody = buildJsonObject {
            put("contents", buildJsonArray {
                add(buildJsonObject {
                    put("parts", buildJsonArray {
                        add(buildJsonObject {
                            put("text", prompt)
                        })
                        add(buildJsonObject {
                            putJsonObject("inlineData") {
                                put("mimeType", "image/png")
                                put("data", imageBase64)
                            }
                        })
                    })
                })
            })
        }
        
        val httpResponse = httpClient.post("$baseUrl/models/$model:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(requestBody))
        }
        
        if (httpResponse.status.value !in 200..299) {
            val errorBody = httpResponse.body<String>()
            throw Exception("Gemini API error (${httpResponse.status.value}): $errorBody")
        }
        
        val response = httpResponse.body<GeminiResponse>()
        
        if (response.candidates.isEmpty()) {
            throw Exception("Gemini returned no candidates in response")
        }
        
        val content = response.candidates.first().content.parts.first().text
        parseResponse(content)
    }
    
    private fun buildImageAnalysisPrompt(language: String): String = """
        Analyze this coding challenge screenshot and provide a complete solution in $language.
        
        Extract the problem description from the image and solve it.
        
        Provide your response in JSON format:
        {
          "code": "complete solution code",
          "explanation": "brief explanation of the problem and approach",
          "timeComplexity": "O(...)",
          "spaceComplexity": "O(...)"
        }
        
        Important: Return ONLY the JSON, no markdown code blocks or additional text.
    """.trimIndent()
    
    // Helper function to list available models
    suspend fun listAvailableModels(): Result<String> = runCatching {
        val httpResponse = httpClient.get("$baseUrl/models?key=$apiKey")
        
        if (httpResponse.status.value !in 200..299) {
            val errorBody = httpResponse.body<String>()
            throw Exception("Failed to list models (${httpResponse.status.value}): $errorBody")
        }
        
        httpResponse.body<String>()
    }
}

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent
)

object HttpClientFactory {
    fun create(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
            install(io.ktor.client.plugins.HttpTimeout) {
                requestTimeoutMillis = 60000
            }
        }
    }
}
