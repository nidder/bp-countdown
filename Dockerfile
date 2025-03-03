FROM gradle:7.6-jdk17-corretto as builder
WORKDIR /app
COPY . .
RUN gradle clean shadowJar
FROM openJDK:17-jre-slim
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]