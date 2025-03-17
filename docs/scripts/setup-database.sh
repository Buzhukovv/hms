#!/bin/bash

echo "Setting up HMS PostgreSQL database..."

# Create the database if it doesn't exist
PGPASSWORD=postgres psql -U postgres -h localhost -c "SELECT 1 FROM pg_database WHERE datname = 'hms'" | grep -q 1 || PGPASSWORD=postgres psql -U postgres -h localhost -c "CREATE DATABASE hms"

# Switch to using the hms database for subsequent commands
PGPASSWORD=postgres psql -U postgres -h localhost -d postgres -c "
-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE hms TO postgres;
"

echo "Database setup complete."

# Update application.yml to use the hms database
echo "Updating application configuration to use the hms database..."

# Backup the original file
cp src/main/resources/application.yml src/main/resources/application.yml.bak

# Replace the database URL in the config
sed -i.bak 's/jdbc:postgresql:\/\/localhost:5432\/postgres/jdbc:postgresql:\/\/localhost:5432\/hms/g' src/main/resources/application.yml

echo "Configuration updated. Building and starting application with dev profile..."

# Build and run the application with the dev profile to load test data
./mvnw clean package -DskipTests

# Run the application with dev profile
java -jar target/hms-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

echo "Database populated with test data!" 