package com.github.ericomonteiro.copilot.ai

import com.github.ericomonteiro.copilot.data.repository.SettingsRepository
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
    
    suspend fun analyzeCertificationQuestion(
        imageBase64: String,
        certificationType: CertificationType
    ): Result<CertificationResponse>
    
    suspend fun analyzeGenericExam(
        imageBase64: String,
        examType: GenericExamType,
        additionalContext: String?
    ): Result<GenericExamResponse>
}

enum class CertificationType(val displayName: String, val description: String) {
    AWS_CLOUD_PRACTITIONER("AWS Cloud Practitioner", "Foundational AWS certification"),
    AWS_SOLUTIONS_ARCHITECT_ASSOCIATE("AWS Solutions Architect Associate", "Associate-level architecture certification"),
    AWS_DEVELOPER_ASSOCIATE("AWS Developer Associate", "Associate-level developer certification"),
    AWS_SYSOPS_ADMINISTRATOR("AWS SysOps Administrator", "Associate-level operations certification"),
    AWS_SOLUTIONS_ARCHITECT_PROFESSIONAL("AWS Solutions Architect Professional", "Professional-level architecture certification"),
    AWS_DEVOPS_ENGINEER_PROFESSIONAL("AWS DevOps Engineer Professional", "Professional-level DevOps certification")
}

@Serializable
data class CertificationQuestionAnswer(
    val questionNumber: Int,
    val questionSummary: String,
    val correctAnswer: String,
    val explanation: String,
    val incorrectAnswersExplanation: String,
    val relatedServices: List<String>
)

@Serializable
data class CertificationResponse(
    val answers: List<CertificationQuestionAnswer>,
    val examTips: String
)

enum class GenericExamType(val displayName: String, val description: String) {
    ENEM("ENEM", "Exame Nacional do Ensino Medio"),
    VESTIBULAR("Vestibular", "Exames vestibulares de universidades"),
    CONCURSO("Concurso Publico", "Concursos publicos em geral"),
    OAB("OAB", "Exame da Ordem dos Advogados do Brasil"),
    ENADE("ENADE", "Exame Nacional de Desempenho dos Estudantes"),
    OUTROS("Outros", "Outros tipos de exames e provas")
}

@Serializable
data class GenericExamQuestionAnswer(
    val questionNumber: Int,
    val questionSummary: String,
    val correctAnswer: String,
    val explanation: String,
    val incorrectAnswersExplanation: String,
    val subject: String,
    val topic: String
)

@Serializable
data class GenericExamResponse(
    val answers: List<GenericExamQuestionAnswer>,
    val studyTips: String,
    val detectedLanguage: String = "Unknown"
)

@Serializable
data class SolutionResponse(
    val code: String,
    val explanation: String,
    val timeComplexity: String,
    val spaceComplexity: String
)

// Google Gemini Service
class GeminiService(
    private val apiKey: String,
    private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository,
    private val defaultModel: String = "gemini-2.5-flash"
) : AIService {
    
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    
    private suspend fun getModel(): String {
        return settingsRepository.getSetting("selected_model") ?: defaultModel
    }
    
    override suspend fun generateSolution(
        problemDescription: String,
        language: String
    ): Result<SolutionResponse> = runCatching {
        if (apiKey.isBlank()) {
            throw IllegalStateException("Gemini API key is not configured. Please set it in Settings.")
        }
        
        val prompt = buildPrompt(problemDescription, language)
        val model = getModel()
        
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
        val model = getModel()
        
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
    
    override suspend fun analyzeCertificationQuestion(
        imageBase64: String,
        certificationType: CertificationType
    ): Result<CertificationResponse> = runCatching {
        if (apiKey.isBlank()) {
            throw IllegalStateException("Gemini API key is not configured. Please set it in Settings.")
        }
        
        val prompt = buildCertificationPrompt(certificationType)
        val model = getModel()
        
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
        parseCertificationResponse(content)
    }
    
    private fun buildCertificationPrompt(certificationType: CertificationType): String = """
        You are an expert AWS certification instructor helping a student prepare for the ${certificationType.displayName} exam.
        
        Analyze this certification exam screenshot and provide detailed answers for ALL questions visible in the image.
        
        CRITICAL INSTRUCTIONS:
        1. DETECT the language of the questions (English, Portuguese, Spanish, etc.) and RESPOND IN THE SAME LANGUAGE
        2. Answer ALL questions visible in the screenshot - there may be one or multiple questions
        3. For EACH question:
           - Identify the correct answer(s)
           - Explain why the correct answer is right
           - Explain why each incorrect answer is wrong
           - List related AWS services
        4. Provide general exam tips at the end
        
        Provide your response in JSON format:
        {
          "answers": [
            {
              "questionNumber": 1,
              "questionSummary": "Brief summary of what the question asks",
              "correctAnswer": "The letter(s) and full text of the correct answer(s), e.g., 'B. Amazon S3'",
              "explanation": "Detailed explanation of why this is the correct answer",
              "incorrectAnswersExplanation": "Explanation of why each incorrect option is wrong",
              "relatedServices": ["List", "of", "AWS", "services", "mentioned"]
            }
          ],
          "examTips": "General tips for answering similar questions on the exam"
        }
        
        If there are multiple questions, add more objects to the "answers" array with incrementing questionNumber.
        
        Important: 
        - Return ONLY the JSON, no markdown code blocks or additional text.
        - RESPOND IN THE SAME LANGUAGE AS THE QUESTIONS IN THE IMAGE.
    """.trimIndent()
    
    private fun parseCertificationResponse(content: String): CertificationResponse {
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
    
    override suspend fun analyzeGenericExam(
        imageBase64: String,
        examType: GenericExamType,
        additionalContext: String?
    ): Result<GenericExamResponse> = runCatching {
        if (apiKey.isBlank()) {
            throw IllegalStateException("Gemini API key is not configured. Please set it in Settings.")
        }
        
        val prompt = buildGenericExamPrompt(examType, additionalContext)
        val model = getModel()
        
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
        parseGenericExamResponse(content)
    }
    
    private fun buildGenericExamPrompt(examType: GenericExamType, additionalContext: String?): String {
        val contextInfo = if (!additionalContext.isNullOrBlank()) {
            "\n\nADDITIONAL CONTEXT PROVIDED BY USER:\n$additionalContext\n"
        } else ""
        
        return """
            You are an expert exam tutor helping a student prepare for ${examType.displayName} (${examType.description}).
            $contextInfo
            Analyze this exam screenshot and provide detailed answers for ALL questions visible in the image.
            
            CRITICAL INSTRUCTIONS:
            1. DETECT the language of the questions (Portuguese, English, Spanish, etc.) and RESPOND IN THE SAME LANGUAGE
            2. Answer ALL questions visible in the screenshot - there may be one or multiple questions
            3. For EACH question:
               - Identify the correct answer(s)
               - Explain why the correct answer is right
               - Explain why each incorrect answer is wrong
               - Identify the subject area (Math, Physics, History, etc.)
               - Identify the specific topic within that subject
            4. Provide study tips at the end
            
            Provide your response in JSON format:
            {
              "detectedLanguage": "The language detected in the exam (e.g., Portuguese, English, Spanish)",
              "answers": [
                {
                  "questionNumber": 1,
                  "questionSummary": "Brief summary of what the question asks",
                  "correctAnswer": "The letter(s) and full text of the correct answer(s)",
                  "explanation": "Detailed explanation of why this is the correct answer, with step-by-step reasoning if applicable",
                  "incorrectAnswersExplanation": "Explanation of why each incorrect option is wrong",
                  "subject": "Subject area (e.g., Mathematics, Physics, History, Portuguese, Biology)",
                  "topic": "Specific topic (e.g., Trigonometry, Thermodynamics, World War II)"
                }
              ],
              "studyTips": "Tips for studying this subject and similar questions"
            }
            
            If there are multiple questions, add more objects to the "answers" array with incrementing questionNumber.
            
            Important: 
            - Return ONLY the JSON, no markdown code blocks or additional text.
            - RESPOND IN THE SAME LANGUAGE AS THE QUESTIONS IN THE IMAGE.
            - Be thorough in explanations, especially for complex subjects like Math and Physics.
        """.trimIndent()
    }
    
    private fun parseGenericExamResponse(content: String): GenericExamResponse {
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
