FROM eclipse-temurin:21-jre-alpine
LABEL authors="PC"
WORKDIR /app
COPY target/web-ui-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]