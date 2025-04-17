# Housing Management System (HMS) - Backend

This project is the backend service for the Housing Management System, built with Java Spring Boot and PostgreSQL.

---

## 🚀 Getting Started with Docker

### 📦 Prerequisites

- Docker
- Docker Compose

---

## ⚙️ Setup Instructions

### 1. Clone the repository

```bash
git clone https://your-repo-url.git
cd your-project-directory
```

### 2. Build and start services

```bash
./mvnw clean package -DskipTests  
docker-compose up --build
```
if docker-compose up --build doesnot work type:

```bash
docker compose up --build
```

> ✅ Make sure your JAR file is located at `target/hms-0.0.1-SNAPSHOT.jar`.

---

## 🧠 Project Structure

- `backend` — Java Spring Boot application
- `postgres` — PostgreSQL 16 container
- `docker-compose.yml` — orchestrates backend and database
- `.env` or environment variables (used for DB credentials)

---

## 🔁 Database Info

- **Database name**: `hms_db_v2`
- **Username**: `hms_user`
- **Password**: `hms_password`
- **Port**: `5432`

If you need to restore the DB from a dump:

```bash
docker exec -it hms_postgres psql -U hms_user -d hms_db_v2 -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
docker exec -it hms_postgres psql -U hms_user -d hms_db_v2 -f /tmp/hms_dump0000011111010101010100010.sql
```

---

## 🌐 API Access

- **Base URL for frontend**: `http://localhost:8080`
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 🔒 CORS Configuration

The backend allows requests from the following origin:

```
http://localhost:3000
```

If your frontend is running on a different port or domain, update it in:

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

## 🧪 Health Check

The database container uses:

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U hms_user -d hms_db_v2"]
```

---

## 🕸️ Frontend Notes

To successfully connect your frontend:

- Use `http://localhost:8080` as the backend base URL
- Ensure the backend is fully up before sending requests
- Axios/fetch requests should include credentials if needed (`withCredentials: true`)

---

Happy coding!
