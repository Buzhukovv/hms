#!/bin/bash

# Check if the port is already in use and kill the process
port=8080
pid=$(lsof -t -i:$port)
if [ -n "$pid" ]; then
  echo "Port $port is in use by process $pid. Killing it..."
  kill -9 $pid
fi

# Change to the project directory
cd "$(dirname "$0")/hms"

# Run the Spring Boot application
../mvnw spring-boot:run

# Exit with the Maven exit code
exit $? 