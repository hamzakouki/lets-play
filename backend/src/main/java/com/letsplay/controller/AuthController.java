package com.letsplay.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.letsplay.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.letsplay.dto.ApiResponse;
import com.letsplay.dto.RegisterRequest;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody @Valid RegisterRequest dto) {
        authService.register(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("success", null, "User registered successfully"));
    }
}
