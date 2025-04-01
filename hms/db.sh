#!/bin/bash

# Function to display usage
show_usage() {
    echo "Usage: $0 [start|stop|restart|status|logs]"
    echo "  start   - Start the database container"
    echo "  stop    - Stop the database container"
    echo "  restart - Restart the database container"
    echo "  status  - Check the status of the database container"
    echo "  logs    - View the database container logs"
    exit 1
}

# Check if command is provided
if [ $# -eq 0 ]; then
    show_usage
fi

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo "Error: Docker is not running"
        exit 1
    fi
}

# Function to check if Docker Compose is installed
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        echo "Error: Docker Compose is not installed"
        exit 1
    fi
}

# Main script logic
case "$1" in
    start)
        check_docker
        check_docker_compose
        echo "Starting database container..."
        docker-compose up -d postgres
        echo "Waiting for database to be ready..."
        sleep 5
        ;;
    stop)
        check_docker
        check_docker_compose
        echo "Stopping database container..."
        docker-compose down
        ;;
    restart)
        check_docker
        check_docker_compose
        echo "Restarting database container..."
        docker-compose restart postgres
        echo "Waiting for database to be ready..."
        sleep 5
        ;;
    status)
        check_docker
        check_docker_compose
        echo "Checking database container status..."
        docker-compose ps postgres
        ;;
    logs)
        check_docker
        check_docker_compose
        echo "Showing database container logs..."
        docker-compose logs -f postgres
        ;;
    *)
        show_usage
        ;;
esac

# Check if the database is ready
if [ "$1" = "start" ] || [ "$1" = "restart" ]; then
    if docker-compose exec -T postgres pg_isready -U hms_user -d hms_db > /dev/null 2>&1; then
        echo "Database is ready!"
    else
        echo "Warning: Database might not be ready yet. Please check the logs."
    fi
fi 