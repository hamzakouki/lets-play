# MongoDB Database Notes

## 1. What is MongoDB?

MongoDB is a **NoSQL, document-oriented database**.
Unlike SQL databases that use tables and rows, MongoDB stores data in **JSON-like documents** within **collections**.

* **Collection**: Similar to a table in SQL. Example: `users`, `products`.
* **Document**: Similar to a row but flexible and schema-less. Example:

```json
{
  "id": "u123",
  "name": "Hamza",
  "email": "hamza@example.com",
  "password": "hashedpassword123",
  "role": "USER"
}
```

* **Schema-less**: Documents in the same collection can have different fields.

---

## 2. NoSQL Basics

NoSQL stands for **"Not Only SQL"** — a type of database that doesn’t use a fixed relational schema.

* Advantages:

  * Flexible data structure
  * High scalability (horizontal scaling)
  * Fast for large datasets
  * Works naturally with JSON

* Types of NoSQL databases:

  | Type      | Example   | Use Case                       |
  | --------- | --------- | ------------------------------ |
  | Document  | MongoDB   | Store JSON-like objects        |
  | Key-Value | Redis     | Fast caching, session storage  |
  | Graph     | Neo4j     | Social networks, relationships |
  | Column    | Cassandra | Big data analytics             |

---

## 3. MongoDB Data Model

### Collections

* Collections store multiple documents.
* Example: `users` collection stores all user documents.

### Documents

* JSON-like objects (BSON internally) that represent single records.
* Example `product` document:

```json
{
  "id": "p101",
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 1200.0,
  "userId": "u123"
}
```

### Fields

* Key-value pairs in a document
* Can be nested objects or arrays

```json
{
  "name": "Hamza",
  "email": "hamza@example.com",
  "addresses": [
    {"city": "Casablanca", "zip": "20000"},
    {"city": "Rabat", "zip": "10000"}
  ]
}
```

---

## 4. CRUD Operations in MongoDB

| Operation | MongoDB Command             | REST API Equivalent               |
| --------- | --------------------------- | --------------------------------- |
| Create    | `insertOne(document)`       | `POST /users`                     |
| Read      | `find()`, `findOne()`       | `GET /users` or `GET /users/{id}` |
| Update    | `updateOne(filter, update)` | `PUT /users/{id}`                 |
| Delete    | `deleteOne(filter)`         | `DELETE /users/{id}`              |

---

## 5. Indexing

* Indexes improve query performance.
* Common indexes:

  * `_id` (default, unique)
  * `email` in `users` for fast lookup
* Example:

```javascript
db.users.createIndex({ "email": 1 }, { unique: true })
```

---

## 6. Relationships in MongoDB

MongoDB is **non-relational**, but you can model relationships in two ways:

1. **Embedding** (store related data inside the document)

```json
{
  "name": "Hamza",
  "products": [
    {"id": "p101", "name": "Laptop"},
    {"id": "p102", "name": "Phone"}
  ]
}
```

2. **Referencing** (store only IDs, like foreign keys)

```json
{
  "id": "p101",
  "name": "Laptop",
  "userId": "u123"
}
```

> In your project, we use **referencing**: Products store `userId` to indicate the owner.

---

## 7. Best Practices

* Always **hash sensitive data** (passwords) before storing.
* Validate data before inserting to prevent injections.
* Use **indexes** for frequently queried fields.
* Keep collections and documents simple and consistent.
* Use transactions only when necessary (MongoDB supports multi-document transactions in replica sets).

---

## 8. Tools for MongoDB

* **Mongo Shell** → interactive command-line client
* **MongoDB Compass** → GUI to browse collections visually
* **Mongoose / Spring Data MongoDB** → libraries to work programmatically in JS or Java

---

## 9. How it fits in this project

* `users` collection → stores user accounts (id, name, email, password, role)
* `products` collection → stores products (id, name, description, price, userId)
* Backend uses **Spring Data MongoDB** to perform CRUD operations.
* Frontend communicates with backend via **RESTful APIs**.
* MongoDB stores all data in a flexible and scalable way.
