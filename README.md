# Ecommerce Order Processing System

## Overview

This project is a simple event-driven order processing system for an ecommerce platform, implemented in Java. It demonstrates the use of the Observer pattern, event sourcing, and modular design to handle order lifecycle events such as creation, payment, shipping, and cancellation.

## Features

- Event-driven processing of order lifecycle events
- Observer pattern for logging and alerting
- JSON-based event ingestion
- Extensible event and observer architecture

## Technologies Used

- Java 11
- Maven (build tool)
- Jackson (JSON parsing)
- JUnit (testing)

## Requirements

- Java 11 or higher
- Maven 3.x

## Project Structure

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── org.example
│   │   │       ├── domain         # Order, OrderItem, OrderStatus
│   │   │       ├── events         # Event classes (OrderCreated, PaymentReceived, etc.)
│   │   │       ├── observers      # Observer interfaces and implementations
│   │   │       ├── processing     # Event ingestion and processing
│   │   │       └── OrderProcessingSystem.java
│   │   └── resources
│   │       └── events.json        # Sample event data
│   └── test
│       └── java                   # (for unit tests)
├── pom.xml                        # Maven build file
```

## High-Level Design

- **Event Sourcing:** All order changes are triggered by events (OrderCreated, PaymentReceived, etc.)
- **Observer Pattern:** Observers (Logger, Alert) are notified on order status changes and event processing
- **Event Ingestion:** Events are read from a JSON file and parsed into Java objects
- **Order Management:** Orders are created and updated based on incoming events

<img width="3840" height="1714" alt="Untitled diagram _ Mermaid Chart-2025-09-04-114314" src="https://github.com/user-attachments/assets/bb3dccd0-b573-4d4f-a759-0300deb36877" />

## Low-Level Design

- **Domain Layer:**
  - `Order`, `OrderItem`, `OrderStatus` represent the core business entities
- **Events Layer:**
  - Abstract `Event` class and concrete subclasses for each event type
- **Observers Layer:**
  - `OrderObserver` interface, with `LoggerObserver` and `AlertObserver` implementations
- **Processing Layer:**
  - `EventIngestionService` reads and parses events from JSON
  - `EventProcessor` updates orders and notifies observers
- **Main System:**
  - `OrderProcessingSystem` initializes everything and runs the event processing loop

<img width="3840" height="2491" alt="Untitled diagram _ Mermaid Chart-2025-09-04-114332" src="https://github.com/user-attachments/assets/214a8351-db73-4e0d-923d-42bf435f4d9e" />

## How to Run

1. **Clone the repository**
2. **Build the project:**
   ```sh
   mvn clean package
   ```
3. **Run the application:**
   ```sh
   mvn exec:java -Dexec.mainClass="org.example.OrderProcessingSystem"
   ```
   Or, if you want to run the shaded JAR:
   ```sh
   java -jar target/order-processing-system-1.0.0.jar
   ```
4. **View Output:**
   - The system will process events from `src/main/resources/events.json` and print logs and alerts to the console.
   - Final order states will be displayed at the end.

## How to Test

You can run the automated tests to verify the functionality of the order processing system. The project uses JUnit for unit testing and Maven as the build tool.

1. **Run all tests:**

   ```sh
   mvn test
   ```

   This will compile the code (if needed) and execute all tests located in `src/test/java`.

2. **View test results:**

   - Test results and reports are generated in the `target/surefire-reports/` directory.
   - You can open the `.txt` or `.xml` files in that directory to see detailed test output and results for each test class.

3. **Typical test classes:**
   - `DomainModelTest` (domain logic)
   - `EventsTest` (event classes)
   - `ObserversTest` (observer pattern)
   - `EventIngestionServiceTest` (event ingestion)
   - `EventProcessorTest` (event processing)

If you add new features or modify the code, it is recommended to run the tests to ensure everything works as expected.

## Customizing Events

- To test with different events, edit `src/main/resources/events.json`.
- Each line should be a valid JSON object representing an event (see provided examples).

## Example Event (OrderCreated)

```json
{
  "eventId": "e1",
  "timestamp": "2025-07-29T10:00:00Z",
  "eventType": "OrderCreated",
  "orderId": "ORD001",
  "customerId": "CUST001",
  "items": [{ "itemId": "P001", "qty": 2 }],
  "totalAmount": 100.0
}
```

## Assignment Notes

- Demonstrates event-driven design and observer pattern
- Easily extensible for new event types or observers
- Suitable for learning or as a starting point for more complex systems
