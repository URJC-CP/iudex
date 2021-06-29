FROM maven:3.8.1-openjdk-11 AS MAVEN_BUILD
WORKDIR /app
COPY . /app
RUN mvn clean package

FROM openjdk:11-jdk
COPY --from=MAVEN_BUILD ./target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]