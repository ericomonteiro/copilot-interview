# Architecture Overview

Pirate-Parrot follows a clean architecture pattern with clear separation of concerns.

## High-Level Architecture

```mermaid
graph TB
    subgraph Presentation["🎨 Presentation Layer"]
        direction TB
        UI[Compose UI]
        VM[ViewModels]
        Theme[Theme System]
    end
    
    subgraph Domain["⚙️ Domain Layer"]
        direction TB
        AI[AI Service]
        Screenshot[Screenshot Capture]
        Hotkey[Hotkey Manager]
    end
    
    subgraph Data["💾 Data Layer"]
        direction TB
        Repo[Repositories]
        DB[(SQLDelight)]
        Prefs[Settings]
    end
    
    subgraph External["🌐 External"]
        direction TB
        Gemini[Google Gemini API]
        Native[Native APIs]
    end
    
    UI --> VM
    VM --> AI
    VM --> Repo
    AI --> Gemini
    Screenshot --> Native
    Hotkey --> Native
    Repo --> DB
    
    style Presentation fill:#00BFA6,color:#0D1B2A
    style Domain fill:#FFB74D,color:#0D1B2A
    style Data fill:#B388FF,color:#0D1B2A
    style External fill:#1a2a3a,color:#E0E0E0
```

## Layer Responsibilities

### Presentation Layer

| Component | Responsibility |
|-----------|---------------|
| **Compose UI** | Render screens and handle user interactions |
| **ViewModels** | Manage UI state and business logic |
| **Theme** | Define colors, typography, and styling |

### Domain Layer

| Component | Responsibility |
|-----------|---------------|
| **AI Service** | Communicate with Gemini API |
| **Screenshot** | Capture screen content |
| **Hotkey Manager** | Handle global keyboard shortcuts |

### Data Layer

| Component | Responsibility |
|-----------|---------------|
| **Repositories** | Abstract data sources |
| **SQLDelight** | Local database storage |
| **Settings** | User preferences management |

## Technology Stack

```mermaid
mindmap
  root((Pirate-Parrot))
    UI
      Compose Multiplatform
      Material 3
      Custom Theme
    Networking
      Ktor Client
      JSON Serialization
    Storage
      SQLDelight
      Coroutines Extensions
    DI
      Koin
      Koin Compose
    Native
      JNA
      JNativeHook
      Platform APIs
```

## Data Flow

```mermaid
sequenceDiagram
    participant U as User
    participant UI as Compose UI
    participant VM as ViewModel
    participant AI as AIService
    participant API as Gemini API
    
    U->>UI: Capture Screenshot
    UI->>VM: onCaptureClick()
    VM->>VM: captureScreen()
    VM->>AI: analyzeCodingChallenge(image)
    AI->>API: POST /generateContent
    API-->>AI: JSON Response
    AI-->>VM: SolutionResponse
    VM->>VM: Update UI State
    VM-->>UI: State Flow
    UI-->>U: Display Solution
```

## Key Design Decisions

### 1. Compose Multiplatform

Chosen for:
- **Single codebase** for UI across platforms
- **Declarative UI** paradigm
- **Kotlin-first** development
- **Hot reload** support

### 2. Koin for DI

Chosen for:
- **Lightweight** - No code generation
- **Kotlin DSL** - Natural syntax
- **Compose integration** - Easy ViewModel injection

### 3. SQLDelight for Storage

Chosen for:
- **Type-safe SQL** - Compile-time verification
- **Multiplatform** - Works on JVM, Native, JS
- **Coroutines** - Flow-based queries

### 4. Ktor for Networking

Chosen for:
- **Kotlin-native** - Coroutines support
- **Multiplatform** - Same API everywhere
- **Extensible** - Plugin architecture

## Module Dependencies

```mermaid
graph TD
    A[composeApp] --> B[commonMain]
    A --> C[jvmMain]
    
    B --> D[Compose Runtime]
    B --> E[Ktor Client]
    B --> F[SQLDelight]
    B --> G[Koin]
    
    C --> H[Compose Desktop]
    C --> I[JNativeHook]
    C --> J[Platform APIs]
    
    style A fill:#00BFA6,color:#0D1B2A
    style B fill:#FFB74D,color:#0D1B2A
    style C fill:#B388FF,color:#0D1B2A
```

## Further Reading

- [Project Structure](project-structure.md) - Detailed file organization
- [Data Flow](data-flow.md) - How data moves through the app
- [AI Integration](ai-integration.md) - Gemini API integration details
