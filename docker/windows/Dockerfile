# Dockerfile for building Windows EXE/distributable for Pirate-Parrot
# Creates an uber-jar that can run on Windows with bundled JRE
# 
# NOTE: Building native MSI installers requires Windows. 
# This creates a distributable package that can be run on Windows.
# For MSI installers, use GitHub Actions with windows-latest runner.

FROM eclipse-temurin:21-jdk-jammy

# Avoid interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# Install required packages
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    zip \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files first (for caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Make gradlew executable
RUN chmod +x gradlew

# Download Gradle dependencies (cached layer)
RUN ./gradlew --no-daemon dependencies || true

# Copy the rest of the project
COPY composeApp composeApp

# Build script
COPY docker/windows/build-windows.sh /build-windows.sh
RUN chmod +x /build-windows.sh

# Output directory
VOLUME /app/output

CMD ["/build-windows.sh"]
