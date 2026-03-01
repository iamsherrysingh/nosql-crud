# NoSQL CRUD

A Spring Boot REST API for managing user data stored in MongoDB. This project demonstrates basic CRUD-style operations with Spring Data MongoDB.

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.3**
- **Spring Data MongoDB**
- **Spring Web (MVC)**
- **Lombok**
- **MongoDB**

## Project Structure

```
src/main/java/ca/biglabs/nosqlcrud/
├── NosqlCrudApplication.java    # Application entry point
├── controller/
│   └── MongoDbController.java   # REST endpoints for users
├── dto/
│   └── User.java                # User entity/document model
└── repository/
    └── UserRepository.java      # MongoDB data access layer
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/users` | Returns all users from the MongoDB `users` collection |

## User Model

The `User` entity maps to the `users` collection in MongoDB with the following fields:

| Field | Type |
|-------|------|
| `id` | String (MongoDB ObjectId) |
| `firstName` | String |
| `lastName` | String |
| `email` | String |
| `jobType` | String |
| `age` | Integer |

## Configuration

MongoDB connection is configured in `src/main/resources/application.properties`:

```properties
spring.application.name=nosql-crud
spring.mongodb.uri=mongodb://admin:adminpass@10.10.10.224:27017/admin
```

**Note:** Spring Data MongoDB uses `spring.data.mongodb.uri` by default. If connection issues occur, add:

```properties
spring.data.mongodb.uri=${spring.mongodb.uri}
```

### Database

- **Database:** `admin` (from URI path)
- **Collection:** `users`

## Prerequisites

- Java 25+
- Maven 3.6+
- MongoDB instance (local or remote)

## Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080` (default Spring Boot port).

## Example Request

```bash
# Get all users
curl http://localhost:8080/users
```

## Development

- **DevTools** is included for automatic restart during development
- Tests use `spring-boot-starter-mongodb-test` and `spring-boot-starter-webmvc-test`
