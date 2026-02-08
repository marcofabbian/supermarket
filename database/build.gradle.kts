plugins {
    kotlin("jvm") version "1.9.10"
    id("org.flywaydb.flyway") version "9.20.0"
}

group = rootProject.group
version = rootProject.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.postgresql:postgresql:42.5.4")
}

// Flyway plugin is applied. Configure connection details via one of these methods:
//  - module gradle properties: database/gradle.properties (defaults checked in this repo)
//  - override on the CLI: ./gradlew :database:flywayMigrate -Pflyway.url=... -Pflyway.user=... -Pflyway.password=...
//  - or export environment variables and pass them to the Gradle invocation
// We avoid referencing the typed Flyway extension in this script to keep IDE/static checks happy.

// Tidy up task metadata for discoverability
tasks.named("flywayMigrate") {
    group = "database"
    description = "Run Flyway migrations for the database module"
}
