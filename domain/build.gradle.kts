plugins {
    kotlin("jvm") version "1.9.10"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    // testing
    testImplementation(kotlin("test"))
}

// Ensure tests run on JUnit platform (used by kotlin-test)
tasks.test {
    useJUnitPlatform()
}
