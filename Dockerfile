FROM openjdk:17-alpine

RUN apk update && \
    apk add --no-cache maven curl

WORKDIR /app

COPY pom.xml .
COPY src/ src/

RUN mvn dependency:go-offline

RUN mvn clean package

CMD ["java", "-jar", "target/ITKAcademyTest.jar"]