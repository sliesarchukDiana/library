FROM eclipse-temurin:21-jre-alpine
LABEL authors="PC"
WORKDIR /app
COPY target/borrow-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]