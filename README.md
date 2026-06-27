# Let's Play - Project Checklist

## 🎯 Project Goal

Build a secure RESTful API using **Spring Boot** and **MongoDB**.

The application manages:

- Users
- Products

Each user can own multiple products.

The API must support:

- CRUD operations
- JWT Authentication
- Spring Security
- Role-based authorization
- Validation
- Global exception handling

---

# Technologies

- Spring Boot
- Spring Web
- Spring Data MongoDB
- Spring Security
- JWT
- BCrypt
- Maven
- MongoDB

---

# Project Structure

```
src/main/java
│
├── controller
│
├── service
│
├── repository
│
├── model
│
├── dto
│
├── security
│
├── exception
│
├── config
│
└── LetsPlayApplication
```

---

# Database

## User

- id
- name
- email
- password
- role

Role:

- USER
- ADMIN

---

## Product

- id
- name
- description
- price
- userId

Relationship

```
User (1)
      |
      | owns
      |
Product (N)
```

---

# Authentication

## Register

Create a new user.

Password must be hashed using BCrypt.

---

## Login

Authenticate using:

- email
- password

Return

```
JWT Token
```

---

## JWT

Every protected request must contain

```
Authorization: Bearer <token>
```

Spring Security validates the token before accessing controllers.

---

# User Roles

## USER

Can

- Login
- Create products
- View products
- Update own products
- Delete own products

Cannot

- Manage users
- Edit other users' products

---

## ADMIN

Can

- Manage every user
- Manage every product
- View everything
- Delete everything
- Update everything

---

# REST Endpoints

## Authentication

- [ ] POST /auth/register
- [ ] POST /auth/login

---

## Users

- [ ] GET /users
- [ ] GET /users/{id}
- [ ] PUT /users/{id}
- [ ] DELETE /users/{id}

Restriction

Only ADMIN.

---

## Products

- [ ] GET /products
- [ ] GET /products/{id}
- [ ] POST /products
- [ ] PUT /products/{id}
- [ ] DELETE /products/{id}

Restrictions

GET

- Public

POST

- Authenticated users

PUT

- Product owner
- ADMIN

DELETE

- Product owner
- ADMIN

---

# CRUD Tasks

## User

- [ ] Create
- [ ] Read
- [ ] Update
- [ ] Delete

---

## Product

- [ ] Create
- [ ] Read
- [ ] Update
- [ ] Delete

---

# Validation

Validate every request.

Examples

- [ ] Name not empty
- [ ] Email format
- [ ] Unique email
- [ ] Password not empty
- [ ] Price > 0
- [ ] Product name not empty

Return

```
400 Bad Request
```

when validation fails.

---

# Security

- [ ] BCrypt password hashing
- [ ] JWT Authentication
- [ ] Spring Security
- [ ] Role authorization
- [ ] Protect endpoints
- [ ] Do not expose passwords
- [ ] Validate inputs
- [ ] Prevent unauthorized access

---

# Error Handling

Create a Global Exception Handler.

Handle

- [ ] Resource Not Found
- [ ] Bad Request
- [ ] Unauthorized
- [ ] Forbidden
- [ ] Conflict
- [ ] Validation Errors
- [ ] Generic Exception

Return proper JSON responses.

Example

```json
{
    "status":404,
    "message":"Product not found"
}
```

---

# DTOs

Create DTOs to avoid exposing entities directly.

Suggested DTOs

- [ ] RegisterRequest
- [ ] LoginRequest
- [ ] LoginResponse
- [ ] UserResponse
- [ ] ProductRequest
- [ ] ProductResponse

---

# Repository

Create

- [ ] UserRepository
- [ ] ProductRepository

---

# Services

Create

- [ ] UserService
- [ ] ProductService
- [ ] AuthService
- [ ] JwtService

---

# Controllers

Create

- [ ] AuthController
- [ ] UserController
- [ ] ProductController

---

# Security Classes

Create

- [ ] SecurityConfig
- [ ] JwtAuthenticationFilter
- [ ] JwtService
- [ ] UserDetailsService implementation

---

# Testing Checklist

## Authentication

- [ ] Register works
- [ ] Login works
- [ ] JWT generated
- [ ] Invalid login returns 401

---

## Users

- [ ] Admin can view users
- [ ] User cannot access /users
- [ ] Delete user works
- [ ] Update user works

---

## Products

- [ ] Anyone can GET products
- [ ] Logged user can create product
- [ ] Owner can edit own product
- [ ] Owner can delete own product
- [ ] Admin can edit every product
- [ ] Admin can delete every product
- [ ] Other users cannot modify someone else's product

---

# HTTP Status Codes

- 200 OK
- 201 Created
- 204 No Content
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found
- 409 Conflict

---

# Bonus

- [ ] CORS configuration
- [ ] Rate limiting
- [ ] HTTPS
- [ ] Swagger / OpenAPI
- [ ] Unit Tests
- [ ] Integration Tests

---

# Final Checklist

- [ ] Spring Boot project created
- [ ] MongoDB connected
- [ ] User entity created
- [ ] Product entity created
- [ ] CRUD Users
- [ ] CRUD Products
- [ ] Register
- [ ] Login
- [ ] JWT
- [ ] Spring Security
- [ ] BCrypt
- [ ] Authorization
- [ ] Validation
- [ ] Exception Handling
- [ ] Hide passwords
- [ ] Proper HTTP Status Codes
- [ ] Clean architecture
- [ ] Documentation complete
