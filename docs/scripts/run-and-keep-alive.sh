#!/bin/bash

echo "Building the application..."
./mvnw clean package -DskipTests

echo "Starting application with kazakh profile in the background..."
java -jar target/hms-0.0.1-SNAPSHOT.jar --spring.profiles.active=kazakh &
APP_PID=$!

echo "Application started with PID: $APP_PID"
echo "Waiting for 15 seconds to let the application initialize and populate the database..."
sleep 15

echo "You can now verify the database with: ./verify-database.sh"
echo "When you're done, you can stop the application with: kill $APP_PID" 