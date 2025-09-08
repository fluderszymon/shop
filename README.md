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
- JUnit 5
- Mockito
- JaCoCo

**Additional Tools**
- Lombok
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
4. Run: `mvn spring-boot:run` - let hibernate create tables
5. Populate database with example data `database.populate.sql`

**Testing:**
```bash
mvn test                    # Run tests
mvn jacoco:report           # Coverage report
```

**Configuration:**
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/shop
spring.datasource.username=root
spring.datasource.password=admin

# Security
jwt.secret.key=your-secret-key      # should be stored safely
jwt.expiration.time=900000          # 15 minutes
rate.limit.requests=60              # requests per minute
```

## 📝 API Reference

**Authentication**
```
POST /users/register     # User registration
POST /users/login        # User login
```

**User Management (Admin only)**
```
GET    /users                    # List all users
GET    /users/{username}         # Get user by username
POST   /users                    # Create user
PUT    /users                    # Update user
DELETE /users/{userId}           # Delete user
```

**Product Management**
```
GET    /products                 # List all products (USER)
GET    /products/{productId}     # Get product by ID (USER)
POST   /products                 # Create product (ADMIN)
PUT    /products                 # Update product (ADMIN)
DELETE /products/{productId}     # Delete product (ADMIN)
```

**Shopping Cart (Admin)**
```
GET    /carts                    # List all carts
GET    /carts/{cartId}           # Get cart by ID
GET    /carts/{cartId}/items     # Get cart items
GET    /carts/items/{cartItemId} # Get cart item by ID
```

**Shopping Cart (User)**
```
GET    /carts/my-cart                    # Get my cart
GET    /carts/my-cart/items              # Get my cart items
GET    /carts/my-cart/items/{cartItemId} # Get my cart item by ID
POST   /carts/my-cart/items              # Add item to my cart
PUT    /carts/my-cart/items              # Update item in my cart
DELETE /carts/my-cart/items/{cartItemId} # Remove item from my cart
GET    /carts/my-cart/total              # Get my cart total
```

**Orders (Admin)**
```
GET    /orders                        # List all orders
GET    /orders/{orderId}              # Get order by ID
GET    /orders/order-items            # List all order items
GET    /orders/{orderId}/order-items  # Get order items by order ID
```

**Orders (User)**
```
POST   /orders/checkout                # Process checkout
GET    /orders/my-orders               # Get my orders
GET    /orders/my-orders/{orderId}     # Get my order items by order ID
GET    /orders/my-orders/order-items   # Get all my order items
```

**Invoices (User)**
```
GET    /invoices/{orderId}/pdf    # Generate invoice PDF
```