#!/bin/bash

echo "Building application..."
./mvnw clean package -DskipTests

echo "Starting application with dev profile to load test data..."
java -jar target/hms-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

echo "Test data loading complete!" 