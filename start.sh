#!/bin/bash
mvn clean package -DskipTests=true
docker-compose up --build
