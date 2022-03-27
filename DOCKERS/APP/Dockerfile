FROM maven:3.8.4-openjdk-11 AS MAVEN_BUILD
WORKDIR /app
COPY src /app/src
COPY pom.xml /app
RUN mvn -f pom.xml clean package -DskipTests

FROM openjdk:17-jdk
COPY DOCKERS DOCKERS
COPY --from=MAVEN_BUILD /app/target/demo-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:18080", "-jar","/app.jar"]