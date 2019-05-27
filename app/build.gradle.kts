import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        targetSdkVersion(28)
        minSdkVersion(19)
        applicationId = "com.example.leo.myapplication"
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        register("release") {
            storeFile = file("keystore/release.jks")
            storePassword = "123456"
            keyAlias = "release"
            keyPassword = "123456"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    lintOptions {
        isCheckReleaseBuilds = false
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")

    compileOnly("androidx.annotation:annotation:1.0.2")
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
}
