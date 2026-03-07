package com.store.userservice.controller;

import com.store.userservice.dtos.AuthResponse;
import com.store.userservice.dtos.LoginRequest;
import com.store.userservice.dtos.RefreshTokenRequest;
import com.store.userservice.dtos.SignupRequest;
import com.store.userservice.dtos.TokenResponse;
import com.store.userservice.models.User;
import com.store.userservice.repo.UserRepository;
import com.store.userservice.services.TokenIssuanceService;
import com.store.userservice.services.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final UserAccountService userAccountService;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuanceService tokenIssuanceService;

    public AuthController(UserRepository userRepository,
                          UserAccountService userAccountService,
                          PasswordEncoder passwordEncoder,
                          TokenIssuanceService tokenIssuanceService) {
        this.userRepository = userRepository;
        this.userAccountService = userAccountService;
        this.passwordEncoder = passwordEncoder;
        this.tokenIssuanceService = tokenIssuanceService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse("Email already in use"));
        }

        User user = userAccountService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse("User registered with email: " + user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    String storedPassword = user.getPassword();
                    if (storedPassword != null && passwordEncoder.matches(request.getPassword(), storedPassword)) {
                        return ResponseEntity.ok(tokenIssuanceService.issueTokens(user));
                    }
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<TokenResponse>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).<TokenResponse>build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        TokenResponse response = tokenIssuanceService.rotateRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().body(new AuthResponse("refreshToken is required"));
        }
        tokenIssuanceService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new AuthResponse("Logged out successfully"));
    }
}


