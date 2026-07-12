package com.letsplay.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.letsplay.dto.ProductRequest;
import com.letsplay.dto.ProductResponse;
import com.letsplay.entities.Product;
import com.letsplay.exception.ForbiddenException;
import com.letsplay.exception.ProductNotFoundException;
import com.letsplay.exception.UserNotFoundException;
import com.letsplay.repository.ProductRepository;
import com.letsplay.repository.UserRepository;
import com.letsplay.entities.User;
import com.letsplay.entities.Role;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // delete product =========================
    public void deleteProduct(String id, String username) {

        User user = getUserByUsername(username);

        Product product = getProductEntityById(id);

        // Only the owner or an admin can delete
        if (user.getRole() != Role.ADMIN && !product.getUserId().equals(user.getId())) {
            throw new ForbiddenException( "You are not authorized to delete this product");
        }
        productRepository.delete(product);
    }

    // create product ==========================
    public ProductResponse createProduct(ProductRequest request, String username) {
        User user = getUserByUsername(username);

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        product.setUserId(user.getId());

        Product savedProduct = productRepository.save(product);

        return toProductResponse(savedProduct);
    }

    // retrieve all products ==========================
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    // retrieve product by id ==========================
    public ProductResponse getProductById(String id) {

        Product product = getProductEntityById(id);

        return toProductResponse(product);
    }

    // update product =========================
    public ProductResponse updateProduct(String id, ProductRequest request, String username) {

        // Get the authenticated user
        User user = getUserByUsername(username);

        // Get the product
        Product product = getProductEntityById(id);

        // Only the owner or an admin can update
        if (user.getRole() != Role.ADMIN && !product.getUserId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to update this product");
        }

        // Update fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        // Save changes
        Product updatedProduct = productRepository.save(product);

        // Return response
        return toProductResponse(updatedProduct);
    }

    // this method retrieves a User entity based on the provided username. If the
    // user is not found, it throws a UserNotFoundException.
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // this method retrieves a Product entity based on the provided product ID. If
    // the product is not found, it throws a ProductNotFoundException.
    private Product getProductEntityById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    // this method updates an existing product based on the provided product ID and
    // request data. It first retrieves the product entity, updates its fields,
    // saves the changes, and returns a ProductResponse.
    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getUserId());
    }
}