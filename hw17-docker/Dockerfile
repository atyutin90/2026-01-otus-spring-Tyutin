FROM maven:3.9.11-eclipse-temurin-17 AS builder
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY ./pom.xml $PROJECT_DIR
RUN mvn dependency:go-offline
COPY ./src $PROJECT_DIR/src
RUN mvn clean package -DskipTests
FROM bellsoft/liberica-openjdk-alpine:17
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=builder $PROJECT_DIR/target/hw17-docker*.jar $PROJECT_DIR/
EXPOSE 8080
CMD ["java", "-jar", "/opt/project/hw17-docker-0.0.1-SNAPSHOT.jar"]