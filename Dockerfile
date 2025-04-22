FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy entire project
COPY . .

# Give permission to gradlew
RUN chmod +x ./gradlew

# Build project and ensure resources are processed
RUN ./gradlew clean build -x test

# Run the jar (if you're producing it) OR use gradle run
CMD ["./gradlew", "run"]
