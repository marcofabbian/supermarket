plugins {
    kotlin("jvm") version "1.9.10"
}

group = rootProject.group
version = rootProject.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

// No runtime dependencies for now; SQL migration lives in resources
dependencies {
    implementation(kotlin("stdlib"))
}
