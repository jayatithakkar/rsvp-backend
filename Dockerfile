# STAGE 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# STAGE 2: Run
# ❌ REMOVED: eclipse-temurin:17-jre-alpine
# ✅ ADDED: eclipse-temurin:17-jre-jammy (This supports the Netty SSL libraries)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the fresh JAR
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# The app will look for the secret file in /etc/secrets/ as configured in your Java code
ENTRYPOINT ["java", "-jar", "app.jar"]