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
    buildTypes {
        getByName("release") {
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
    compileOnly("de.robv.android.xposed:api:82:sources")

    compileOnly("com.android.support:support-annotations:28.0.0")
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
}
