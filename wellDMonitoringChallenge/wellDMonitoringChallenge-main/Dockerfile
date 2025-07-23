FROM  eclipse-temurin:17.0.15_6-jre-ubi9-minimal

WORKDIR /app

COPY target/monitoring-test-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]