# Evently

Backend REST API built with **Java 21**, **Spring Boot 3.3**, and **MongoDB**, containerized with **Docker** and **Docker Compose**.

## Prerequisites

- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/)
- [Java 21+](https://adoptium.net/) *(local development only)*
- [Maven 3.9+](https://maven.apache.org/) *(local development only)*

## Run

```bash
docker-compose up --build
```

App: `http://localhost:8080`  
MongoDB: `localhost:27017`

## Local Development (without Docker)

Requires a running MongoDB instance.

```bash
mvn spring-boot:run
```

## Configuration

| Variable      | Default                             | Description            |
|---------------|-------------------------------------|------------------------|
| `MONGODB_URI` | `mongodb://localhost:27017/evently` | MongoDB connection URI |
| `SERVER_PORT` | `8080`                              | Application port       |

## Useful Commands

```bash
docker-compose up --build   # Build and start
docker-compose down         # Stop containers
docker-compose down -v      # Stop and remove data volume
mvn clean package           # Build JAR
mvn test                    # Run tests
```
