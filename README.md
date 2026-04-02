# Retail Rewards API

A robust, enterprise-grade RESTful microservice built with Spring Boot that calculates financial reward amounts for
customers based on their transaction history.

This project follows Clean Architecture principles, utilizing Spring Data JPA for persistent state, `BigDecimal` for
precise financial calculations, and comprehensive test coverage splitting unit and integration layers.

---

## Table of Contents

* [ Business Logic](#-business-logic)
* [ Technology Stack](#️-technology-stack)
* [ Key Enterprise Design Decisions](#️-key-enterprise-design-decisions)
* [ How to Run Locally](#-how-to-run-locally)
* [️ Accessing the Database](#️-accessing-the-database)
* [ API Documentation (Inputs & Outputs)](#-api-documentation-inputs--outputs)
* [ Testing Strategy](#-testing-strategy)

---

## Business Logic

The system processes transaction data and awards financial reward amounts based on a configurable, tiered calculation
model:

* **Tier 1 ($0 - $50):** No rewards earned.
* **Tier 2 ($50 - $100):** 1x reward amount for every dollar spent.
* **Tier 3 (Over $100):** 2x reward amount for every dollar spent.
* *Note: Fractional amounts (cents) are truncated (rounded down to the nearest whole dollar) prior to calculation to
  adhere to standard retail rounding rules.*

**Example Calculation:** A $120 purchase yields a **$90.00** reward amount.

* $20 (amount over $100) x 2 = 40.00
* $50 (amount between $50 and $100) x 1 = 50.00
* **Total = 90.00**

---

## Technology Stack

* **Java 17:** Leveraging modern features like `record` for immutable Data Transfer Objects.
* **Spring Boot 3.2.x:** Core framework for rapid REST API creation and dependency injection.
* **Spring Data JPA & Hibernate:** For ORM and database interactions.
* **H2 In-Memory Database:** For seamless local development and integration testing.
* **Maven:** Build automation and dependency management.
* **JUnit 5, Mockito & MockMvc:** For comprehensive unit and integration testing.

---

## Key Enterprise Design Decisions

To ensure the application is scalable, maintainable, and production-ready, the following design choices were
implemented:

1. **Financial Data Precision:** The concept of "integer points" was replaced with `BigDecimal` reward amounts. `Double`
   is strictly avoided to prevent floating-point arithmetic errors inherent in Java, ensuring 100% accuracy for
   financial reporting.
2. **Clean Architecture (Entity vs. Domain):** The application strictly separates Database Entities (
   `TransactionEntity`) from Domain Models/DTOs (`Transaction`, `RewardSummary` records). This prevents database
   framework logic (Hibernate) from leaking into the presentation layer.
3. **Dynamic Configuration:** Hardcoded magic numbers were removed. Reward tiers and multipliers are injected via
   `@Value` from `application.properties`, allowing business rules to be updated without recompiling Java code.
4. **Timezone-Safe Grouping:** Hardcoded month strings (e.g., "JANUARY") were avoided. The API uses the Java Time API's
   `YearMonth` (e.g., "2026-04") to dynamically group transactions, preventing data collision across different years.
5. **Global Exception Handling:** Anticipated negative scenarios (like requesting a non-existent customer) throw a
   `CustomerNotFoundException`. An `@ControllerAdvice` intercepts this and returns a clean, standardized JSON
   `404 Not Found` response instead of a stack trace.

---

## How to Run Locally

### Prerequisites

* Java 17+ installed
* Maven installed

### Build and Run

1. Clone this repository to your local machine.
2. Open a terminal in the project root directory.
3. Clean and build the project (this will also run the test suite):
   ```bash
   mvn clean install
   4. Start the application:
      mvn spring-boot:run
   The application will start on http://localhost:8080. Upon startup, the database is automatically seeded with test data via the schema.sql and data.sql scripts located in src/main/resources.

Accessing the Database
You can view the raw transaction data currently loaded into the system using the H2 Web Console.

Navigate to: http://localhost:8080/h2-console

Enter the following credentials:

JDBC URL: jdbc:h2:mem:rewarddb

User Name: sa

Password: password

Click Connect and run SELECT * FROM TRANSACTIONS; to view the seeded data.

API Documentation (Inputs & Outputs)
All requests assume the application is running locally on port 8080.

1. Get Rewards for a Specific Customer (Happy Path)
   Retrieves the aggregated reward amounts (monthly breakdown and total) for a single customer.

Input Method: GET

Input URL: http://localhost:8080/api/rewards/101

cURL Command:

Bash
curl -X GET http://localhost:8080/api/rewards/101 -H "Accept: application/json"
Output Response (200 OK):

JSON
{
"customerId": 101,
"monthlyRewardAmount": {
"2026-03": 150.00,
"2026-04": 120.00
},
"totalRewardAmount": 270.00
}

2. Get Rewards for a Specific Customer (Not Found Error)
   Tests the global exception handler when requesting data for a customer that does not exist.

Input Method: GET

Input URL: http://localhost:8080/api/rewards/999

cURL Command:

Bash
curl -X GET http://localhost:8080/api/rewards/999 -H "Accept: application/json"
Output Response (404 Not Found):

JSON
{
"message": "No transactions found for customer ID: 999",
"error": "Not Found",
"status": 404
}

3. Get Rewards for All Customers
   Retrieves the reward aggregations for all customers currently in the database.

Input Method: GET

Input URL: http://localhost:8080/api/rewards

cURL Command:

Bash
curl -X GET http://localhost:8080/api/rewards -H "Accept: application/json"
Output Response (200 OK):

JSON
[
{
"customerId": 101,
"monthlyRewardAmount": {
"2026-03": 150.00,
"2026-04": 120.00
},
"totalRewardAmount": 270.00
},
{
"customerId": 102,
"monthlyRewardAmount": {},
"totalRewardAmount": 0.00
},
{
"customerId": 103,
"monthlyRewardAmount": {
"2026-04": 50.00
},
"totalRewardAmount": 50.00
}
]
Testing Strategy
The project contains a comprehensive test suite utilizing JUnit 5 and Mockito, strictly separating web-layer testing
from data-layer testing to ensure fast feedback loops and robust integration validation.

Controller Tests (@WebMvcTest): Unit tests that verify API routing, JSON serialization, and HTTP status codes using
MockMvc while mocking the underlying Service layer.

Service Unit Tests (@ExtendWith(MockitoExtension.class)): Blazing-fast tests that validate the core BigDecimal
mathematics and grouping logic without booting the Spring Context.

Integration Tests (@SpringBootTest): End-to-end tests that boot the full application, execute the SQL initialization
scripts against the H2 database, and verify the entire data flow from HTTP request to database retrieval.

To execute the test suite:

Bash
mvn test