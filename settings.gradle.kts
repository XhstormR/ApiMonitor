rootProject.buildFileName = "build.gradle.kts"

include(":app")

pluginManagement {
    repositories {
        google()
        maven("https://repo.huaweicloud.com/repository/maven/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        maven("https://repo.huaweicloud.com/repository/maven/")
    }
}
