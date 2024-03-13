FROM node:18-alpine AS frontend
WORKDIR /ang
COPY front/ /ang/
RUN npm install
RUN npm install -g @angular/cli@16.2.4
RUN ng build --configuration development --base-href="/"



FROM --platform=linux/x86_64 maven:3.8.4-openjdk-17 AS MAVEN_BUILD
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress -B


COPY src ./src
COPY --from=frontend /ang/dist/front/* /app/src/main/resources/static/
RUN mvn clean package -Dmaven.test.skip

FROM openjdk:17-jdk
WORKDIR /app
COPY DOCKERS DOCKERS 
COPY --from=MAVEN_BUILD /app/target/demo-0.0.1-SNAPSHOT.jar /app.jar


EXPOSE 8080
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:18080", "-jar","/app.jar"]
