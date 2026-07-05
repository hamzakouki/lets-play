package com.letsplay.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;       // MongoDB _id

    private String username;
    private String email;
    private String password;
    private Role role;     // e.g., "user" or "admin"
}
