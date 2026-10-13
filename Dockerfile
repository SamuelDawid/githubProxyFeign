FROM eclipse-temurin:21-jre
LABEL authors="Samuel Dawid"
COPY target/githubProxy-0.0.1-SNAPSHOT.jar githubProxy.jar
ENTRYPOINT ["java", "-jar", "githubProxy.jar"]