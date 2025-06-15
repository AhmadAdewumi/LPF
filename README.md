# ProductFinder Backend Application

## Overview

ProductFinder is a robust backend application designed to connect users with physical stores nearby that stock specific products. It provides comprehensive tools for store owners to manage their inventory and product images, while offering users a powerful, location-aware search experience leveraging advanced full-text and geospatial capabilities. Built with modern Java and Spring Boot, it prioritizes clean architecture, security, and scalability.

## Features

* **User Authentication & Authorization:**
    * Secure user registration and login.
    * JWT (JSON Web Token) based authentication for API security.
* **Store Management:**
    * Authenticated store owners can create, update, and manage their store profiles.
    * Stores are geospatially located using latitude and longitude for precise location-based services.
* **Product & Inventory Management:**
    * Store owners can upload product details and manage inventory levels for each product within their store.
    * Integrated image management for products via Cloudinary.
* **Advanced Product Search:**
    * Users can perform a combined full-text and geospatial search to find stores.
    * **Full-Text Search (FTS):** Utilizes PostgreSQL's `tsvector`and for efficient searching across store names, description.
    * **Geospatial Filtering:** Employs PostGIS functions like `ST_DWithin` to filter stores within a specified radius from the user's location.
    * **Proximity Sorting:** Results are sorted by relevance (text match) and then by proximity using `ST_Distance`.
* **Clean Architecture Adherence:**
    * Strict separation of concerns across layers (Controller, Service, Repository).
    * Application of the Single Responsibility Principle (SRP) within the service layer for enhanced maintainability and testability.
* **Global Exception Handling:** Centralized error management for consistent API responses.
* **Comprehensive API Documentation:** All endpoints are documented using OpenAPI (Swagger UI) for easy exploration and testing.
* **Robust Logging:** Detailed logging implemented across key components for improved observability and debugging.

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3
* **Database:** PostgreSQL
* **Geospatial Extension:** PostGIS
* **Build Tool:** Apache Maven
* **Cloud Storage:** Cloudinary (for image management)
* **Security:** JSON Web Tokens (JWT)
* **Utility:** Lombok
* **API Documentation:** Springdoc OpenAPI (Swagger UI)

## Architecture Breakdown (Clean Architecture Principles)

The application follows a clean architecture approach to ensure modularity, testability, and separation of concerns:

* **Controllers (`src/main/java/.../controller`)**:
    * **Purpose:** The entry point for incoming HTTP requests.
    * **Responsibility:** Handles request mapping, input validation (basic), and delegates business logic execution entirely to the Service layer. Keeps logic thin.
    * **Interactions:** Receives DTOs from clients and returns DTOs as responses.

* **Services (`src/main/java/.../service`)**:
    * **Purpose:** Encapsulates the core business logic of the application.
    * **Responsibility:** Orchestrates operations, applies business rules, interacts with repositories, and transforms entities to/from DTOs. Adheres to the Single Responsibility Principle (SRP) by breaking down complex operations into focused service components (e.g., `AuthService`, `StoreQueryService`, `StoreManagementService`, `ImageService`).
    * **Interactions:** Receives DTOs from controllers, interacts with repositories, and returns DTOs or domain objects.

* **Repositories (`src/main/java/.../repository`)**:
    * **Purpose:** Provides an abstraction layer for data persistence.
    * **Responsibility:** Handles direct database interactions. Utilizes Spring Data JPA for common CRUD operations and native PostgreSQL/PostGIS queries for advanced functionalities like full-text search and geospatial filtering.
    * **Interactions:** Communicates directly with the PostgreSQL database.

* **DTOs (`src/main/java/.../dto`)**:
    * **Purpose:** Data Transfer Objects.
    * **Responsibility:** Defines the structure of data exchanged between different layers (e.g., client-controller, service-controller, repository-service). Separates request, response, and internal entity representations.

* **Global Exception Handling (`src/main/java/.../exception`)**:
    * **Purpose:** Centralized management of application-wide exceptions.
    * **Responsibility:** Catches specific exceptions thrown by other layers and maps them to consistent HTTP status codes and error responses, enhancing API predictability.

## API Documentation

The API documentation is generated using Springdoc OpenAPI and can be accessed locally:

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`

## Local Setup Instructions

Follow these steps to get the ProductFinder backend running on your local machine.

### Prerequisites

* **Java 21 SDK:** Ensure Java 21 is installed and configured.
    * Verify with: `java -version`
* **Apache Maven:** Version 3.x or higher.
    * Verify with: `mvn -v`
* **PostgreSQL Database:** Version 12 or higher recommended.
    * Ensure PostgreSQL server is running.
* **PostGIS Extension:** Must be enabled in your PostgreSQL database.
* **Git:** For cloning the repository.
* **Cloudinary Account:** A free tier account is sufficient for development.

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd productfinder-backend # Or whatever your project folder is named
```

### 2. Database Setup

1.  **Create a PostgreSQL Database:**
    ```sql
    CREATE DATABASE productfinder_db;
    ```
2.  **Enable PostGIS Extension:**
    Connect to your newly created database and enable the PostGIS extension:
    ```sql
    \c productfinder_db;
    CREATE EXTENSION postgis;
    ```
3.  **Update Database Configuration:**
    Open `src/main/resources/application.properties` and configure your database connection details:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/productfinder_db
    spring.datasource.username=your_db_username
    spring.datasource.password=your_db_password
    spring.jpa.hibernate.ddl-auto=create
    spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
    spring.jpa.show-sql=true
    ```

### 3. Cloudinary Configuration

1.  **Create a Cloudinary Account:** If you don't have one, register at [cloudinary.com](https://cloudinary.com/).
2.  **Get Credentials:** From your Cloudinary dashboard, obtain your `Cloud Name`, `API Key`, and `API Secret`.
3.  **Update Configuration:** Open `src/main/resources/application.properties` and add/update the Cloudinary properties:
    ```properties
    cloudinary.cloud_name=your_cloud_name
    cloudinary.api_key=your_api_key
    cloudinary.api_secret=your_api_secret
    ```

### 4. Run the Application

Navigate to the project root directory in your terminal and execute the Spring Boot application:

```bash
mvn spring-boot:run
```

The application should start on `http://localhost:8080` (or the port configured in `application.properties`).

## Example Postman Test Flows

These examples illustrate key API interactions. Ensure the backend application is running and you have a Postman (or similar API client) setup.

### 1. User Registration

* **Endpoint:** `POST http://localhost:8080/api/auth/register`
* **Headers:**
    * `Content-Type: application/json`
* **Request Body:**
    ```json
    {
      "username": "testuser",
      "email": "test@example.com",
      "password": "SecurePassword123"
    }
    ```
* **Expected Response (201 Created):**
    ```json
    {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com"
    }
    ```

### 2. User Login

* **Endpoint:** `POST http://localhost:8080/api/auth/login`
* **Headers:**
    * `Content-Type: application/json`
* **Request Body:**
    ```json
    {
      "email": "test@example.com",
      "password": "SecurePassword123"
    }
    ```
* **Expected Response (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1Ni...",
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "roles": ["USER"]
    }
    ```
    * **Note:** Copy the `token` from the response. You'll need it for authenticated requests.

### 3. Create a Store (Requires Authentication)

* **Endpoint:** `POST http://localhost:8080/api/stores`
* **Headers:**
    * `Content-Type: application/json`
    * `Authorization: Bearer <YOUR_JWT_TOKEN>` (Paste the token obtained from login)
* **Request Body:**
    ```json
    {
  "name": "Ahmad Ibraheem Stores",
  "username": "AhmadAdewumi",
  "address": {
    "street": "Muslim Hospital Road",
    "city": "Ede",
    "state": "Osun",
    "country": "Nigeria",
    "postalCode": "232101"
  },
  "description": "Your go-to store near the muslim Hospital for any pharmaceutical needs",
  "latitude": 7.74941,
  "longitude": 4.43901
 }
    ```
* **Expected Response (201 Created):**
    ```json
    {
    "message": "Store created successfully",
    "data": {
        "id": 1,
        "name": "Ahmad Ibraheem Stores",
        "addressDto": {
            "street": "Muslim Hospital Road",
            "city": "Ede",
            "state": "Osun",
            "country": "Nigeria",
            "postalCode": "232101"
        },
        "description": "Your go-to store near the muslim Hospital for any pharmaceutical needs",
        "latitude": 7.74941,
        "longitude": 4.43901,
        "isVerified": true,
        "isActive": true,
        "ownerId": 1,
        "createdAt": "2025-06-15T14:47:10.996181",
        "updatedAt": "2025-06-15T14:47:10.996277"
    }
}
    ```
    * **Note:** If you get a 403 Forbidden, ensure your authenticated user has the necessary roles (e.g., `STORE_OWNER`).

### 4. Search Nearby Stores with Full-Text Search

* **Endpoint:** `GET http://localhost:8080/api/stores/search?query=store&lat=34.05&lon=-118.25&distanceKm=10`
* **Headers:** (No authentication required for public search)
* **Query Parameters:**
    * `query`: The text query (e.g., "awesome", "electronics", "fashion")
    * `lat`: User's latitude (e.g., 34.05)
    * `lon`: User's longitude (e.g., -118.25)
    * `distanceKm`: Search radius in kilometers (e.g., 10 for 10km)
* **Expected Response (200 OK):**
    ```json
    [
      {
    "message": "Store created successfully",
    "data": {
        "id": 1,
        "name": "Ahmad Ibraheem Stores",
        "addressDto": {
            "street": "Muslim Hospital Road",
            "city": "Ede",
            "state": "Osun",
            "country": "Nigeria",
            "postalCode": "232101"
        },
        "description": "Your go-to store near the muslim Hospital for any pharmaceutical needs",
        "latitude": 7.5494,
        "longitude": 5.43901,
        "isVerified": true,
        "isActive": true,
        "ownerId": 1,
        "createdAt": "2025-06-15T14:47:10.996181",
        "updatedAt": "2025-06-15T14:47:10.996277"
    }
    }
     
    ]
    ```

