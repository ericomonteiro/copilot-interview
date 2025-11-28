# Interview Assistant

AI-powered desktop application that helps you solve coding challenges using Google Gemini AI with vision capabilities.

## 🚀 Features

- **Screenshot Analysis**: Capture coding challenges from your screen and get instant solutions
- **AI-Powered**: Google Gemini (FREE) with vision support
- **Multi-Language**: Kotlin, Java, Python, JavaScript, C++, Go, Rust
- **Fast Search**: Instant problem lookup from 30 pre-loaded problems
- **Solution Caching**: Offline access to previously generated solutions
- **Always on Top**: Window stays visible while you work

## 📋 Prerequisites

- JDK 17 or higher
- Google Gemini API Key ([Get one FREE here](https://makersuite.google.com/app/apikey))
- macOS 11+ or Windows 10+
- No credit card required!

## 🛠️ Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd copilot
```

### 2. Get Gemini API Key

1. Visit https://makersuite.google.com/app/apikey
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the key (starts with `AIza...`)

### 3. Configure API Key

**Option 1 - In the app**:
1. Run the app
2. Click Settings (⚙️)
3. Paste your API key
4. Done!

**Option 2 - Environment variable**:
```bash
# macOS/Linux
export OPENAI_API_KEY="your-gemini-key-here"

# Windows (PowerShell)
$env:OPENAI_API_KEY="your-gemini-key-here"
```

### 4. Build and Run

```bash
# macOS/Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

## 📦 Building Distributable Packages

### macOS DMG

```bash
./gradlew :composeApp:packageDmg
```

Output: `composeApp/build/compose/binaries/main/dmg/`

### Windows MSI

```bash
.\gradlew.bat :composeApp:packageMsi
```

Output: `composeApp\build\compose\binaries\main\msi\`

## 📖 Documentation

- [GEMINI_SETUP.md](./GEMINI_SETUP.md) - Complete Gemini setup guide
- [SCREENSHOT_FEATURE.md](./SCREENSHOT_FEATURE.md) - Screenshot analysis feature
- [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) - Common issues and solutions
- [CHANGELOG.md](./CHANGELOG.md) - Version history

## 🏗️ Project Structure

```
copilot/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/          # Shared code
│   │   │   ├── kotlin/
│   │   │   │   ├── ai/          # Gemini AI integration
│   │   │   │   ├── data/        # Repository & models
│   │   │   │   ├── ui/          # Compose UI
│   │   │   │   ├── screenshot/  # Screenshot capture
│   │   │   │   └── di/          # Dependency injection
│   │   │   └── sqldelight/      # Database schema
│   │   └── jvmMain/             # Desktop-specific
│   │       └── kotlin/
│   │           └── screenshot/  # Platform screenshot impl
│   └── build.gradle.kts
├── GEMINI_SETUP.md              # Gemini setup guide
├── SCREENSHOT_FEATURE.md        # Screenshot feature docs
└── README.md                    # This file
```

## 🔧 Technology Stack

- **Language**: Kotlin 2.2.20
- **UI**: Compose Multiplatform 1.9.1
- **Database**: SQLDelight 2.0.1
- **HTTP**: Ktor 2.3.7
- **DI**: Koin 3.5.3
- **AI**: Google Gemini API (gemini-2.5-flash)
- **Screenshot**: Java AWT Robot

## 🧪 Testing

```bash
./gradlew test
```

## 📝 Features

### ✅ Completed
- [x] Problem search and browsing (30 pre-loaded problems)
- [x] AI solution generation (Gemini)
- [x] Screenshot capture and analysis
- [x] Multi-language support (7 languages)
- [x] Solution caching
- [x] Settings management
- [x] Dark theme UI
- [x] Always-on-top window

### 📅 Planned
- [ ] Global hotkeys (Cmd+Shift+S)
- [ ] Region selection for screenshots
- [ ] Screenshot preview before analysis
- [ ] More programming languages
- [ ] Custom problem addition

## 🤝 Contributing

This is an MVP project. Contributions are welcome after the initial release.

## ⚠️ Disclaimer

This tool is for educational and practice purposes. Using AI assistance during actual interviews may violate interview guidelines. Use responsibly and ethically.

## 📄 License

See LICENSE file for details.

## 🎯 Quick Start

1. Get free Gemini API key: https://makersuite.google.com/app/apikey
2. Run: `./gradlew :composeApp:run`
3. Click Settings and paste your API key
4. Click the camera icon to capture a coding challenge
5. Get instant solution and explanation!

---

**Status**: Active Development
**Version**: 1.1.0
**License**: MIT