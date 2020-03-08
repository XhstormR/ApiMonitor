buildscript {
    repositories {
        google()
        maven("https://maven.aliyun.com/repository/jcenter")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.1")
        classpath(kotlin("gradle-plugin", version = "1.3.70"))
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

allprojects {
    repositories {
        google()
        maven("https://maven.aliyun.com/repository/jcenter")
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
        gradleVersion = "6.2.2"
        distributionType = Wrapper.DistributionType.ALL
    }
}
