package com.letsplay.repository;

import com.letsplay.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    // Find all products by owner userId
    List<Product> findByUserId(String userId);

    // If you want to support searching by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
}
