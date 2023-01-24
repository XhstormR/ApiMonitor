import com.android.build.gradle.ProguardFiles.ProguardFile
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 33
    defaultConfig {
        targetSdk = 33
        minSdk = 29
        applicationId = "com.example.myapplication"
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        val properties = Properties().apply {
            file("keystore/keystore.properties").inputStream().use { this.load(it) }
        }
        register("release") {
            storeFile = file(properties.getProperty("storeFile"))
            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFile(getDefaultProguardFile(ProguardFile.OPTIMIZE.fileName))
            proguardFile("proguard-rules.pro")
        }
    }
    lint {
        checkReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    packagingOptions {
        resources{
            excludes.add("**/*.kotlin_module")
            excludes.add("**/*.kotlin_metadata")
            excludes.add("**/*.kotlin_builtins")
            excludes.add("**/*.version")
        }
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    compileOnly("androidx.annotation:annotation:1.5.0")
}
