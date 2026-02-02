# Multi-stage Dockerfile for Kotlin Spring Boot (Gradle)
FROM gradle:8.4-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :web-api:clean :web-api:build -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/web-api/build/libs/web-api-0.1.0.jar ./web-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/web-api.jar"]
