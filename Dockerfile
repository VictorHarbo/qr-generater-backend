# Use maven image
FROM maven:3.9.9-eclipse-temurin-23 AS builder

# Set the working directory in the container
WORKDIR /app

# Clone project from github
RUN apt-get update && apt-get install -y git && \
    git clone https://github.com/VictorHarbo/qr-generater-backend.git .

# Build project with maven
RUN mvn clean package

# Java version to run the program
FROM openjdk:23

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the builder
COPY --from=builder /app/target/*.jar qr-generater-backend.jar

# Run the application
CMD ["java", "-jar", "qr-generater-backend.jar"]