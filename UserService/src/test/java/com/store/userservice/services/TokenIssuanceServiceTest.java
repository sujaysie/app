package com.store.userservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nimbusds.jwt.JWTClaimsSet;
import com.store.userservice.dtos.TokenResponse;
import com.store.userservice.models.RefreshToken;
import com.store.userservice.models.Role;
import com.store.userservice.models.User;
import com.store.userservice.repo.RoleRepository;
import com.store.userservice.repo.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TokenIssuanceServiceTest {

    @Test
    void rotateRefreshTokenRevokesOldTokenAndReturnsNewTokens() {
        JwtTokenService jwtTokenService = Mockito.mock(JwtTokenService.class);
        RefreshTokenService refreshTokenService = Mockito.mock(RefreshTokenService.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);

        TokenIssuanceService tokenIssuanceService = new TokenIssuanceService(
                jwtTokenService,
                refreshTokenService,
                userRepository,
                roleRepository
        );

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("10")
                .claim("token_use", "refresh")
                .jwtID("old-refresh-id")
                .build();

        Role userRole = new Role();
        userRole.setName("USER");
        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");
        user.setRoles(Set.of(userRole));

        RefreshToken activeToken = new RefreshToken();
        activeToken.setTokenId("old-refresh-id");
        activeToken.setUser(user);
        activeToken.setExpiresAt(Instant.now().plusSeconds(3600));

        RefreshToken newToken = new RefreshToken();
        newToken.setTokenId("new-refresh-id");
        newToken.setUser(user);
        newToken.setExpiresAt(Instant.now().plusSeconds(7200));

        when(jwtTokenService.validateAndParse("refresh-token")).thenReturn(claimsSet);
        when(refreshTokenService.validateActiveToken("old-refresh-id", 10L)).thenReturn(Optional.of(activeToken));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(jwtTokenService.generateAccessToken(user)).thenReturn("new-access-token");
        when(refreshTokenService.createToken(user, 604800L)).thenReturn(newToken);
        when(jwtTokenService.getRefreshTokenTtlSeconds()).thenReturn(604800L);
        when(jwtTokenService.generateRefreshToken(user, "new-refresh-id")).thenReturn("new-refresh-token");
        when(jwtTokenService.getAccessTokenTtlSeconds()).thenReturn(900L);

        TokenResponse response = tokenIssuanceService.rotateRefreshToken("refresh-token");

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(refreshTokenService).revoke("old-refresh-id");
    }

    @Test
    void revokeRefreshTokenMarksTokenRevoked() {
        JwtTokenService jwtTokenService = Mockito.mock(JwtTokenService.class);
        RefreshTokenService refreshTokenService = Mockito.mock(RefreshTokenService.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);

        TokenIssuanceService tokenIssuanceService = new TokenIssuanceService(
                jwtTokenService,
                refreshTokenService,
                userRepository,
                roleRepository
        );

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("11")
                .claim("token_use", "refresh")
                .jwtID("refresh-to-revoke")
                .build();

        when(jwtTokenService.validateAndParse(anyString())).thenReturn(claimsSet);

        tokenIssuanceService.revokeRefreshToken("refresh-token");

        verify(refreshTokenService).revoke("refresh-to-revoke");
    }
}
