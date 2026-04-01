# Retail Rewards API

## Overview
A Spring Boot RESTful Web API that calculates reward points for retail customers based on their transaction history. Built as a demonstration of clean architecture, modern Java features, and robust testing.

## Implementation Details
- **Architecture**: Standard layered architecture (Controller, Service, Model, Exception).
- **Dynamic Calculation**: Months are resolved dynamically using `LocalDate.getMonth().name()` to prevent hardcoding.
- **Immutability**: Utilizes Java 17 `record` types for DTOs and models for thread-safety and reduced boilerplate.
- **Exception Handling**: Features a `@ControllerAdvice` global exception handler for graceful negative path resolution (e.g., 404 Customer Not Found).
- **Testing**: Comprehensive JUnit 5 unit tests for core math logic and Spring Boot MockMvc integration tests for HTTP endpoints.

## Reward Logic
- 2 points for every dollar spent over $100 in each transaction.
- 1 point for every dollar spent between $50 and $100 in each transaction.
- Example: A $120 purchase = (2 * $20) + (1 * $50) = 90 points.

## Running the Application
1. Clone the repository.
2. Run `mvn clean install` to execute all unit and integration tests.
3. Run `mvn spring-boot:run` to start the embedded Tomcat server.
4. Access endpoints at `http://localhost:8080/api/rewards`.