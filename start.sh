#!/bin/bash

service ssh start

cd /app

mvn clean package -DskipTests

java -jar target/*.jar --server.port=8081 &

nginx -g "daemon off;"