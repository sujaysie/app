package com.store.userservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.store.userservice.dtos.LoginRequest;
import com.store.userservice.dtos.TokenResponse;
import com.store.userservice.models.User;
import com.store.userservice.repo.UserRepository;
import com.store.userservice.services.TokenIssuanceService;
import com.store.userservice.services.UserAccountService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthControllerTest {

    @Test
    void loginReturnsTokenPayloadForValidCredentials() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserAccountService userAccountService = Mockito.mock(UserAccountService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        TokenIssuanceService tokenIssuanceService = Mockito.mock(TokenIssuanceService.class);

        AuthController authController = new AuthController(userRepository, userAccountService, passwordEncoder, tokenIssuanceService);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("plain-pass");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded-pass");

        TokenResponse tokenResponse = new TokenResponse("access", "refresh", "Bearer", 900, java.util.List.of("USER"));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-pass", "encoded-pass")).thenReturn(true);
        when(tokenIssuanceService.issueTokens(user)).thenReturn(tokenResponse);

        ResponseEntity<TokenResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("access", response.getBody().getAccessToken());
    }

    @Test
    void loginRejectsInvalidCredentials() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserAccountService userAccountService = Mockito.mock(UserAccountService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        TokenIssuanceService tokenIssuanceService = Mockito.mock(TokenIssuanceService.class);

        AuthController authController = new AuthController(userRepository, userAccountService, passwordEncoder, tokenIssuanceService);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong-pass");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded-pass");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-pass", "encoded-pass")).thenReturn(false);

        ResponseEntity<TokenResponse> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }
}
