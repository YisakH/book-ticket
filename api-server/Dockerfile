FROM openjdk:17-jdk
LABEL authors="yshong"

ARG JAR_FILE=build/libs/ticket-api-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} docker-springboot.jar

ENTRYPOINT ["java", "-jar", "/docker-springboot.jar"]