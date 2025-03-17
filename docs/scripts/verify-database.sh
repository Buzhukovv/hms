#!/bin/bash

echo "Verifying HMS database population..."
echo "==================================="

# Set database connection parameters
DB_NAME="hms"
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_HOST="localhost"
DB_PORT="5432"

# Function to execute a query and print the results
execute_query() {
  echo -e "\n$1:"
  PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "$2"
}

# Check if the database exists
echo "Checking database connection..."
if ! PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c '\q' 2>/dev/null; then
  echo "Error: Cannot connect to the database. Please make sure PostgreSQL is running and the database exists."
  exit 1
fi
echo "Database connection successful."

# List all tables in the database
echo -e "\nListing all tables in the database:"
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "\dt"

# Get database information
execute_query "Database Schema Information" "SELECT schemaname, tablename FROM pg_tables WHERE schemaname = 'public';"

# Check if any table exists
table_count=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM pg_tables WHERE schemaname = 'public';")
if [ "$table_count" -eq 0 ]; then
  echo -e "\nNo tables found in the public schema. The database might be empty or tables might be in a different schema."
  
  # List all schemas
  execute_query "Available Schemas" "SELECT schema_name FROM information_schema.schemata;"
  
  # Exit with a message
  echo -e "\nDatabase verification failed. Please check the application logs to ensure the data was properly populated."
  exit 1
fi

# If tables exist, continue with the queries
echo -e "\nContinuing with table queries..."

# Query tables to verify data
execute_query "User Base Table Count" "SELECT COUNT(*) FROM user_base;"
execute_query "Student Table Count" "SELECT COUNT(*) FROM user_students;"
execute_query "Property Base Table Count" "SELECT COUNT(*) FROM property_base;"
execute_query "Dormitory Rooms Table Count" "SELECT COUNT(*) FROM property_dormitory_rooms;"
execute_query "Leases Table Count" "SELECT COUNT(*) FROM leases;"
execute_query "Maintenance Requests Table Count" "SELECT COUNT(*) FROM maintenance_requests;"

# Get detailed information about the student
execute_query "Student Information" "SELECT id, first_name, last_name, email, role FROM user_base JOIN user_students ON user_base.id = user_students.id;"

# Get detailed information about the dormitory room
execute_query "Dormitory Room Information" "SELECT id, property_number, property_block, rent, status FROM property_base JOIN property_dormitory_rooms ON property_base.id = property_dormitory_rooms.id;"

# Get detailed information about the lease
execute_query "Lease Information" "SELECT lease_number, tenant_id, property_id, status, start_date, end_date FROM leases;"

# Get detailed information about the maintenance request
execute_query "Maintenance Request Information" "SELECT title, description, requester_id, lease_id, status FROM maintenance_requests;"

echo -e "\nDatabase verification complete!" 