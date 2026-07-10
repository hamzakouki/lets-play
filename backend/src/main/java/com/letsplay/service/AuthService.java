package com.letsplay.service;

import com.letsplay.dto.LoginResponse;
import com.letsplay.dto.LoginRequest;
import com.letsplay.dto.RegisterRequest;
import com.letsplay.entities.Role;
import com.letsplay.entities.User;
import com.letsplay.exception.DuplicateResourceException;
import com.letsplay.exception.EmailAlreadyExistsException;
import com.letsplay.exception.InvalidCredentialsException;
import com.letsplay.repository.UserRepository;
import com.letsplay.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user in the system
     * The first user registered will be assigned the ADMIN role,
     * all subsequent users will be assigned the USER role
     */
    public void register(RegisterRequest dto) {
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

        // First user is an admin, subsequent users are regular users
        if (userRepository.count() == 0) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        
        userRepository.save(user);
    }

    /**
     * Authenticate user and return JWT token
     */
    public LoginResponse login(LoginRequest dto) {
        // Find user by username
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRole().name());

        // Return login response with token
        return new LoginResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }
}