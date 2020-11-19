import com.android.build.gradle.ProguardFiles.ProguardFile
import java.util.Properties
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

ktlint {
    android.set(true)
}

android {
    compileSdkVersion(30)
    defaultConfig {
        targetSdkVersion(30)
        minSdkVersion(25)
        applicationId = "com.example.leo.monitor"
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
    lintOptions {
        isCheckReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.preference:preference-ktx:1.1.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    implementation("com.squareup.moshi:moshi:1.11.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")

    implementation("com.github.topjohnwu.libsu:io:3.0.2")
}

/*
keytool -genkeypair -keystore release.jks -storepass 123456 -alias release -keyalg RSA -dname "cn=123" -validity 365

keytool -exportcert -keystore release.jks -storepass 123456 -alias release -file release.cer -rfc

keytool -printcert -file release.cer

https://docs.oracle.com/en/java/javase/12/tools/keytool.html
*/
