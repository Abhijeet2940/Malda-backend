FROM eclipse-temurin:17

WORKDIR /app

# Copy everything
COPY . .

# Move into correct folder
WORKDIR /app/mri-backend

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Run the jar
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar target/*.jar"]
