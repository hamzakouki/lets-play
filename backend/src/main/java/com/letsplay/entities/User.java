package com.letsplay.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;       // MongoDB _id

    private String name;
    private String email;
    private String password;
    private String role;     // e.g., "user" or "admin"
}
