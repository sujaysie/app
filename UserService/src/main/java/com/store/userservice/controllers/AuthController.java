package com.store.userservice.controllers;

import com.store.userservice.dtos.AuthResponse;
import com.store.userservice.dtos.LoginRequest;
import com.store.userservice.dtos.SignupRequest;
import com.store.userservice.models.User;
import com.store.userservice.services.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserAccountService userAccountService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserAccountService userAccountService,
                          AuthenticationManager authenticationManager) {
        this.userAccountService = userAccountService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        User user = userAccountService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse("User registered with email: " + user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(new AuthResponse("Login successful"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("Invalid credentials"));
    }
}
