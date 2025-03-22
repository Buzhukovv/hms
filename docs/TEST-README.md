# HMS Test Scripts

This directory contains scripts for loading test data and testing the HMS (Housing Management System) APIs.

## Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL database running with credentials configured in application.properties

## Available Scripts

### 1. Load Test Data

The `load-test-data.sh` script builds the application and runs it with the `dev` profile, which triggers the `TestDataLoader` to populate the database with test data.

```bash
./load-test-data.sh
```

This will:

1. Build the application using Maven
2. Run the application with the `dev` profile
3. Load the following test data:
   - A student user
   - A dormitory room
   - A lease connecting the student to the room
   - A maintenance request for the leased room

### 2. Test API Endpoints

The `test-api.sh` script tests various API endpoints using curl commands.

```bash
./test-api.sh
```

This will:

1. Check if the server is running
2. Fetch and display all students
3. Fetch and display all dormitory rooms
4. Fetch and display all leases
5. Fetch and display all maintenance requests

### Customizing the Tests

To customize the test data being loaded:

1. Edit the `TestDataLoader.java` file in `src/main/java/housingManagment/hms/`
2. Modify the methods:
   - `createStudent()` to change student details
   - `createDormitoryRoom()` to change property details
   - `createLease()` to change lease details
   - `createMaintenanceRequest()` to change maintenance request details

To customize the API tests:

1. Edit the `test-api.sh` script
2. Add or modify the curl commands to test different endpoints

## Additional Information

- The Swagger UI is available at: <http://localhost:8080/swagger-ui/index.html>
- You can use this interface to explore and test all available endpoints

## Troubleshooting

If you encounter any issues:

1. Check that the database is properly configured and running
2. Ensure that the application starts successfully without errors
3. Make sure the required ports (8080 for the application) are available
4. Check the logs for detailed error messages
