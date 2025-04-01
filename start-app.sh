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

# Make sure we have the correct Maven wrapper
if [ ! -f ".mvn/wrapper/maven-wrapper.jar" ]; then
  echo "Maven wrapper not found, installing..."
  mkdir -p .mvn/wrapper
  curl -o .mvn/wrapper/maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
fi

# Run the Spring Boot application with reduced logging verbosity
echo "Starting Spring Boot application..."
LOGGING_LEVEL_ROOT=INFO \
LOGGING_LEVEL_ORG_HIBERNATE=WARN \
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=WARN \
../mvnw spring-boot:run

# Exit with the Maven exit code
exit $? 