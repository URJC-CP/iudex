FROM maven:3.8.4-openjdk-17 AS MAVEN_BUILD
WORKDIR /app

COPY pom.xml .
RUN mvn clean package -Dmaven.main.skip -Dmaven.test.skip && rm -r target 

COPY src ./src
RUN mvn clean package -Dmaven.test.skip

FROM openjdk:17-jdk
WORKDIR /app
COPY --from=MAVEN_BUILD /app/target/demo-0.0.1-SNAPSHOT.jar /app.jar


EXPOSE 8080
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:18080", "-jar","/app.jar"]