# Use an official Maven image to handle the build process
FROM maven:3.9.9-eclipse-temurin-23 AS builder

# Set the working directory in the container
WORKDIR /app

# Replace <GITHUB_URL> with your repository's URL
RUN apt-get update && apt-get install -y git && \
    git clone https://github.com/VictorHarbo/qr-generater-backend.git .

# Run Maven to build the project
RUN mvn clean package

# Use a lightweight JDK runtime image to run the application
FROM openjdk:23

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Specify the command to run the application
CMD ["java", "-jar", "app.jar"]