# Build Instructions

## Prerequisites

- **JDK 21** or later
- **Gradle** (wrapper included)

## Build Commands

### Run the Application (Development)

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
**Must be run on Windows:**
```bash
gradlew.bat :composeApp:packageMsi
```
Output: `composeApp/build/compose/binaries/main/msi/`

**Note:** Building Windows MSI requires [WiX Toolset](https://wixtoolset.org/) to be installed.

#### Linux (.deb)
**Must be run on Linux:**
```bash
./gradlew :composeApp:packageDeb
```
Output: `composeApp/build/compose/binaries/main/deb/`

### Build All Formats for Current OS
```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

### Create Uber JAR (Cross-platform)
```bash
./gradlew :composeApp:packageUberJarForCurrentOS
```
Output: `composeApp/build/compose/jars/`

The uber JAR can be run on any platform with Java 21:
```bash
java -jar pirate-parrot-*.jar
```

## Global Hotkeys

| Platform | Screenshot | Toggle Stealth |
|----------|------------|----------------|
| macOS    | `Cmd+Shift+Opt+S` | `Cmd+Shift+Opt+B` |
| Windows  | `Ctrl+Shift+Alt+S` | `Ctrl+Shift+Alt+B` |

## Platform-Specific Notes

### macOS
- Grant **Accessibility** permission for global hotkeys: System Preferences → Security & Privacy → Privacy → Accessibility
- Grant **Screen Recording** permission for screenshots: System Preferences → Security & Privacy → Privacy → Screen Recording

### Windows
- Run as Administrator if hotkeys don't work
- Windows Defender may flag the app initially - allow it through

### Linux
- May require `libxkbcommon` for global hotkeys
- Run with `sudo` if hotkeys don't register
