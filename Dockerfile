# Etapa 1: compilacion de los 7 modulos con Maven y JDK 21
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

# Se copian primero los POM para aprovechar la cache de capas de Docker
COPY pom.xml ./
COPY common/pom.xml common/
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY maria-output-adapter/pom.xml maria-output-adapter/
COPY mongo-output-adapter/pom.xml mongo-output-adapter/
COPY rest-input-adapter/pom.xml rest-input-adapter/
COPY cli-input-adapter/pom.xml cli-input-adapter/

COPY common/src common/src
COPY domain/src domain/src
COPY application/src application/src
COPY maria-output-adapter/src maria-output-adapter/src
COPY mongo-output-adapter/src mongo-output-adapter/src
COPY rest-input-adapter/src rest-input-adapter/src
COPY cli-input-adapter/src cli-input-adapter/src

RUN mvn -B -ntp clean package -DskipTests

# Etapa 2a: imagen ligera para la API REST (Swagger / OpenAPI 3)
FROM eclipse-temurin:21-jre-alpine AS rest
WORKDIR /app
COPY --from=build /app/rest-input-adapter/target/rest-input-adapter-*.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Etapa 2b: imagen ligera para la aplicacion de consola (CLI)
FROM eclipse-temurin:21-jre-alpine AS cli
WORKDIR /app
COPY --from=build /app/cli-input-adapter/target/cli-input-adapter-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
