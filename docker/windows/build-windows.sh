#!/bin/bash
set -e

echo "=========================================="
echo "Building Pirate-Parrot Windows Package"
echo "=========================================="
echo ""
echo "NOTE: Native MSI installers require Windows."
echo "This builds a distributable uber-jar package."
echo "For MSI, use GitHub Actions with windows-latest."
echo "=========================================="

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean --no-daemon

# Build the release distributable (cross-platform jar)
echo "Building release distributable..."
./gradlew :composeApp:createReleaseDistributable --no-daemon \
    -Pcompose.desktop.verbose=true \
    -Dorg.gradle.jvmargs="-Xmx4g" || true

# Also build uber-jar (works cross-platform with Java installed)
echo "Building uber-jar..."
./gradlew :composeApp:packageUberJarForCurrentOS --no-daemon \
    -Pcompose.desktop.verbose=true \
    -Dorg.gradle.jvmargs="-Xmx4g"

# Copy output to mounted volume
echo "Copying build artifacts to output directory..."
mkdir -p /app/output

# Copy uber-jar
find . -name "*.jar" -path "*/compose/jars/*" -exec cp {} /app/output/ \;

# Create a Windows launcher script
cat > /app/output/run-pirate-parrot.bat << 'EOF'
@echo off
echo Starting Pirate-Parrot...
java -jar pirate-parrot.jar
pause
EOF

# Create README for Windows users
cat > /app/output/README-WINDOWS.txt << 'EOF'
Pirate-Parrot for Windows
=========================

Requirements:
- Java 21 or later (https://adoptium.net/)

To run:
1. Install Java 21 if not already installed
2. Double-click run-pirate-parrot.bat
   OR
   Open command prompt and run: java -jar pirate-parrot.jar

For native MSI installer:
- Download from GitHub Releases (built via GitHub Actions)
- Or build on Windows: ./gradlew packageMsi
EOF

echo "=========================================="
echo "Build complete!"
echo ""
echo "Output files:"
ls -la /app/output/
echo ""
echo "To create MSI installer, use GitHub Actions"
echo "or build directly on Windows."
echo "=========================================="
