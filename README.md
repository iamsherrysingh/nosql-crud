# NoSQL CRUD

Spring Boot REST API for user data in MongoDB. Supports full CRUD operations.

## Tech Stack

- Java 25, Spring Boot 4.0.3
- Spring Data MongoDB, Spring Web, Validation, Lombok
- Spring Cache with Redis (JSON serialization)

## Project Structure

```
src/main/java/ca/biglabs/nosqlcrud/
├── NosqlCrudApplication.java
├── controller/MongoDbController.java
├── dto/User.java
└── repository/UserRepository.java
```

## System Design

### High-Level Architecture

This project is a layered Spring Boot REST API that exposes CRUD operations for user data stored in MongoDB, with a Redis cache in front of single-user lookups.

- **Client**: Any HTTP client (browser, Postman, curl) calls REST endpoints on the Spring Boot service.
- **API Layer (Controller)**: `MongoDbController` handles HTTP requests, validation, and delegates to the service layer.
- **Service Layer**: `UserService` implements business logic and applies caching semantics.
- **Persistence Layer (Repository)**: `UserRepository` is a `MongoRepository<User, String>` that abstracts MongoDB access via Spring Data.
- **Cache**: Redis stores JSON-serialized `User` objects for `/user/{id}` lookups.
- **Database**: MongoDB stores user documents in the `users` collection of the `admin` database.

The application uses **Spring Boot auto-configuration**, **Spring Data MongoDB**, and **Spring Cache with Redis**. Auditing is enabled via `@EnableMongoAuditing` in `NosqlCrudApplication`, and caching via `@EnableCaching`.

### Components
 
- **Entry Point**
  - `NosqlCrudApplication` bootstraps Spring Boot and enables Mongo auditing and caching.
- **Controller**
  - `MongoDbController` exposes REST endpoints:
    - `GET /users` – fetch all users
    - `POST /user` – create a new user
    - `GET /user/{id}` – fetch a user by id (cached)
    - `PUT /user/{id}` – update an existing user by id (keeps cache in sync)
    - `DELETE /user/{id}` – delete a user by id (evicts from cache)
- **Service**
  - `UserService` coordinates with `UserRepository` and applies caching annotations (`@Cacheable`, `@CachePut`, `@CacheEvict`) on user operations.
- **Data Transfer Object / Document**
  - `User` represents a user document stored in MongoDB and is used as the request/response body.
  - Validation annotations ensure required fields and basic constraints (e.g., email format, non-negative age).
- **Repository**
  - `UserRepository` extends `MongoRepository<User, String>` and inherits standard CRUD methods (`findAll`, `save`, `findById`, `deleteById`, etc.).

### Data Flow

1. **Create**
   - Client sends `POST /user` with a JSON payload.
   - `MongoDbController.createUser` validates the request body and calls `userRepository.save(user)`.
   - MongoDB generates an ObjectId (`id`) and persists the document in the `users` collection.
2. **Read (list)**
   - Client sends `GET /users`.
   - Controller calls `userService.getAllUsers()`, which delegates to `userRepository.findAll()`, returning a list of `User` documents.
3. **Read (single, cached)**
   - Client sends `GET /user/{id}`.
   - Controller calls `userService.getUserById(id)`.
   - On a cache miss, `UserService` loads from MongoDB via `userRepository.findById(id)` and stores the JSON-serialized `User` in Redis; on subsequent calls the value is served from Redis.
4. **Update**
   - Client sends `PUT /user/{id}` with updated JSON.
   - Controller looks up the existing user via `userService.getUserById(id)`, preserves `createdAt`, then calls `userService.updateUser(id, user)` which saves to MongoDB and updates the cached entry.
5. **Delete**
   - Client sends `DELETE /user/{id}`.
   - Controller verifies existence via `userService.getUserById(id)` and then calls `userService.deleteUser(id)`, which deletes from MongoDB and evicts the cached entry.

### System Design Diagrams

#### Component Diagram

```mermaid
flowchart LR
    Client[HTTP Client\n(Postman, curl, Frontend)] -->|REST calls| API[Spring Boot App\nnosql-crud]

    subgraph SpringBoot[Spring Boot Application]
        direction TB
        Ctrl[MongoDbController\n(REST Controller)]
        Repo[UserRepository\n(Spring Data MongoDB)]
        User[User\n(Document / DTO)]
    end

    API --> Ctrl
    Ctrl --> Repo
    Repo -->|CRUD operations| MongoDB[(MongoDB\nadmin.users)]
    Repo --> User
```

#### CRUD Request Flow (Sequence)

```mermaid
sequenceDiagram
    participant C as Client
    participant Ctrl as MongoDbController
    participant Repo as UserRepository
    participant DB as MongoDB (users collection)

    C->>Ctrl: POST /user (User JSON)
    Ctrl->>Repo: save(user)
    Repo->>DB: insert document
    DB-->>Repo: inserted document (with id, createdAt)
    Repo-->>Ctrl: saved User
    Ctrl-->>C: 201 Created (User JSON)
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/users` | List all users |
| `GET` | `/user/{id}` | Get user by id |
| `POST` | `/user` | Create user |
| `PUT` | `/user/{id}` | Update user by id |
| `DELETE` | `/user/{id}` | Delete user by id |

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
app.docker.host=10.10.10.224

spring.application.name=nosql-crud
spring.mongodb.uri=mongodb://admin:adminpass@${app.docker.host}:27017/admin
spring.jackson.time-zone=America/Toronto

# Redis cache configuration
spring.data.redis.host=${app.docker.host}
spring.data.redis.port=6379
spring.data.redis.password=redispass
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
curl -X PUT http://localhost:8080/user/{id} -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith","email":"jane@example.com","jobType":"Manager","age":35}'

# Delete user
curl -X DELETE http://localhost:8080/user/{id}
```
