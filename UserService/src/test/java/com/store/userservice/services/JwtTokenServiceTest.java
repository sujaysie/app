package com.store.userservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.jwt.JWTClaimsSet;
import com.store.userservice.configs.JwtProperties;
import com.store.userservice.models.Role;
import com.store.userservice.models.User;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JwtTokenServiceTest {

    @Test
    void generateAccessTokenContainsExpectedClaims() {
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("test-issuer");
        properties.setAudience("test-audience");
        properties.setAccessTokenTtlSeconds(900);

        JwtTokenService jwtTokenService = new JwtTokenService(new JwkService(), properties);

        User user = new User();
        user.setId(7L);
        user.setEmail("user@example.com");
        Role role = new Role();
        role.setName("USER");
        user.setRoles(Set.of(role));

        String token = jwtTokenService.generateAccessToken(user);
        assertNotNull(token);

        JWTClaimsSet claimsSet = jwtTokenService.validateAndParse(token);
        assertEquals("7", claimsSet.getSubject());
        assertEquals("user@example.com", claimsSet.getClaim("email"));
        assertEquals("access", claimsSet.getClaim("token_use"));
        assertTrue(jwtTokenService.extractRoles(claimsSet).contains("USER"));
    }

    @Test
    void generateRefreshTokenContainsRefreshMarker() {
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("test-issuer");
        properties.setAudience("test-audience");
        properties.setRefreshTokenTtlSeconds(3600);

        JwtTokenService jwtTokenService = new JwtTokenService(new JwkService(), properties);

        User user = new User();
        user.setId(9L);
        user.setEmail("refresh@example.com");

        String token = jwtTokenService.generateRefreshToken(user, "token-id-1");
        JWTClaimsSet claimsSet = jwtTokenService.validateAndParse(token);

        assertEquals("refresh", claimsSet.getClaim("token_use"));
        assertEquals("token-id-1", claimsSet.getJWTID());
        assertEquals("9", claimsSet.getSubject());
    }
}
