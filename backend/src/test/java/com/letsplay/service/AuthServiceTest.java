package com.letsplay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.letsplay.dto.LoginRequest;
import com.letsplay.dto.LoginResponse;
import com.letsplay.entities.Role;
import com.letsplay.entities.User;
import com.letsplay.repository.UserRepository;
import com.letsplay.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, jwtService, passwordEncoder);
    }

    @Test
    void loginShouldAuthenticateUsingEmailAsIdentifier() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@example.com");
        request.setPassword("secret123");

        User user = new User();
        user.setId("user-1");
        user.setUsername("jdoe");
        user.setEmail("user@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("user@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken("jdoe", "user-1", Role.USER.name())).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("jdoe", response.getUsername());
        assertEquals("user@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }
}
