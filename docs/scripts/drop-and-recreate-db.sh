#!/bin/bash

echo "Stopping any running HMS application instances..."
# Find and kill any running Java processes with hms in the name
pkill -f "java.*hms"

echo "Dropping all tables from the HMS database..."
# Drop all tables in the public schema
PGPASSWORD=postgres psql -h localhost -U postgres -d hms -c "
-- Disable foreign key checks temporarily
SET session_replication_role = 'replica';

-- Get all table names from the public schema
DO \$\$
DECLARE
    r RECORD;
BEGIN
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
END \$\$;

-- Re-enable foreign key checks
SET session_replication_role = 'origin';
"

echo "All tables dropped successfully."

echo "Building and running the application with dev and kazakh profiles to regenerate the database..."
# Build the application
./mvnw clean package -DskipTests

# Run the application with both dev and kazakh profiles to create tables and load test data
java -jar target/hms-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev,kazakh

echo "Database has been regenerated with the dev and kazakh profile data!"
echo "You can now run './verify-database.sh' to check the database contents." 