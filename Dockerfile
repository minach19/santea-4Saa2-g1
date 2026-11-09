FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
ARG SERVICE_MODULE
COPY --from=build /workspace/Backend/${SERVICE_MODULE}/target/${SERVICE_MODULE}-1.0.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
