plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))

    //implementation("ai.koog:agents-core")      // core agent SDK
    //implementation("ai.koog:prompt-executor-openai-client") // OpenAI LLM
    //implementation("io.ktor:ktor-client-cio:2.3.0")          // HTTP client }

    testImplementation(kotlin("test"))
}