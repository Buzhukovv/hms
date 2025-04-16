# HMS Documentation

## Database Setup

### Dockerized PostgreSQL Database

The application uses a Docker container for the PostgreSQL database. To set up and run the database:

1. Make sure Docker is installed on your system
2. Navigate to the project root directory
3. Run the following command to start the database:

   ```bash
   docker-compose up -d
   ```

This will:

- Create a PostgreSQL 16 container named `hms_postgres`
- Create a database named `hms_db`
- Create a user `hms_user` with password `hms_password`
- Map the database port to 5432 on your host machine
- Set up proper permissions for the application to access the database

To verify the database is running:

```bash
docker ps
```

To check database logs:

```bash
docker logs hms_postgres
```

To stop the database:

```bash
docker-compose down
```

To stop the database and remove all data:

```bash
docker-compose down -v
```

### Database Configuration

The application is configured to connect to the database with the following settings:

- Host: localhost
- Port: 5432
- Database: hms_db
- Username: hms_user
- Password: hms_password

These settings are defined in `src/main/resources/application-production.yml`.

## Application Setup

1. Clone the repository
2. Navigate to the project directory
3. Build the project:

   ```bash
   mvn clean install
   ```

4. Run the application:

   ```bash
   mvn spring-boot:run
   ```

## Development

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (for database)
- PostgreSQL client (optional, for direct database access)

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -Pproduction
```

## Troubleshooting

### Database Connection Issues

If you encounter database connection issues:

1. Verify the Docker container is running:

   ```bash
   docker ps
   ```

2. Check database logs for errors:

   ```bash
   docker logs hms_postgres
   ```

3. Verify database user and permissions:

   ```bash
   docker exec -it hms_postgres psql -U postgres -c "\du"
   ```

4. Test database connection:

   ```bash
   docker exec -it hms_postgres psql -U hms_user -d hms_db -c "SELECT 1;"
   ```

### Common Issues

1. Port conflicts: If port 5432 is already in use, modify the port mapping in `docker-compose.yml`
2. Permission issues: Ensure the database user has proper permissions
3. Connection timeouts: Check if the Docker container is running and accessible
