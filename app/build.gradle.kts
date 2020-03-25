import com.android.build.gradle.ProguardFiles.ProguardFile
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

ktlint {
    android.set(true)
}

android {
    compileSdkVersion(29)
    defaultConfig {
        targetSdkVersion(29)
        minSdkVersion(25)
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
            proguardFile(getDefaultProguardFile(ProguardFile.OPTIMIZE.fileName))
            proguardFile("proguard-rules.pro")
        }
    }
    lintOptions {
        isCheckReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude("**/*.kotlin_module")
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_builtins")
        exclude("**/*.version")
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")

    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")

    implementation("androidx.preference:preference:1.1.0")

    implementation("com.squareup.retrofit2:retrofit:2.7.2")
    implementation("com.squareup.retrofit2:converter-gson:2.7.2")

    implementation("com.google.code.gson:gson:2.8.6")

    implementation("com.github.topjohnwu.libsu:core:2.5.1")
    implementation("com.github.topjohnwu.libsu:io:2.5.1")
}

/*
keytool -genkeypair -keystore release.jks -storepass 123456 -alias release -keyalg RSA -dname "cn=123" -validity 365

keytool -exportcert -keystore release.jks -storepass 123456 -alias release -file release.cer -rfc

keytool -printcert -file release.cer

https://docs.oracle.com/en/java/javase/12/tools/keytool.html
*/
