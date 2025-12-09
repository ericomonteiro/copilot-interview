# AI Service API

Reference documentation for the AI Service interface and implementation.

## Interface Definition

```kotlin
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
```

## Methods

### generateSolution

Generate a code solution from a text description.

```kotlin
suspend fun generateSolution(
    problemDescription: String,
    language: String
): Result<SolutionResponse>
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `problemDescription` | `String` | Text description of the coding problem |
| `language` | `String` | Target programming language |

**Returns:** `Result<SolutionResponse>`

---

### analyzeCodingChallenge

Analyze a screenshot of a coding challenge and generate a solution.

```kotlin
suspend fun analyzeCodingChallenge(
    imageBase64: String,
    language: String
): Result<SolutionResponse>
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `imageBase64` | `String` | Base64-encoded PNG image |
| `language` | `String` | Target programming language |

**Returns:** `Result<SolutionResponse>`

**Example:**

```kotlin
val result = aiService.analyzeCodingChallenge(
    imageBase64 = screenshotBase64,
    language = "Kotlin"
)

result.onSuccess { solution ->
    println("Code: ${solution.code}")
    println("Time: ${solution.timeComplexity}")
}

result.onFailure { error ->
    println("Error: ${error.message}")
}
```

---

### analyzeCertificationQuestion

Analyze a screenshot of a certification exam question.

```kotlin
suspend fun analyzeCertificationQuestion(
    imageBase64: String,
    certificationType: CertificationType
): Result<CertificationResponse>
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `imageBase64` | `String` | Base64-encoded PNG image |
| `certificationType` | `CertificationType` | Type of AWS certification |

**Returns:** `Result<CertificationResponse>`

---

### analyzeGenericExam

Analyze a screenshot of a generic exam question.

```kotlin
suspend fun analyzeGenericExam(
    imageBase64: String,
    examType: GenericExamType,
    additionalContext: String?
): Result<GenericExamResponse>
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `imageBase64` | `String` | Base64-encoded PNG image |
| `examType` | `GenericExamType` | Type of exam |
| `additionalContext` | `String?` | Optional additional context |

**Returns:** `Result<GenericExamResponse>`

---

## Data Types

### SolutionResponse

```kotlin
@Serializable
data class SolutionResponse(
    val code: String,
    val explanation: String,
    val timeComplexity: String,
    val spaceComplexity: String
)
```

| Field | Type | Description |
|-------|------|-------------|
| `code` | `String` | Complete solution code |
| `explanation` | `String` | Explanation of the approach |
| `timeComplexity` | `String` | Big-O time complexity |
| `spaceComplexity` | `String` | Big-O space complexity |

---

### CertificationResponse

```kotlin
@Serializable
data class CertificationResponse(
    val answers: List<CertificationQuestionAnswer>,
    val examTips: String
)
```

| Field | Type | Description |
|-------|------|-------------|
| `answers` | `List<CertificationQuestionAnswer>` | List of question answers |
| `examTips` | `String` | General exam tips |

---

### CertificationQuestionAnswer

```kotlin
@Serializable
data class CertificationQuestionAnswer(
    val questionNumber: Int,
    val questionSummary: String,
    val correctAnswer: String,
    val explanation: String,
    val incorrectAnswersExplanation: String,
    val relatedServices: List<String>
)
```

| Field | Type | Description |
|-------|------|-------------|
| `questionNumber` | `Int` | Question number (1-indexed) |
| `questionSummary` | `String` | Brief summary of the question |
| `correctAnswer` | `String` | The correct answer with letter |
| `explanation` | `String` | Why this answer is correct |
| `incorrectAnswersExplanation` | `String` | Why other answers are wrong |
| `relatedServices` | `List<String>` | Related AWS services |

---

### GenericExamResponse

```kotlin
@Serializable
data class GenericExamResponse(
    val answers: List<GenericExamQuestionAnswer>,
    val studyTips: String,
    val detectedLanguage: String = "Unknown"
)
```

---

### GenericExamQuestionAnswer

```kotlin
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
```

---

## Enums

### CertificationType

```kotlin
enum class CertificationType(val displayName: String, val description: String) {
    AWS_CLOUD_PRACTITIONER(
        "AWS Cloud Practitioner", 
        "Foundational AWS certification"
    ),
    AWS_SOLUTIONS_ARCHITECT_ASSOCIATE(
        "AWS Solutions Architect Associate", 
        "Associate-level architecture certification"
    ),
    AWS_DEVELOPER_ASSOCIATE(
        "AWS Developer Associate", 
        "Associate-level developer certification"
    ),
    AWS_SYSOPS_ADMINISTRATOR(
        "AWS SysOps Administrator", 
        "Associate-level operations certification"
    ),
    AWS_SOLUTIONS_ARCHITECT_PROFESSIONAL(
        "AWS Solutions Architect Professional", 
        "Professional-level architecture certification"
    ),
    AWS_DEVOPS_ENGINEER_PROFESSIONAL(
        "AWS DevOps Engineer Professional", 
        "Professional-level DevOps certification"
    )
}
```

### GenericExamType

```kotlin
enum class GenericExamType(val displayName: String, val description: String) {
    ENEM("ENEM", "Exame Nacional do Ensino Medio"),
    VESTIBULAR("Vestibular", "Exames vestibulares de universidades"),
    CONCURSO("Concurso Publico", "Concursos publicos em geral"),
    OAB("OAB", "Exame da Ordem dos Advogados do Brasil"),
    ENADE("ENADE", "Exame Nacional de Desempenho dos Estudantes"),
    OUTROS("Outros", "Outros tipos de exames e provas")
}
```

---

## GeminiService Implementation

### Constructor

```kotlin
class GeminiService(
    private val apiKeyProvider: suspend () -> String,
    private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository,
    private val defaultModel: String = "gemini-2.5-flash"
) : AIService
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `apiKeyProvider` | `suspend () -> String` | Lambda to get API key |
| `httpClient` | `HttpClient` | Ktor HTTP client |
| `settingsRepository` | `SettingsRepository` | Settings repository |
| `defaultModel` | `String` | Default Gemini model |

### Additional Methods

#### listAvailableModels

```kotlin
suspend fun listAvailableModels(): Result<String>
```

Lists available Gemini models from the API.

---

## Usage Examples

### Basic Usage

```kotlin
// Get from Koin
val aiService: AIService = get()

// Analyze coding challenge
viewModelScope.launch {
    val result = aiService.analyzeCodingChallenge(
        imageBase64 = capturedImage.toBase64(),
        language = selectedLanguage
    )
    
    result.fold(
        onSuccess = { solution ->
            _uiState.update { it.copy(
                solution = solution,
                isLoading = false
            )}
        },
        onFailure = { error ->
            _uiState.update { it.copy(
                error = error.message,
                isLoading = false
            )}
        }
    )
}
```

### With Dependency Injection

```kotlin
// In AppModule.kt
val appModule = module {
    single { HttpClientFactory.create() }
    single { SettingsRepository(get()) }
    
    single<AIService> {
        GeminiService(
            apiKeyProvider = { 
                get<SettingsRepository>().getSetting(SettingsKeys.API_KEY) ?: "" 
            },
            httpClient = get(),
            settingsRepository = get()
        )
    }
}
```

---

## Error Handling

### Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `API key not configured` | Empty API key | Set key in Settings |
| `Gemini API error (401)` | Invalid API key | Check/regenerate key |
| `Gemini API error (429)` | Rate limited | Wait and retry |
| `No candidates in response` | Empty AI response | Retry with clearer image |

### Error Handling Pattern

```kotlin
result.fold(
    onSuccess = { response ->
        // Handle success
    },
    onFailure = { throwable ->
        when {
            throwable.message?.contains("401") == true -> 
                showError("Invalid API key")
            throwable.message?.contains("429") == true -> 
                showError("Rate limited, please wait")
            else -> 
                showError("Error: ${throwable.message}")
        }
    }
)
```
