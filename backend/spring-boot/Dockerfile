# ---- SPRING BOOT DOCKERFILE ----
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Copy target jav files
COPY target/*.jar app.jar
# Expose port 8080 to be our endpoint
EXPOSE 8080
# Run the java app
ENTRYPOINT ["java", "-jar", "app.jar"]