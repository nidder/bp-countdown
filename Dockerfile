FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the project files into the container
COPY . .

# Copy resources to the appropriate location
COPY src/main/resources /app/resources

# Stop Gradle daemon before running any command
RUN gradle --stop

# Clear Gradle cache
RUN rm -rf ~/.gradle/caches

# Build the project without running tests
RUN gradle build -x test

# Run the application using gradle run
CMD ["gradle", "run"]
