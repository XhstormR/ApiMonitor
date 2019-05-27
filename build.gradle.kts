buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.1")
        classpath(kotlin("gradle-plugin", version = "1.3.31"))
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks {
    register<Delete>("clean") {
        delete(rootProject.buildDir)
    }

    withType<Wrapper> {
        gradleVersion = "5.4.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}
