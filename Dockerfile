# Use maven image
FROM maven:3.9.9-eclipse-temurin-23 AS builder

# Set the working directory in the container
WORKDIR /app


# Copy the pom.xml and the source code
COPY pom.xml .
COPY src ./src
COPY config ./config


# Build project with maven
RUN mvn clean package

# Java version to run the program
FROM openjdk:23

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the builder
COPY --from=builder /app/target/*.jar qr-generater-backend.jar

# Run the application with the server configuration active
CMD ["java", "-Dspring.profiles.active=server", "-jar", "qr-generater-backend.jar"]