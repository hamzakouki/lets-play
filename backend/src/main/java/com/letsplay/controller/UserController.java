package com.letsplay.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.letsplay.dto.ApiResponse;
import com.letsplay.dto.UserResponse;
import com.letsplay.dto.UserUpdateRequest;
import com.letsplay.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok( new ApiResponse<>( "success", users, "Users retrieved successfully"));
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>( "success", user, "User retrieved successfully"));
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser( @PathVariable String id, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>( "success", user, "User updated successfully"));
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>( "success", null, "User deleted successfully"));
    }
}