# Data Flow

How data moves through Pirate-Parrot from user action to displayed result.

## Overview

Pirate-Parrot uses a **unidirectional data flow** pattern where:
1. User actions trigger events
2. Events are processed by ViewModels
3. ViewModels update state
4. UI observes and renders state

```mermaid
flowchart LR
    A[User Action] --> B[Event]
    B --> C[ViewModel]
    C --> D[State Update]
    D --> E[UI Render]
    E --> A
    
    style A fill:#00BFA6,color:#0D1B2A
    style E fill:#00BFA6,color:#0D1B2A
```

## Screenshot Analysis Flow

### Complete Flow Diagram

```mermaid
sequenceDiagram
    participant U as User
    participant UI as ComposeUI
    participant VM as ViewModel
    participant SC as ScreenshotCapture
    participant AI as AIService
    participant API as Gemini API
    participant DB as Database
    
    U->>UI: Click "Capture"
    UI->>VM: onCaptureClick()
    
    VM->>VM: setState(Loading)
    VM->>SC: captureScreen()
    SC->>SC: Robot.createScreenCapture()
    SC-->>VM: BufferedImage
    
    VM->>VM: encodeToBase64()
    VM->>AI: analyzeCodingChallenge(base64, language)
    
    AI->>API: POST /generateContent
    Note over API: Gemini processes image
    API-->>AI: JSON Response
    
    AI->>AI: parseResponse()
    AI-->>VM: Result<SolutionResponse>
    
    alt Success
        VM->>VM: setState(Success)
        VM->>DB: saveToHistory()
    else Error
        VM->>VM: setState(Error)
    end
    
    VM-->>UI: StateFlow emission
    UI->>UI: Recompose
    UI-->>U: Display result
```

## State Management

### ViewModel State

Each ViewModel manages its own state using `StateFlow`:

```kotlin
class ScreenshotAnalysisViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    data class UiState(
        val isLoading: Boolean = false,
        val screenshot: ImageBitmap? = null,
        val solution: SolutionResponse? = null,
        val error: String? = null
    )
}
```

### State Transitions

```mermaid
stateDiagram-v2
    [*] --> Idle
    Idle --> Loading: Capture clicked
    Loading --> Success: API success
    Loading --> Error: API failure
    Success --> Idle: Reset
    Error --> Idle: Retry
    Error --> Loading: Retry clicked
    
    state Success {
        [*] --> DisplaySolution
        DisplaySolution --> SaveToHistory
    }
```

## Data Persistence Flow

### Settings Flow

```mermaid
sequenceDiagram
    participant UI as SettingsScreen
    participant VM as SettingsViewModel
    participant Repo as SettingsRepository
    participant DB as SQLDelight
    
    UI->>VM: onApiKeyChange(key)
    VM->>Repo: setSetting(API_KEY, key)
    Repo->>DB: INSERT OR REPLACE
    DB-->>Repo: Success
    Repo-->>VM: Unit
    VM->>VM: Update state
    VM-->>UI: StateFlow emission
```

### History Flow

```mermaid
sequenceDiagram
    participant VM as AnalysisViewModel
    participant Repo as HistoryRepository
    participant DB as SQLDelight
    
    VM->>Repo: saveScreenshot(image, result)
    Repo->>DB: INSERT screenshot_history
    DB-->>Repo: id
    
    Note over VM: Later...
    
    VM->>Repo: getHistory()
    Repo->>DB: SELECT * FROM screenshot_history
    DB-->>Repo: List<ScreenshotHistory>
    Repo-->>VM: Flow<List<ScreenshotHistory>>
```

## API Communication Flow

### Request Flow

```mermaid
flowchart TD
    A[ViewModel] --> B[AIService]
    B --> C[Build Request Body]
    C --> D[Add Image Data]
    D --> E[Ktor HttpClient]
    E --> F[POST to Gemini API]
    
    style A fill:#00BFA6,color:#0D1B2A
    style F fill:#FFB74D,color:#0D1B2A
```

### Response Flow

```mermaid
flowchart TD
    A[Gemini API Response] --> B[Ktor HttpClient]
    B --> C[JSON Deserialization]
    C --> D[Extract Content]
    D --> E[Parse JSON Response]
    E --> F[Map to Domain Model]
    F --> G[Return Result]
    
    style A fill:#FFB74D,color:#0D1B2A
    style G fill:#00BFA6,color:#0D1B2A
```

## Event Handling

### Hotkey Events

```mermaid
sequenceDiagram
    participant OS as Operating System
    participant JNH as JNativeHook
    participant HM as HotkeyManager
    participant Main as Main.kt
    participant VM as ViewModel
    
    OS->>JNH: Key Event
    JNH->>HM: nativeKeyPressed()
    HM->>HM: Check modifiers
    
    alt Screenshot Hotkey
        HM->>Main: onScreenshotHotkey()
        Main->>Main: screenshotTrigger++
        Main-->>VM: LaunchedEffect triggers
        VM->>VM: captureAndAnalyze()
    else Stealth Hotkey
        HM->>Main: onStealthHotkey()
        Main->>Main: Toggle stealth
    end
```

## Error Handling Flow

```mermaid
flowchart TD
    A[API Call] --> B{Success?}
    B -->|Yes| C[Parse Response]
    C --> D{Valid JSON?}
    D -->|Yes| E[Return Success]
    D -->|No| F[Parse Error]
    B -->|No| G{Error Type}
    G -->|Network| H[Network Error]
    G -->|Auth| I[Auth Error]
    G -->|Rate Limit| J[Rate Limit Error]
    
    F --> K[Return Failure]
    H --> K
    I --> K
    J --> K
    
    style E fill:#00BFA6,color:#0D1B2A
    style K fill:#FF5252,color:#FFFFFF
```

## Coroutine Scopes

### Scope Hierarchy

```mermaid
graph TD
    A[Application Scope] --> B[ViewModel Scope]
    B --> C[UI Coroutines]
    B --> D[IO Coroutines]
    
    A --> E[Global Hotkey Scope]
    
    style A fill:#00BFA6,color:#0D1B2A
    style B fill:#FFB74D,color:#0D1B2A
```

### Dispatcher Usage

| Operation | Dispatcher | Reason |
|-----------|------------|--------|
| UI Updates | `Main` | Compose requires main thread |
| API Calls | `IO` | Network operations |
| Image Processing | `Default` | CPU-intensive |
| Database | `IO` | Disk operations |

## Reactive Streams

### Flow Usage

```kotlin
// Settings as Flow
val apiKey: Flow<String?> = settingsRepository
    .getSettingFlow(SettingsKeys.API_KEY)

// History as Flow
val history: Flow<List<ScreenshotHistory>> = historyRepository
    .getAllHistory()

// UI observes
val apiKey by viewModel.apiKey.collectAsState()
```

### StateFlow vs SharedFlow

| Type | Use Case |
|------|----------|
| `StateFlow` | UI state (always has value) |
| `SharedFlow` | One-time events (navigation, snackbar) |
