# Library Management System

A Spring Boot-based application for managing library resources, including book inventory and borrow records.

## Features

* **Book Management:** Add, update, view, and validate library books.
* **Borrowing System:** Track borrow records and statuses.
* **Custom Aspects:** Built-in AOP mechanisms for Caching (`@CacheResult`), Rate Limiting (`@RateLimit`), and Operation Retries (`@RetryOperation`).
* **Database Migrations:** Version-controlled database schema using Liquibase.
* **Web Interface:** Server-side rendered UI templates (HTML).
* **Containerization:** Ready-to-use Docker Compose setup for the application and database.

## Tech Stack

* **Language:** Java
* **Framework:** Spring Boot
* **Build Tool:** Maven
* **Database:** SQL (via `init.sql` and Liquibase changelogs)
* **Deployment:** Docker & Docker Compose

## Prerequisites

* Java 17 or higher
* Maven (or use the provided `mvnw` wrapper)
* Docker and Docker Compose

## Getting Started

### 1. Run with Docker (Recommended)

The easiest way to get the database and application running is via Docker Compose:

```bash
docker-compose up -d --build

```

This will initialize the database using `init.sql` and start the application.

### 2. Run Locally (Development)

Ensure your local database is running and configured in `src/main/resources/application.yml`. Then, build and start the application using the Maven wrapper:

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

```

## Project Structure

* `src/main/java/com/ascariaa/library/`
* `aspect/`: Custom annotations and AOP logic (Cache, RateLimit, Retry).
* `config/`: Application configuration (Security, WebMvc, Argument Resolvers).
* `controller/`: REST APIs and Web controllers.
* `domain/`: Entities, DTOs, Enums, Mappers, and custom Validation.
* `exception/`: Global exception handling.
* `repository/`: Data access layer.
* `service/`: Business logic.


* `src/main/resources/`
* `db/changelog/`: Liquibase migration scripts.
* `templates/`: HTML views for the web interface.



## API Testing

A `requests.http` file is included in the project root. You can use it directly in IntelliJ IDEA or VS Code (with the REST Client extension) to test the available API endpoints without needing an external tool like Postman.

## Running Tests

To execute the unit and integration tests:

```bash
./mvnw test

```
