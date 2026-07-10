package com.letsplay.service;

import org.springframework.stereotype.Service;

import com.letsplay.dto.ProductRequest;
import com.letsplay.dto.ProductResponse;
import com.letsplay.entities.Product;
import com.letsplay.exception.UserNotFoundException;
import com.letsplay.repository.ProductRepository;
import com.letsplay.repository.UserRepository;
import com.letsplay.entities.User;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService( ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    

    public ProductResponse createProduct(
        ProductRequest request,
        String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new UserNotFoundException("User not found"));

    Product product = new Product();

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());

    product.setUserId(user.getId());

    Product savedProduct = productRepository.save(product);

    return new ProductResponse(
            savedProduct.getId(),
            savedProduct.getName(),
            savedProduct.getDescription(),
            savedProduct.getPrice(),
            savedProduct.getUserId());
}
}