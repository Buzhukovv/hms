#!/bin/bash

echo "Setting up PostgreSQL in Docker container for HMS..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed or not in PATH. Please install Docker first."
    exit 1
fi

# Check if the container is already running
if docker ps | grep -q "hms-postgres"; then
    echo "PostgreSQL container 'hms-postgres' is already running."
else
    # Check if the container exists but is stopped
    if docker ps -a | grep -q "hms-postgres"; then
        echo "Starting existing PostgreSQL container 'hms-postgres'..."
        docker start hms-postgres
    else
        echo "Creating and starting a new PostgreSQL container..."
        docker run --name hms-postgres \
            -e POSTGRES_PASSWORD=postgres \
            -e POSTGRES_USER=postgres \
            -e POSTGRES_DB=postgres \
            -p 5432:5432 \
            -d postgres:14
        
        # Wait for PostgreSQL to be ready
        echo "Waiting for PostgreSQL to start..."
        sleep 5
    fi
fi

# Verify the container is running
if docker ps | grep -q "hms-postgres"; then
    echo "PostgreSQL container is running successfully."
    echo "Connection details:"
    echo "  Host: localhost"
    echo "  Port: 5432"
    echo "  User: postgres"
    echo "  Password: postgres"
    echo "  Default Database: postgres"
    echo ""
    echo "You can now run ./setup-database.sh to set up the HMS database."
else
    echo "Error: Failed to start PostgreSQL container."
    exit 1
fi

# Show helpful commands
echo ""
echo "Useful Docker commands:"
echo "  docker logs hms-postgres        # View PostgreSQL logs"
echo "  docker stop hms-postgres        # Stop the container"
echo "  docker start hms-postgres       # Start the container"
echo "  docker rm -f hms-postgres       # Remove the container (will delete all data)" 