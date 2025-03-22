# HMS Database Setup and Management

This guide explains how to set up the PostgreSQL database for the Housing Management System (HMS) and populate it with test data.

## Prerequisites

- PostgreSQL installed and running
- PostgreSQL credentials (default: username=postgres, password=postgres)
- Java 17 or higher
- Maven

## Database Setup Scripts

### 1. Setup Database (`setup-database.sh`)

This script performs the following operations:

1. Creates a dedicated `hms` database in PostgreSQL
2. Grants necessary privileges
3. Updates the application configuration to use the new database
4. Builds the application
5. Starts the application with the `dev` profile to populate test data

To run the script:

```bash
./setup-database.sh
```

### 2. Verify Database Population (`verify-database.sh`)

This script checks if the database has been properly populated with test data by:

1. Confirming database connectivity
2. Counting records in key tables
3. Displaying detailed information about the created entities (Student, DormitoryRoom, Lease, MaintenanceRequest)

To run the script:

```bash
./verify-database.sh
```

## Database Structure

The application uses a PostgreSQL database with the following key tables:

- `user_base` - Base table for all user types
- `user_students` - Stores student-specific data
- `property_base` - Base table for all property types
- `property_dormitory_rooms` - Stores dormitory room-specific data
- `leases` - Stores lease agreements between users and properties
- `maintenance_requests` - Stores maintenance requests for properties

## Test Data

The test data includes:

1. **Student**:
   - Name: John Doe
   - Email: <john.doe@university.edu>
   - Role: BACHELOR_DEGREE_3_Y
   - School: School of Engineering
   - Specialty: Computer Science

2. **Dormitory Room**:
   - Location: Block A
   - Type: TWO_BEDDED_ROOM
   - Status: VACANT
   - Max Occupants: 2
   - Rent: 500.0
   - Deposit: 500.0

3. **Lease**:
   - Active lease connecting the student to the dormitory room
   - 6-month term

4. **Maintenance Request**:
   - Title: "Broken Light Fixture"
   - Description: "The ceiling light in the room is flickering and needs to be replaced"

## Manual Database Operations

If you need to interact with the database directly:

```bash
# Connect to the database
psql -U postgres -h localhost -d hms

# Basic commands inside psql
\dt            # List all tables
\d table_name  # Describe a specific table
```

## Resetting the Database

The application is configured with `spring.jpa.hibernate.ddl-auto=create-drop`, which means the database schema will be recreated each time the application starts. To start fresh:

1. Stop the application
2. Run the setup script again:

   ```bash
   ./setup-database.sh
   ```

## Troubleshooting

- **Connection Issues**: Make sure PostgreSQL is running and accessible at localhost:5432
- **Authentication Errors**: Verify the credentials in application.yml
- **Table Not Found Errors**: Check if the database schema was properly created
