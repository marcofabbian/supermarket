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

    // JDBC support so the domain-level JdbcProductRepository compiles
    implementation("org.springframework:spring-jdbc:6.1.3")
    implementation("org.springframework:spring-context:6.1.3")

    // testing
    testImplementation(kotlin("test"))
}

// Ensure tests run on JUnit platform (used by kotlin-test)
tasks.test {
    useJUnitPlatform()
}
