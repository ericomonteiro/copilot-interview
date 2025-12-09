# 🦜 Pirate-Parrot

**Your AI-powered study companion for coding interviews and certification exams.**

Pirate-Parrot is a cross-platform desktop application that captures your screen, analyzes content using Google's Gemini AI, and provides intelligent solutions and answers in real-time. It runs in **stealth mode**, making it invisible to screen sharing and recording software.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-purple?logo=kotlin)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.9.1-blue?logo=jetpackcompose)
![Platform](https://img.shields.io/badge/Platform-macOS%20%7C%20Windows%20%7C%20Linux-lightgrey)
![License](https://img.shields.io/badge/License-MIT-green)

---

## ✨ Features

### 🖥️ Code Challenge Mode
- Capture screenshots of coding problems (LeetCode, HackerRank, CodeSignal, etc.)
- Get complete solutions with syntax-highlighted code
- Receive time and space complexity analysis
- Supports multiple programming languages (Kotlin, Java, Python, JavaScript, Go, Rust, C++, and more)

### 📜 AWS Certification Mode
- Analyze AWS certification exam questions
- Get correct answers with detailed explanations
- Understand why incorrect options are wrong
- Supports all major AWS certifications:
  - Cloud Practitioner
  - Solutions Architect (Associate & Professional)
  - Developer Associate
  - SysOps Administrator
  - DevOps Engineer Professional

### 📝 Generic Exam Mode
- Support for Brazilian exams: ENEM, Vestibular, Concursos, OAB, ENADE
- Multi-language detection (Portuguese, English, Spanish)
- Subject and topic identification
- Study tips and explanations

### 🔒 Stealth Mode
- **Invisible to screen capture** - The app window is hidden from screen sharing, screenshots, and recording software
- Toggle with keyboard shortcut or settings
- Perfect for maintaining privacy during video calls

### ⌨️ Global Hotkeys
| Platform | Screenshot | Toggle Stealth |
|----------|------------|----------------|
| macOS    | `Cmd+Shift+Opt+S` | `Cmd+Shift+Opt+B` |
| Windows  | `Ctrl+Shift+Alt+S` | `Ctrl+Shift+Alt+B` |

---

## 🚀 Getting Started

### Prerequisites

- **JDK 21** or later
- **Gemini API Key** (free from [Google AI Studio](https://aistudio.google.com))

### Installation

#### Option 1: Download Pre-built Package
Download the latest release for your platform:
- **macOS**: `.dmg` file
- **Windows**: `.msi` installer
- **Linux**: `.deb` package

#### Option 2: Build from Source
```bash
# Clone the repository
git clone https://github.com/ericomonteiro/pirate-parrot-ai.git
cd pirate-parrot-ai

# Run the application
./gradlew :composeApp:run
```

### Configuration

1. Launch Pirate-Parrot
2. Go to **Settings** (⚙️ icon)
3. Enter your **Gemini API Key**
4. (Optional) Select your preferred AI model and programming language
5. Start capturing!

---

## 🛠️ Build Instructions

### Run in Development Mode
```bash
./gradlew :composeApp:run
```

### Build Distribution Packages

#### macOS (.dmg)
```bash
./gradlew :composeApp:packageDmg
```
Output: `composeApp/build/compose/binaries/main/dmg/`

#### Windows (.msi)
```bash
# Must be run on Windows
gradlew.bat :composeApp:packageMsi
```
> **Note:** Requires [WiX Toolset](https://wixtoolset.org/) to be installed.

#### Linux (.deb)
```bash
# Must be run on Linux
./gradlew :composeApp:packageDeb
```

#### Cross-platform Uber JAR
```bash
./gradlew :composeApp:packageUberJarForCurrentOS
```
Run with: `java -jar pirate-parrot-*.jar`

---

## 🏗️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 2.2.20 |
| **UI Framework** | Compose Multiplatform 1.9.1 |
| **AI Provider** | Google Gemini API |
| **Networking** | Ktor 2.3.7 |
| **Database** | SQLDelight 2.0.1 |
| **DI** | Koin 3.5.3 |
| **Native Access** | JNA 5.14.0, JNativeHook 2.2.2 |

---

## 📁 Project Structure

```
pirate-parrot-ai/
├── composeApp/
│   └── src/
│       ├── commonMain/          # Shared code
│       │   └── kotlin/
│       │       └── com/github/ericomonteiro/copilot/
│       │           ├── ai/      # AI service (Gemini integration)
│       │           ├── data/    # Repositories & data layer
│       │           ├── di/      # Dependency injection
│       │           ├── ui/      # Compose UI screens
│       │           └── util/    # Utilities & helpers
│       └── jvmMain/             # JVM-specific code
│           └── kotlin/
│               └── com/github/ericomonteiro/copilot/
│                   ├── hotkey/      # Global hotkey handling
│                   ├── platform/    # Platform-specific (Window management)
│                   └── screenshot/  # Screen capture
├── native/
│   └── macos/                   # Native macOS code (stealth mode)
├── gradle/
│   └── libs.versions.toml       # Version catalog
└── BUILD.md                     # Detailed build instructions
```

---

## ⚙️ Platform-Specific Notes

### macOS
- Grant **Accessibility** permission for global hotkeys:  
  `System Preferences → Security & Privacy → Privacy → Accessibility`
- Grant **Screen Recording** permission for screenshots:  
  `System Preferences → Security & Privacy → Privacy → Screen Recording`

### Windows
- Run as Administrator if hotkeys don't work
- Windows Defender may flag the app initially - allow it through

### Linux
- May require `libxkbcommon` for global hotkeys
- Run with `sudo` if hotkeys don't register

---

## 🔑 API Key Setup

1. Visit [Google AI Studio](https://aistudio.google.com)
2. Sign in with your Google account
3. Click **"Create API Key"**
4. Copy the key and paste it in Pirate-Parrot Settings

> **Note:** The free tier includes generous usage limits suitable for personal use.

---

## 🎨 UI Theme

Pirate-Parrot features a modern ocean-themed dark UI:
- **Primary**: Vibrant Teal (#00BFA6) - Ocean theme
- **Secondary**: Warm Orange/Gold (#FFB74D) - Treasure theme
- **Tertiary**: Deep Purple (#B388FF) - Mystery theme
- **Background**: Rich Dark Blue (#0D1B2A) - Night Sea

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## ⚠️ Disclaimer

This tool is intended for **educational and study purposes only**. Please use it responsibly and in accordance with the terms of service of any platforms or exams you're preparing for. The developers are not responsible for any misuse of this application.

---

<p align="center">
  Made with ❤️ and 🦜 by <a href="https://github.com/ericomonteiro">Erico Monteiro</a>
</p>
