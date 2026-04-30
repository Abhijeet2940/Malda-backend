FROM eclipse-temurin:17

WORKDIR /app

# Copy everything
COPY . .

# Build the project
RUN ./mvnw clean package -DskipTests

# Run the jar
CMD ["java", "-Dserver.port=${PORT}", "-jar", "target/mit-approval-system-0.0.1-SNAPSHOT.jar"]
