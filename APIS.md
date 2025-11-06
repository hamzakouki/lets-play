# Project Notes: API Concepts

## What is an API?
API stands for "Application Programming Interface". 
It is a set of rules that allows one program (like a frontend) 
to communicate with another program (like a backend). 
The API defines what data can be requested and how to interact with the backend.

## What is a RESTful API?
A RESTful API is an API that follows REST principles:
- Uses HTTP methods correctly (GET, POST, PUT, DELETE)
- Uses resource-based URLs (nouns, not verbs) like /users or /products
- Returns proper HTTP status codes (200 OK, 404 Not Found, 401 Unauthorized, etc.)
- Uses JSON to exchange data
- Is stateless (each request contains all needed info)
- Has consistent URL structure and clear error messages

## What is CRUD?
CRUD stands for the four basic operations for managing data:
- **C**reate → Add new data (POST /users)
- **R**ead → Get data (GET /users or GET /users/{id})
- **U**pdate → Modify existing data (PUT /users/{id})
- **D**elete → Remove data (DELETE /users/{id})

## How it works together
- The frontend communicates with the backend through the API endpoints.
- Each endpoint is part of the RESTful API and follows CRUD operations.
- HTTP is just the transport method; the API defines the rules and data available.
