# Housing Management System (HMS) - Documentation

## 1. Project Overview

The Housing Management System (HMS) is a test project designed to manage users and properties, allowing lease agreements to link them. The system provides a structured way to handle different types of users and properties, supporting lease creation and management.

## 2. System Structure

The project consists of three main components:

### 2.1 Users

Users represent different roles in the housing system. The base class `BaseUser` defines common attributes, while specialized classes extend its functionality:

- **Student (**``**)** – Represents a student living in campus housing.
- **Teacher (**``**)** – Represents a faculty member.
- **Family Member (**``**)** – Represents a family member residing in housing.
- **DSS (**``**)** – Represents a dormitory staff member.
- **Maintenance (**``**)** – Represents maintenance personnel.
- **Housing Management (**``**)** – Represents administrative users managing housing.

### 2.2 Properties

Properties are different types of housing accommodations. The base class `BaseProperty` defines shared attributes, with specialized classes extending its functionality:

- **Dormitory Room (**``**)** – A student dormitory room.
- **Campus Apartment (**``**)** – Apartments within the campus.
- **Cottage (**``**)** – Standalone housing unit.
- **Off-Campus Apartment (**``**)** – Housing located outside the campus.

### 2.3 Lease Agreements

The `Lease.java` class links users to properties, defining the contractual relationship between a user and a property.

## 3. Installation & Setup

### 3.1 Prerequisites

- Java (JDK 17+ recommended)
- Maven (for dependency management)
- Docker (for database setup)
- PostgreSQL (if using a database)
- Spring Boot (already configured in the project)

### 3.2 Running the Project

1. Clone the repository or extract the provided ZIP file.
2. Navigate to the project root.
3. Run the following command to build and start the application:
   ```sh
   ./mvnw spring-boot:run
   ```
   *(For Windows, use **`mvnw.cmd spring-boot:run`**)*
4. The application should now be running on `http://localhost:8080`.

### 3.3 Setting up the Database with Docker

To run PostgreSQL locally using Docker, execute the following command:

```sh
docker run --name hms-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres -p 5432:5432 -d postgres
```

#### Database Configuration in `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
```

## 4. API Endpoints (Example)

The project likely includes REST controllers for managing users, properties, and leases. Sample endpoints might include:

- **User Management**:

    - `POST /users` – Create a new user
    - `GET /users/{id}` – Get user details

- **Property Management**:

    - `POST /properties` – Create a new property
    - `GET /properties/{id}` – Get property details

- **Lease Management**:

    - `POST /leases` – Create a new lease linking a user to a property
    - `GET /leases/{id}` – Get lease details

## 5. Future Enhancements

- Implement authentication and role-based access control.
- Add a frontend (React/Angular) for user interaction.
- Expand reporting and analytics capabilities.

This documentation provides a foundation for understanding the structure and functionality of the Housing Management System.

