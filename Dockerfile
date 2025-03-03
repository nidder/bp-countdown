FROM gradle:8.13-jdk17 as builder
WORKDIR /app
COPY . .
RUN gradle build
FROM openjdk:17-jre-slim
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]