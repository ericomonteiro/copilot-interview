# 🦜 Pirate-Parrot

> Your AI-powered study companion for coding interviews and certification exams.

## Welcome

Pirate-Parrot is a cross-platform desktop application that captures your screen, analyzes content using Google's Gemini AI, and provides intelligent solutions and answers in real-time.

**Key Highlights:**

- 🖥️ **Code Challenge Mode** - Get complete solutions for coding problems
- 📜 **AWS Certification Mode** - Ace your AWS certification exams
- 📝 **Generic Exam Mode** - Support for ENEM, Vestibular, Concursos
- 🔒 **Stealth Mode** - Invisible to screen sharing and recording
- ⌨️ **Global Hotkeys** - Quick capture from anywhere

## Quick Start

```bash
# Clone and run
git clone https://github.com/ericomonteiro/pirate-parrot-ai.git
cd pirate-parrot-ai
./gradlew :composeApp:run
```

Then configure your [Gemini API Key](getting-started/configuration.md) in Settings.

## Architecture Overview

```mermaid
graph TB
    subgraph UI["🎨 UI Layer"]
        Home[Home Screen]
        Code[Code Challenge]
        Cert[Certification]
        Exam[Generic Exam]
        Settings[Settings]
    end
    
    subgraph Core["⚙️ Core Layer"]
        VM[ViewModels]
        Screenshot[Screenshot Capture]
        Hotkey[Global Hotkeys]
    end
    
    subgraph Data["💾 Data Layer"]
        Repo[Repositories]
        DB[(SQLDelight DB)]
    end
    
    subgraph External["🌐 External"]
        Gemini[Google Gemini AI]
    end
    
    Home --> Code
    Home --> Cert
    Home --> Exam
    Home --> Settings
    
    Code --> VM
    Cert --> VM
    Exam --> VM
    
    VM --> Screenshot
    VM --> Repo
    VM --> Gemini
    
    Hotkey --> Screenshot
    Repo --> DB
    
    style UI fill:#00BFA6,color:#0D1B2A
    style Core fill:#FFB74D,color:#0D1B2A
    style Data fill:#B388FF,color:#0D1B2A
    style External fill:#1a2a3a,color:#E0E0E0
```

## Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 2.2.20 |
| **UI Framework** | Compose Multiplatform 1.9.1 |
| **AI Provider** | Google Gemini API |
| **Networking** | Ktor 2.3.7 |
| **Database** | SQLDelight 2.0.1 |
| **DI** | Koin 3.5.3 |

## Documentation Sections

- **[Getting Started](getting-started/)** - Installation, configuration, and first steps
- **[Features](features/)** - Detailed feature documentation
- **[Architecture](architecture/)** - Technical architecture and design
- **[API Reference](api/)** - API documentation and integration
- **[Contributing](contributing.md)** - How to contribute to the project
