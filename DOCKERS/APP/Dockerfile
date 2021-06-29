FROM openjdk:11-jdk
MAINTAINER urjc-cp
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY DOCKERS /DOCKERS
ENTRYPOINT ["java","-jar","/app.jar"]