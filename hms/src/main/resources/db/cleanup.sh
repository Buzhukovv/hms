#!/bin/bash

# Database credentials
DB_HOST="13.48.128.129"
DB_PORT="5432"
DB_NAME="hms_db"
DB_USER="hms_user"
DB_PASSWORD="hms_password"

# Execute the SQL script
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f cleanup.sql

echo "Database cleanup completed!" 