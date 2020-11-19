buildscript {
    repositories {
        google()
        maven("https://mirrors.huaweicloud.com/repository/maven")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath(kotlin("gradle-plugin", version = "1.4.20"))
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
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
        gradleVersion = "6.7"
        distributionType = Wrapper.DistributionType.ALL
    }
}
