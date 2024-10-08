# Use a Maven image to build the application
FROM maven:3.8.4-openjdk-11 AS build

# Set the working directory
WORKDIR /usr/src/app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the project (this will generate the JAR file)
RUN mvn clean package

# Use a slim OpenJDK runtime image for the final stage
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /usr/src/app

# Install procps to make ps command available
RUN apt-get update && apt-get install -y procps

# Copy the JAR file from the build stage
COPY --from=build /usr/src/app/target/service1-1.0-SNAPSHOT.jar ./

# Expose port 8199 for the HTTP server
EXPOSE 8199

# Run the Java service
ENTRYPOINT ["java", "-jar", "service1-1.0-SNAPSHOT.jar"]
