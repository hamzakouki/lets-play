package com.letsplay.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.letsplay.dto.UserResponse;
import com.letsplay.dto.UserUpdateRequest;
import com.letsplay.entities.User;
import com.letsplay.exception.DuplicateResourceException;
import com.letsplay.exception.EmailAlreadyExistsException;
import com.letsplay.exception.UserNotFoundException;
import com.letsplay.repository.ProductRepository;
import com.letsplay.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserService(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ================= GET ALL USERS =================

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    // ================= GET USER BY ID =================

    public UserResponse getUserById(String id) {

        User user = getUserEntityById(id);

        return toUserResponse(user);
    }

    // ================= UPDATE USER =================

    public UserResponse updateUser(String id, UserUpdateRequest request) {

        User user = getUserEntityById(id);

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.findByUsername(request.getUsername()).isPresent()) {

            throw new DuplicateResourceException("Username already exists");
        }

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.findByEmail(request.getEmail()).isPresent()) {

            throw new EmailAlreadyExistsException("Email already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);

        return toUserResponse(updatedUser);
    }

    // ================= DELETE USER =================

    public void deleteUser(String id) {

        User user = getUserEntityById(id);

        // Delete all products owned by the user
        productRepository.deleteByUserId(user.getId());

        // Delete the user
        userRepository.delete(user);
    }

    // ================= HELPERS =================

    private User getUserEntityById(String id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private UserResponse toUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole());
    }
}