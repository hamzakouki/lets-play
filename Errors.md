1. Validation Errors (400 Bad Request)

When the client sends invalid data.

Examples:

Empty username
Empty email
Invalid email format
Empty password
Empty product name
Negative price

Example response:
====================================================================
2. Email Already Exists (409 Conflict)

During registration.
====================================================================
3. Invalid Login Credentials (401 Unauthorized)

Example:

POST /auth/login

Wrong email or password.
====================================================================
4. User Not Found (404 Not Found)

Examples:

GET /users/{id}

PUT /users/{id}

DELETE /users/{id}

User doesn't exist.
====================================================================
5. Product Not Found (404 Not Found)

Examples:

GET /products/{id}

PUT /products/{id}

DELETE /products/{id}
====================================================================
6. Unauthorized (401 Unauthorized)

Examples:

No JWT.

Invalid JWT.

Expired JWT.

Return

401 Unauthorized

Spring Security usually handles this.
====================================================================
7. Forbidden (403 Forbidden)

The user is authenticated.

But doesn't have permission.

Example:

Hamza tries deleting Ali's product.
====================================================================
8. Duplicate Resource (409 Conflict)

Besides email.

If you later decide product names must be unique.
====================================================================
9. Method Not Allowed (405)

Example

DELETE /auth/login

Return

405 Method Not Allowed

Spring already does this.
====================================================================
10. Invalid JSON (400)

Example

Client sends

{
    "email":
}

Malformed JSON.

Return

400 Bad Request

Spring handles this.
====================================================================
11. Invalid Path Variable (400)

Example

GET /products/%%%%

or invalid parameter types.

Return

400 Bad Request
====================================================================
12. Unexpected Server Errors (500)

Your project says:

Never return unhandled 5XX errors.

So create a catch-all handler.

If something unexpected happens:

NullPointerException

MongoException

IOException

Return

500 Internal Server Error

Message:

{
    "status":"error",
    "message":"An unexpected error occurred"
}

Never expose the stack trace to the client.
====================================================================
Exceptions I would create
exception
│
├── EmailAlreadyExistsException
├── UserNotFoundException
├── ProductNotFoundException
├── InvalidCredentialsException
├── UnauthorizedException (optional)
├── ForbiddenException (optional)
└── GlobalExceptionHandler

=====================================================================================================

| Situation            | Status                        |
| -------------------- | ----------------------------- |
| Register success     | **201 Created**               |
| Login success        | **200 OK**                    |
| Get products         | **200 OK**                    |
| Create product       | **201 Created**               |
| Update product       | **200 OK**                    |
| Delete product       | **204 No Content**            |
| Validation failed    | **400 Bad Request**           |
| Invalid login        | **401 Unauthorized**          |
| Missing/invalid JWT  | **401 Unauthorized**          |
| No permission        | **403 Forbidden**             |
| User not found       | **404 Not Found**             |
| Product not found    | **404 Not Found**             |
| Email already exists | **409 Conflict**              |
| Unexpected error     | **500 Internal Server Error** |
