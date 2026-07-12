package com.letsplay.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.letsplay.dto.ApiResponse;
import com.letsplay.dto.ProductRequest;
import com.letsplay.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.letsplay.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody @Valid ProductRequest request,
            Authentication authentication) {

        ProductResponse product = productService.createProduct(request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", product, "Product created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "success",
                        products,
                        "Products retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String id) {

        ProductResponse product = productService.getProductById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "success",
                        product,
                        "Product retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable String id,
            @RequestBody @Valid ProductRequest request, Authentication authentication) {

        ProductResponse product = productService.updateProduct(id, request, authentication.getName());

        return ResponseEntity.ok(new ApiResponse<>("success", product, "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id, Authentication authentication) {
        productService.deleteProduct(id, authentication.getName());

        return ResponseEntity.ok(new ApiResponse<>("success", null, "Product deleted successfully"));
    }
}
