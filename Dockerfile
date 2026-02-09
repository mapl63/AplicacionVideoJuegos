FROM amazoncorretto:25-alpine

COPY target/AplicacionVideoJuegos.jar app.jar

LABEL authors="2daw"

ENTRYPOINT ["java", "-jar", "/app.jar"]