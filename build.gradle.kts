import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        languageVersion = "1.8"
        apiVersion = "1.8"
    }
}