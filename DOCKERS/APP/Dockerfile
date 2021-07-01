FROM maven:3.8.1-openjdk-11 AS MAVEN_BUILD
WORKDIR /app
COPY src /app/src
COPY pom.xml /app
RUN mvn -f pom.xml clean package -DskipTests

FROM openjdk:11-jdk
COPY DOCKERS DOCKERS
COPY --from=MAVEN_BUILD /app/target/demo-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]