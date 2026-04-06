# STAGE 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Compile the latest code (this ensures WebConfig and RsvpController are included)
RUN mvn clean package -DskipTests

# STAGE 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy ONLY the fresh JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# NOTE: No COPY for the JSON file here.
# Render will automatically mount your Secret File to /etc/secrets/
ENTRYPOINT ["java", "-jar", "app.jar"]