package com.store.userservice.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.store.userservice.dtos.TokenResponse;
import com.store.userservice.models.RefreshToken;
import com.store.userservice.models.Role;
import com.store.userservice.models.User;
import com.store.userservice.repo.RoleRepository;
import com.store.userservice.repo.UserRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TokenIssuanceService {
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public TokenIssuanceService(JwtTokenService jwtTokenService,
                                RefreshTokenService refreshTokenService,
                                UserRepository userRepository,
                                RoleRepository roleRepository) {
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public TokenResponse issueTokens(User user) {
        User savedUser = ensureDefaultRole(user);
        String accessToken = jwtTokenService.generateAccessToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createToken(savedUser, jwtTokenService.getRefreshTokenTtlSeconds());
        String refreshTokenValue = jwtTokenService.generateRefreshToken(savedUser, refreshToken.getTokenId());
        List<String> roles = savedUser.getRoles().stream().map(Role::getName).toList();
        return new TokenResponse(accessToken, refreshTokenValue, "Bearer", jwtTokenService.getAccessTokenTtlSeconds(), roles);
    }

    @Transactional
    public TokenResponse rotateRefreshToken(String refreshTokenRaw) {
        JWTClaimsSet claims;
        try {
            claims = jwtTokenService.validateAndParse(refreshTokenRaw);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token", ex);
        }

        if (!"refresh".equals(claims.getClaim("token_use"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token type");
        }

        Long userId = parseUserId(claims.getSubject());
        String tokenId = claims.getJWTID();
        if (tokenId == null || tokenId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
        }

        RefreshToken activeToken = refreshTokenService.validateActiveToken(tokenId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is invalid or revoked"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        refreshTokenService.revoke(activeToken.getTokenId());
        return issueTokens(user);
    }

    @Transactional
    public void revokeRefreshToken(String refreshTokenRaw) {
        JWTClaimsSet claims;
        try {
            claims = jwtTokenService.validateAndParse(refreshTokenRaw);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token", ex);
        }

        if (!"refresh".equals(claims.getClaim("token_use"))) {
            return;
        }

        String tokenId = claims.getJWTID();
        if (tokenId != null && !tokenId.isBlank()) {
            refreshTokenService.revoke(tokenId);
        }
    }

    private Long parseUserId(String subject) {
        try {
            return Long.valueOf(subject);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token subject");
        }
    }

    private User ensureDefaultRole(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(new Role("USER")));
            user.setRoles(Collections.singleton(userRole));
            return userRepository.save(user);
        }
        return user;
    }
}
