package com.letsplay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct( @RequestBody @Valid ProductRequest request, Authentication authentication) {

        ProductResponse product = productService.createProduct( request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("success", product, "Product created successfully"));
    }
}
