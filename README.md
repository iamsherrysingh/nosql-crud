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
