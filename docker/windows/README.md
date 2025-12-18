# Building Windows Installer for Pirate-Parrot

## Quick Summary

| Method | Output | Recommended For |
|--------|--------|-----------------|
| **GitHub Actions** | Native MSI installer | Production releases |
| **Docker (this)** | Cross-platform uber-jar | Development/testing |
| **Windows machine** | Native MSI installer | Local Windows builds |

## Option 1: GitHub Actions (Recommended for MSI)

A workflow is already configured at `.github/workflows/build-windows.yml`.

**Trigger a build:**
- Push a tag: `git tag v1.0.0 && git push --tags`
- Or manually trigger via GitHub Actions UI (workflow_dispatch)

**Output:** Native Windows MSI installer uploaded as artifact and release asset.

## Option 2: Docker (Cross-platform JAR)

Creates an uber-jar that runs on **any OS** with Java 21 installed.

```bash
# From project root
docker-compose -f docker-compose.windows.yml up --build

# Output location:
# ./build/installers/windows/pirate-parrot-*.jar
# ./build/installers/windows/run-pirate-parrot.bat
# ./build/installers/windows/README-WINDOWS.txt
```

**Note:** Docker on macOS/Linux cannot create native Windows MSI installers because:
- MSI packaging requires WiX Toolset (Windows-only)
- jpackage cross-compilation needs Windows-specific tools

The uber-jar is fully functional on Windows - users just need Java 21 installed.

## Option 3: Build on Windows

For native MSI installer, build directly on Windows:

```powershell
# PowerShell
./gradlew packageMsi

# Output: composeApp/build/compose/binaries/main/msi/pirate-parrot-1.0.0.msi
```

## Files

| File | Purpose |
|------|---------|
| `Dockerfile` | Docker image for building uber-jar |
| `build-windows.sh` | Build script executed in container |
| `.dockerignore` | Files excluded from Docker context |

## Troubleshooting

### Memory Issues
```bash
docker-compose -f docker-compose.windows.yml up --build
# Edit docker-compose.windows.yml to increase GRADLE_OPTS memory
```

### Slow Builds
Gradle cache is persisted in a Docker volume (`gradle-cache`). Subsequent builds will be faster.
