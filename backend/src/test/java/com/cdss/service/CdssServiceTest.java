package com.cdss.service;

import com.cdss.dto.AuthResponse;
import com.cdss.dto.LoginRequest;
import com.cdss.dto.RegisterRequest;
import com.cdss.model.User;
import com.cdss.repository.UserRepository;
import com.cdss.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        User user = new User();
        user.setUsername("doctor");
        user.setPassword("encoded");
        user.setRole(User.Role.USER);

        when(userRepository.findByUsername("doctor")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken("doctor", "USER")).thenReturn("jwt-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("doctor");
        request.setPassword("password");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("doctor", response.getUsername());
        assertEquals("USER", response.getRole());
    }

    @Test
    void login_withInvalidPassword_shouldThrow() {
        User user = new User();
        user.setUsername("doctor");
        user.setPassword("encoded");

        when(userRepository.findByUsername("doctor")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setUsername("doctor");
        request.setPassword("wrong");

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_withInvalidUsername_shouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("password");

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void register_withNewUser_shouldReturnToken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(jwtUtil.generateToken("newuser", "USER")).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setFullName("New User");
        request.setEmail("new@test.com");

        AuthResponse response = authService.register(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("newuser", response.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_withExistingUsername_shouldThrow() {
        when(userRepository.existsByUsername("doctor")).thenReturn(true);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("doctor");
        request.setPassword("password");
        request.setFullName("Doctor");
        request.setEmail("doc@test.com");

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }
}
