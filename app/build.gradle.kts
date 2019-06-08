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
    packagingOptions {
        exclude("**/*.kotlin_module")
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_builtins")
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")

    compileOnly("androidx.annotation:annotation:1.0.2")
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
}

/*
keytool -genkeypair -keystore release.jks -storepass 123456 -alias release -keyalg RSA -dname "cn=123" -validity 365

keytool -exportcert -keystore release.jks -storepass 123456 -alias release -file release.cer -rfc

keytool -printcert -file release.cer

https://docs.oracle.com/en/java/javase/12/tools/keytool.html
*/
