FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]