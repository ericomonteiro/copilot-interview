import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            
            // Coroutines
            implementation(libs.kotlinx.coroutinesCore)
            
            // Serialization
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.kotlinx.datetime)
            
            // Networking
            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.serializationJson)
            
            // Database
            implementation(libs.sqldelight.coroutines)
            
            // DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            
            // Code Editor
            implementation(libs.compose.code.editor)
            
            // Native
            implementation(libs.jna)
            implementation(libs.jna.platform)
            implementation(libs.java.objc.bridge)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.clientCio)
            implementation(libs.sqldelight.driver)
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.github.ericomonteiro.copilot.db")
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.github.ericomonteiro.copilot.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "interview-copilot"
            packageVersion = "1.0.0"
            
            modules("java.sql")
        }
    }
}
