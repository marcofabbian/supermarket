plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))

    // JDBC support for DB-backed repository
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("org.postgresql:postgresql:42.5.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
