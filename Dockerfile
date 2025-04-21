FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 8083
COPY ./target/schedule-consumer-service-0.0.1-SNAPSHOT.jar schedule-consumer-service.jar
COPY .env .env

ENTRYPOINT ["java", "-jar", "schedule-consumer-service.jar"]