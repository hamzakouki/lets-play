# Security & JWT Implementation Guide

## Overview
This document provides a comprehensive guide to the security and JWT authentication implementation in the Let's Play API. The system uses JWT (JSON Web Tokens) for stateless authentication and Spring Security for authorization.

## Components

### 1. **JwtService** (`security/JwtService.java`)
The core service responsible for JWT token operations.

#### Key Methods:
- `generateToken(username, userId, role)` - Creates a new JWT token with user claims
- `extractUsername(token)` - Extracts username from token
- `extractUserId(token)` - Extracts user ID from token
- `extractRole(token)` - Extracts user role from token
- `isTokenValid(token)` - Validates token signature and expiration
- `isTokenValid(token, userDetails)` - Validates token against UserDetails

#### Configuration Properties:
```properties
app.jwt.secret=rKY5Bk8zY7ANLejsGVVwCc5IrAj000N2iKMRn7uUES0=
app.jwt.expiration=86400000  # 24 hours in milliseconds
```

### 2. **JwtAuthenticationFilter** (`security/JwtAuthenticationFilter.java`)
Intercepts every HTTP request to validate JWT tokens.

#### Workflow:
1. Extracts `Authorization` header (expects format: `Bearer <token>`)
2. Validates token using JwtService
3. Extracts user information from token claims
4. Sets authentication in SecurityContext with granted authorities

#### Authorization Header Format:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. **JwtAuthenticationEntryPoint** (`security/JwtAuthenticationEntryPoint.java`)
Handles authentication failures with a standardized JSON response.

#### Response Format:
```json
{
  "status": "error",
  "data": null,
  "message": "Unauthorized"
}
```

### 4. **SecurityConfiguration** (`configue/securityConfigue.java`)
Configures Spring Security with JWT-based authentication.

#### Security Chain:
1. **Rate Limiting Filter** - Prevents brute force attacks (100 requests/minute per IP)
2. **JWT Authentication Filter** - Validates tokens
3. **Authorization Rules** - Enforces role-based access control

#### Endpoint Security Rules:
- **Public Endpoints** (No authentication required):
  - `GET /products` - List all products
  - `POST /auth/register` - Register new user
  - `POST /auth/login` - User login

- **User Endpoints** (Requires ROLE_USER or ROLE_ADMIN):
  - `POST /products` - Create product
  - `PUT /products/{id}` - Update product
  - `DELETE /products/{id}` - Delete product

- **Admin Endpoints** (Requires ROLE_ADMIN):
  - `GET /users` - List all users
  - `GET /users/{id}` - Get user details
  - `PUT /users/{id}` - Update user
  - `DELETE /users/{id}` - Delete user

### 5. **CorsConfiguration** (`configue/CorsConfiguration.java`)
Enables Cross-Origin Resource Sharing for frontend applications.

#### Configuration:
- Allows all HTTP methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Allows all headers
- Credentials support enabled
- Max age: 3600 seconds

### 6. **RateLimitingFilter** (`security/RateLimitingFilter.java`)
Implements rate limiting to prevent brute force attacks.

#### Limits:
- 100 requests per minute per client IP
- Returns HTTP 429 (Too Many Requests) when limit exceeded
- Supports X-Forwarded-For header for proxy environments

---

## Authentication Flow

### 1. **Registration**
```
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Response (201 Created):**
```json
{
  "status": "success",
  "data": null,
  "message": "User registered successfully"
}
```

### 2. **Login**
```
POST /auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  },
  "message": "User logged in successfully"
}
```

### 3. **Authenticated Request**
Use the token in subsequent requests:
```
GET /products
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Security Features

### 1. **Password Security**
- Passwords are hashed using **BCrypt** before storage
- BCryptPasswordEncoder with default strength (10 rounds)
- Passwords are never returned in API responses

### 2. **Token Security**
- JWT tokens are signed using HS256 algorithm
- Token expiration: 24 hours
- Stateless authentication (no session storage required)
- Token claims include: username, userId, role

### 3. **Role-Based Access Control (RBAC)**
- **ADMIN Role**: Full system access
- **USER Role**: Can manage own products
- First registered user automatically becomes ADMIN
- Subsequent users are registered as USER

### 4. **Rate Limiting**
- Prevents brute force attacks
- Limit: 100 requests per minute per IP
- Applies to all endpoints
- Can be customized in RateLimitingFilter

### 5. **CORS Security**
- Controlled cross-origin access
- Credentials support for secure requests
- Configurable allowed origins

### 6. **Input Validation**
- All DTOs use Jakarta validation annotations
- Request body validation in controllers
- Exception handling with meaningful error messages

---

## Error Handling

### Common Authentication Errors

**401 Unauthorized - Missing Token:**
```json
{
  "status": "error",
  "message": "Unauthorized - Missing or invalid token"
}
```

**401 Unauthorized - Invalid Token:**
```json
{
  "status": "error",
  "message": "Unauthorized - Token signature invalid"
}
```

**403 Forbidden - Insufficient Permissions:**
```json
{
  "status": "error",
  "message": "Access Denied"
}
```

**400 Bad Request - Duplicate Email:**
```json
{
  "status": "error",
  "message": "Email already exists"
}
```

**429 Too Many Requests - Rate Limit Exceeded:**
```json
{
  "status": "error",
  "message": "Rate limit exceeded. Maximum 100 requests per minute allowed."
}
```

---

## Configuration Files

### `application.properties`
```properties
# Server Configuration
server.port=8080

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://admin:admin123@localhost:27017/lets_play_db?authSource=admin

# JWT Configuration
app.jwt.secret=rKY5Bk8zY7ANLejsGVVwCc5IrAj000N2iKMRn7uUES0=
app.jwt.expiration=86400000  # 24 hours in milliseconds
```

### Maven Dependencies (`pom.xml`)
- Spring Boot 3.5.7
- Spring Security
- Spring Data MongoDB
- JWT (JJWT 0.12.3)
- Bucket4j (Rate Limiting 7.6.0)
- Lombok
- Jakarta Validation

---

## Best Practices

### 1. **Token Management**
- Store tokens securely on the client (HttpOnly cookies recommended)
- Include token in Authorization header for all authenticated requests
- Implement token refresh mechanism for long-lived sessions (future enhancement)

### 2. **Password Policies**
- Enforce strong password requirements
- Minimum 8 characters recommended
- Mix of uppercase, lowercase, numbers, and special characters

### 3. **Admin Account**
- Change default admin password after first deployment
- Use separate admin account for day-to-day operations
- Implement admin activity logging (future enhancement)

### 4. **HTTPS**
- Always use HTTPS in production
- Configure certificate and SSL/TLS
- Never transmit tokens over HTTP

### 5. **Environment Variables**
For production, consider externalizing sensitive configuration:
```bash
export JWT_SECRET="your-secure-secret-key"
export JWT_EXPIRATION="86400000"
export MONGODB_URI="mongodb+srv://user:pass@cluster.mongodb.net/dbname"
```

---

## Testing JWT Authentication

### Using cURL

**1. Register User:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

**2. Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123!"
  }'
```

**3. Access Protected Resource:**
```bash
curl -X GET http://localhost:8080/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### Using Postman

1. Set Authorization type to "Bearer Token"
2. Paste token from login response
3. Send authenticated requests

---

## Future Enhancements

1. **Token Refresh Mechanism** - Issue refresh tokens with longer expiration
2. **Token Blacklisting** - Implement logout functionality
3. **Multi-Factor Authentication** - Add 2FA support
4. **Audit Logging** - Log all authentication events
5. **OAuth2/OIDC Support** - Support third-party authentication providers
6. **API Key Authentication** - Support API keys for service-to-service calls
7. **Request Signing** - Cryptographic signing of requests
8. **User Session Management** - Track active sessions and allow termination

---

## Troubleshooting

### Issue: "Unauthorized - Missing or invalid token"
- Ensure token is included in Authorization header
- Verify token format: `Bearer <token>`
- Check token expiration time
- Regenerate token with login endpoint

### Issue: "Access Denied" for authorized user
- Verify user role is assigned correctly
- Check endpoint security rules in SecurityConfiguration
- Confirm role matches required permissions

### Issue: Rate limiting blocking legitimate requests
- Check client IP (especially behind proxy)
- Adjust limit in RateLimitingFilter if needed
- Implement X-Forwarded-For header support

### Issue: CORS errors in frontend
- Verify CORS configuration allows frontend origin
- Check allowed methods and headers
- Ensure credentials flag is set if needed

---

## Conclusion

This comprehensive security implementation provides:
✅ JWT-based stateless authentication
✅ Role-based access control
✅ Password hashing with BCrypt
✅ Rate limiting for brute force prevention
✅ CORS support for frontend integration
✅ Comprehensive error handling
✅ Production-ready security features

For questions or issues, refer to:
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io Documentation](https://jwt.io/)
- [OWASP Security Guidelines](https://owasp.org/)
