FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

CMD ["./gradlew", "run"]
