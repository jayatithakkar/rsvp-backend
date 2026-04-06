# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
# Copy the actual JAR file from your target folder
COPY target/*.jar app.jar

COPY pom.xml .
COPY src ./src

# Copy the JSON file into the SAME /app folder
COPY firebase-service-account.json /app/firebase-service-account.json

RUN mvn clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
