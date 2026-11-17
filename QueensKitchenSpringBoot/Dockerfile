# Build the application from source rather than relying on an untracked local JAR.
FROM gradle:7.6.4-jdk11 AS builder
WORKDIR /app
COPY . .
RUN gradle --no-daemon bootJar

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
