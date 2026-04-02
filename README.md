# Retail Rewards API

A RESTful web service built with Spring Boot that calculates reward points for customers based on their transaction
history.

## 📋 Table of Contents

* [Business Logic](#business-logic)
* [Technology Stack](#technology-stack)
* [Key Design Decisions](#key-design-decisions)
* [How to Run Locally](#how-to-run-locally)
* [Testing](#testing)
* [API Documentation](#api-documentation)

## 🧮 Business Logic

The system processes transaction data and awards points based on a tiered calculation model:

* **Tier 1:** 1 point for every dollar spent between $50 and $100 in each transaction.
* **Tier 2:** 2 points for every dollar spent over $100 in each transaction.
* Fractional amounts (cents) are truncated (rounded down to the nearest whole dollar) prior to calculation.

**Example Calculation:** A $120 purchase yields 90 points.

* $20 (amount over $100) x 2 = 40 points
* $50 (amount between $50 and $100) x 1 = 50 points
* Total = 90 points

## 🛠 Technology Stack

* **Java 17:** Leveraging modern features like `record` for immutable Data Transfer Objects.
* **Spring Boot 3.x:** For rapid REST API creation and dependency injection.
* **Maven:** Build automation and dependency management.
* **JUnit 5 & MockMvc:** For comprehensive unit and integration testing.

## 🏗 Key Design Decisions

To ensure the application is scalable, maintainable, and robust, the following design choices were implemented:

1. **Dynamic Month Calculation:** Hardcoded month strings (e.g., "JANUARY") were strictly avoided. The application uses
   the Java 8 Time API (`LocalDate`) and the Streams API to dynamically group transactions by month. This allows the API
   to seamlessly scale across any rolling timeframe.
2. **Immutability & DTOs:** Domain models (`Transaction`, `RewardSummary`) are implemented using Java Records. This
   guarantees shallow immutability for data traveling between the service and presentation layers.
3. **Robust Exception Handling:** The application anticipates negative scenarios. Requesting a non-existent customer ID
   throws a `CustomerNotFoundException`, which is caught and returned as a clean `404 Not Found` response. Negative
   transaction amounts safely bypass the reward logic, preventing incorrect point deductions.
4. **Separation of Concerns:** The mathematical logic is strictly isolated within the `RewardService`, keeping the
   `RewardController` solely responsible for HTTP orchestration.

## 🚀 How to Run Locally

1. Clone this repository to your local machine.
2. Open a terminal in the project root directory.
3. Run the following Maven command to compile and start the application:
   ```bash
   mvn spring-boot:run