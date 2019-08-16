buildscript {
    repositories {
        google()
        maven("https://maven.aliyun.com/repository/jcenter")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.2")
        classpath(kotlin("gradle-plugin", version = "1.3.41"))
    }
}

allprojects {
    repositories {
        google()
        maven("https://maven.aliyun.com/repository/jcenter")
    }
}

tasks {
    register<Delete>("clean") {
        delete(rootProject.buildDir)
    }

    withType<Wrapper> {
        gradleVersion = "5.6"
        distributionType = Wrapper.DistributionType.ALL
    }
}
