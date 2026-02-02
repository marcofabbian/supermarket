# Multi-stage Dockerfile for the engine module (Gradle project)
FROM gradle:8.4-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :engine:clean :engine:build -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/engine/build/libs/engine-0.1.0.jar ./engine.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/engine.jar"]
