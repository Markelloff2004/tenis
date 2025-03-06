# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/ping-pong-tournament-1.0-SNAPSHOT.jar /app/ping-pong-tournament-1.0-SNAPSHOT.jar

# Expose port used by Vaadin
EXPOSE 8090

# Run the application with optimized JVM parameters
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/app/ping-pong-tournament-1.0-SNAPSHOT.jar"]
