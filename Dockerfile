# STAGE 1: Build the application (The "Kitchen")
# We use a full Maven/JDK image here to compile the code
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src
COPY firebase-service-account.json .

# Build the JAR (This creates the file in /app/target/)
RUN mvn clean package -DskipTests

# STAGE 2: Run the application (The "Table")
# We use a tiny JRE image here to keep the file size small
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy ONLY the finished JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the Firebase JSON from the build stage into the final image
COPY --from=build /app/firebase-service-account.json /app/firebase-service-account.json

EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]