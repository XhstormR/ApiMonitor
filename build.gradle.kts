buildscript {
    repositories {
        google()
        maven("https://mirrors.huaweicloud.com/repository/maven")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath(kotlin("gradle-plugin", version = "1.4.32"))
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

allprojects {
    repositories {
        google()
        maven("https://mirrors.huaweicloud.com/repository/maven")
        maven("https://jitpack.io")
    }
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
    }
}

tasks {
    register<Delete>("clean") {
        delete(rootProject.buildDir)
    }

    withType<Wrapper> {
        gradleVersion = "6.8.2"
        distributionType = Wrapper.DistributionType.ALL
    }
}
