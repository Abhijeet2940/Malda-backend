# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY mri-backend ./mri-backend

WORKDIR /app/mri-backend
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17

WORKDIR /app
COPY --from=builder /app/mri-backend/target/*.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
