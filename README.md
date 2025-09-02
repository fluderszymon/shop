# Shop Application

A modern e-commerce platform built with Spring Boot featuring JWT authentication, basic inventory management, shopping cart, and automated invoice generation. Includes transactional checkout with stock validation and advanced security features.

## 🚀 Tech Stack

**Backend**
- Spring Boot 3.5.0
- Java 17
- Maven

**Database**
- MySQL (production)
- H2 (testing)
- Spring Data JPA + Hibernate

**Security**
- Spring Security
- JWT authentication
- BCrypt password hashing
- Rate limiting (Bucket4j)

**Testing**
- Lombok
- JUnit 5
- Mockito
- JaCoCo

**Additional Tools**
- MapStruct (object mapping)
- iText (PDF generation)

## 🏗️ Architecture

Clean layered architecture with separation of concerns:
```
Controllers  → REST API endpoints
Services     → Business logic  
Repositories → Data access
Entities     → Data model
```

## 🔐 Security

- JWT authentication (15-minute tokens)
- Role-based access control (ADMIN → USER) - *in development*
- BCrypt password hashing (strength 12)
- Rate limiting (60 requests/minute)
- Input validation - *in development*

## 🛒 Features

**Core Functionality**
- User registration/authentication with balance management
- Product catalog
- Persistent shopping cart
- Transactional checkout with stock/balance validation
- Automated PDF invoice generation

## 🚀 Quick Start

**Prerequisites:** Java 17+, MySQL 8.0+, Maven 3.6+

**Setup:**
1. Clone repository
2. Create database: `CREATE DATABASE shop;`
3. Update `application.properties` with database credentials
4. Run: `mvn spring-boot:run`

**Testing:**
```bash
mvn test                    # Run tests
mvn jacoco:report          # Coverage report
```

**Configuration:**
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/shop
spring.datasource.username=root
spring.datasource.password=admin

# Security
jwt.secret.key=your-secret-key
jwt.expiration.time=900000          # 15 minutes
rate.limit.requests=60              # requests per minute
```

## 📝 API Reference

**Authentication**
```
POST /users/register     # User registration
POST /users/login        # User login
```

**User Management**
```
GET    /users                    # List all users
GET    /users/{username}         # Get user by username
PUT    /users                    # Update user
DELETE /users/{userId}           # Delete user
```

**Product Management**
```
GET    /products                 # List all products
GET    /products/{productId}     # Get product by ID
POST   /products                 # Create product
PUT    /products                 # Update product
DELETE /products/{productId}     # Delete product
```

**Shopping Cart**
```
GET    /carts                    # List all carts
GET    /carts/{cartId}           # Get cart by ID
POST   /carts/{userId}           # Create cart for user
GET    /carts/{cartId}/items     # Get cart items
POST   /carts/{cartId}/items     # Add item to cart
DELETE /carts/{cartId}           # Delete cart
```

**Orders & Invoices**
```
GET    /orders                           # List all orders
GET    /orders/{orderId}                 # Get order by ID
POST   /orders/checkout/{userId}/{cartId} # Process checkout
GET    /orders/{orderId}/order-items     # Get order items
GET    /invoices/{orderId}/pdf           # Generate invoice PDF
```




