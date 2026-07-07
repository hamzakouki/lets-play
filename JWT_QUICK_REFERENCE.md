# JWT Authentication Quick Reference

## Quick Start

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

### 2. Login and Get Token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123!"
  }'
```

**Response:**
```json
{
  "status": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER"
  },
  "message": "User logged in successfully"
}
```

### 3. Use Token in Requests
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X GET http://localhost:8080/products \
  -H "Authorization: Bearer $TOKEN"
```

---

## JWT Token Structure

A JWT token consists of three parts separated by dots:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJ1c2VyIiwidXNlcklkIjoiMTIzIiwicm9sZSI6IlVTRVIifQ.
signature
```

1. **Header** - Algorithm and token type
2. **Payload** - User claims (username, userId, role)
3. **Signature** - HMAC-SHA256 signature

---

## Token Claims

| Claim | Description | Example |
|-------|-------------|---------|
| `sub` | Subject (username) | `john_doe` |
| `userId` | User ID from MongoDB | `507f1f77bcf86cd799439011` |
| `role` | User role | `USER` or `ADMIN` |
| `iat` | Issued at timestamp | `1704067200` |
| `exp` | Expiration timestamp | `1704153600` |

---

## Endpoint Security Summary

| Method | Endpoint | Auth Required | Role Required | Description |
|--------|----------|---------------|---------------|-------------|
| POST | `/auth/register` | ❌ No | - | Register new user |
| POST | `/auth/login` | ❌ No | - | Login and get token |
| GET | `/products` | ❌ No | - | Get all products |
| POST | `/products` | ✅ Yes | USER/ADMIN | Create product |
| PUT | `/products/{id}` | ✅ Yes | USER/ADMIN | Update product |
| DELETE | `/products/{id}` | ✅ Yes | USER/ADMIN | Delete product |
| GET | `/users` | ✅ Yes | ADMIN | List all users |
| GET | `/users/{id}` | ✅ Yes | ADMIN | Get user details |
| PUT | `/users/{id}` | ✅ Yes | ADMIN | Update user |
| DELETE | `/users/{id}` | ✅ Yes | ADMIN | Delete user |

---

## Common Scenarios

### Scenario 1: User Creates Product
```bash
# 1. Register user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller",
    "email": "seller@example.com",
    "password": "Pass123!"
  }'

# 2. Login
TOKEN=$(curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller",
    "password": "Pass123!"
  }' | jq -r '.data.token')

# 3. Create product
curl -X POST http://localhost:8080/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99
  }'
```

### Scenario 2: Admin Views All Users
```bash
# 1. Admin login (first registered user gets ADMIN role)
TOKEN=$(curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_user",
    "password": "AdminPass123!"
  }' | jq -r '.data.token')

# 2. Get all users
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN"
```

### Scenario 3: Regular User Cannot Access Admin Endpoints
```bash
# Regular user tries to access /users (admin endpoint)
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer $REGULAR_USER_TOKEN"

# Response: 403 Forbidden
{
  "status": "error",
  "message": "Access Denied"
}
```

---

## Rate Limiting

- **Limit**: 100 requests per minute per client IP
- **Response**: HTTP 429 (Too Many Requests)
- **Error Message**: "Rate limit exceeded. Maximum 100 requests per minute allowed."

### Example Rate Limit Error:
```bash
# After 100+ requests in a minute
curl -X GET http://localhost:8080/products

# Response:
HTTP/1.1 429 Too Many Requests
Rate limit exceeded. Maximum 100 requests per minute allowed.
```

---

## Error Responses

### 400 Bad Request - Invalid Input
```json
{
  "status": "error",
  "message": "Email already exists"
}
```

### 401 Unauthorized - Missing/Invalid Token
```json
{
  "status": "error",
  "message": "Unauthorized"
}
```

### 403 Forbidden - Insufficient Permissions
```json
{
  "status": "error",
  "message": "Access Denied"
}
```

### 404 Not Found
```json
{
  "status": "error",
  "message": "User not found"
}
```

### 429 Too Many Requests
```json
{
  "status": "error",
  "message": "Rate limit exceeded. Maximum 100 requests per minute allowed."
}
```

---

## Token Expiration

- **Default Expiration**: 24 hours
- **Configuration**: `app.jwt.expiration=86400000` (milliseconds)
- **Action Required**: User must login again to get new token

---

## Security Tips

1. ✅ Always use HTTPS in production
2. ✅ Store tokens securely (HttpOnly cookies recommended)
3. ✅ Never commit secrets to version control
4. ✅ Change default admin password after deployment
5. ✅ Implement token rotation/refresh for long sessions
6. ✅ Monitor rate limit violations
7. ✅ Use strong passwords (min 8 chars, mixed case, numbers, symbols)

---

## Debugging

### Check if Token is Valid
```bash
# Decode JWT token (visit jwt.io)
# Copy token and paste on jwt.io to view claims

# Or use jq and base64:
TOKEN="your_jwt_token"
echo $TOKEN | cut -d. -f2 | base64 -d | jq
```

### Check User Role
```bash
# Login response includes role
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"pass"}' | jq '.data.role'
```

### Verify Token in Request
```bash
# Check Authorization header
curl -v -X GET http://localhost:8080/products \
  -H "Authorization: Bearer $TOKEN" 2>&1 | grep Authorization
```

---

## Integration Examples

### JavaScript/Fetch
```javascript
// Register
async function register(username, email, password) {
  const response = await fetch('http://localhost:8080/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, email, password })
  });
  return response.json();
}

// Login
async function login(username, password) {
  const response = await fetch('http://localhost:8080/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.data.token);
  return data;
}

// Authenticated request
async function getProducts() {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/products', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
}
```

### Python/Requests
```python
import requests

BASE_URL = 'http://localhost:8080'

# Login
response = requests.post(f'{BASE_URL}/auth/login', json={
    'username': 'user',
    'password': 'pass'
})
token = response.json()['data']['token']

# Authenticated request
headers = {'Authorization': f'Bearer {token}'}
products = requests.get(f'{BASE_URL}/products', headers=headers).json()
print(products)
```

---

## Support

For more details, see [SECURITY.md](./SECURITY.md)

For questions:
1. Check [Spring Security Documentation](https://spring.io/projects/spring-security)
2. Review security configuration in `src/main/java/com/letsplay/configue/`
3. Check JWT service implementation in `src/main/java/com/letsplay/security/`
