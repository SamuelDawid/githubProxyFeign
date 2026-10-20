# Etap 1: build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY model/pom.xml model/
COPY domain/pom.xml domain/
COPY public/pom.xml public/
COPY adapter/pom.xml adapter/


COPY model/src model/src
COPY domain/src domain/src
COPY public/src public/src
COPY adapter/src adapter/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl adapter -am package -DskipTests

FROM eclipse-temurin:21-jre
LABEL authors="Samuel Dawid"
WORKDIR /app
RUN useradd --system --uid 1001 appuser

COPY --from=build /app/adapter/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]