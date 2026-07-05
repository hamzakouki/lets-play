package com.letsplay.service;

import com.letsplay.repository.UserRepository;
import com.letsplay.exception.EmailAlreadyExistsException;
import com.letsplay.entities.User;
import com.letsplay.exception.DuplicateResourceException;
import com.letsplay.entities.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.letsplay.dto.LoginResponse;
import com.letsplay.dto.LoginRequest;
import com.letsplay.exception.UserNotFoundException;
import com.letsplay.exception.InvalidCredentialsException;

public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void register(com.letsplay.dto.RegisterRequest dto) {
        // Check if the user already exists
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Create a new User entity and save it to the database
        User user = new User();
        
        
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));


        if (userRepository.count() == 0) {
            user.setRole(Role.ADMIN); // First user is an admin
        }else {
            user.setRole(Role.USER); // Subsequent users are regular users
        }
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest dto) {
        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }
        
        // Return a LoginResponse with user details (you can customize this as needed)
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());

    }

}