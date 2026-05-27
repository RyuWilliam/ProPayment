FROM eclipse-temurin:24 AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew
RUN ./gradlew clean build -x test

FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 7021

ENTRYPOINT ["java", "-jar", "app.jar"]