# NoSQL CRUD

Spring Boot REST API for user data in MongoDB. Supports full CRUD operations.

## Tech Stack

- Java 25, Spring Boot 4.0.3
- Spring Data MongoDB, Spring Web, Validation, Lombok

## Project Structure

```
src/main/java/ca/biglabs/nosqlcrud/
├── NosqlCrudApplication.java
├── controller/MongoDbController.java
├── dto/User.java
└── repository/UserRepository.java
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/users` | List all users |
| `POST` | `/user` | Create user |
| `PUT` | `/users/{id}` | Update user by id |
| `DELETE` | `/users/{id}` | Delete user by id |

## User Model

Maps to `users` collection in `admin` database:

| Field | Type | Notes |
|-------|------|-------|
| `id` | String | Auto-generated (MongoDB ObjectId) |
| `createdAt` | Date | Auto-populated on create |
| `firstName` | String | Required |
| `lastName` | String | Required |
| `email` | String | Required, valid format |
| `jobType` | String | Optional |
| `age` | Integer | Optional, min 0 |

## Configuration

`src/main/resources/application.properties`:

```properties
spring.application.name=nosql-crud
spring.mongodb.uri=mongodb://admin:adminpass@10.10.10.224:27017/admin
spring.jackson.time-zone=America/Toronto
```

## MongoDB setup (Docker)

MongoDB can be run locally with Docker using the compose file in the database dump folder:

**[database dump](src/main/resources/database%20dump/)**

1. From the project root, start MongoDB:
   ```bash
   docker-compose -f "src/main/resources/database dump/docker-compose.yml" up -d
   ```
2. The `admin` database is created automatically. The **`users` collection must be created manually** in the `admin` database (e.g. via MongoDB Compass or mongosh).
3. Optional: import seed data from `admin.users.json` in the same folder if you use a tool that supports JSON import.

For local Docker, use `localhost` in the connection URI (e.g. `mongodb://admin:adminpass@localhost:27017/admin`) in `application.properties` if needed.

## Run

```bash
mvn clean install
mvn spring-boot:run
```

API: `http://localhost:8080`

## Example Requests

```bash
# List users
curl http://localhost:8080/users

# Create user
curl -X POST http://localhost:8080/user -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane@example.com","jobType":"Engineer","age":30}'

# Update user
curl -X PUT http://localhost:8080/users/{id} -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith","email":"jane@example.com","jobType":"Manager","age":35}'

# Delete user
curl -X DELETE http://localhost:8080/users/{id}
```
