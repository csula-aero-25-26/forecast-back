# ---- BUILD STAGE ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -e -DskipTests package

# ---- RUN STAGE ----
FROM eclipse-temurin:21-jdk

ENV SPRING_PROFILES_ACTIVE=docker

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
