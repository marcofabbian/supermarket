plugins {
    kotlin("jvm") version "1.9.10" apply false
}

allprojects {
    group = "com.example.supermarket"
    version = "0.1.0"

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
