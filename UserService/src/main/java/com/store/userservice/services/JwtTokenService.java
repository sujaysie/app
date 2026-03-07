package com.store.userservice.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.store.userservice.configs.JwtProperties;
import com.store.userservice.models.User;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final JwkService jwkService;
    private final JwtProperties jwtProperties;

    public JwtTokenService(JwkService jwkService, JwtProperties jwtProperties) {
        this.jwkService = jwkService;
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getAccessTokenTtlSeconds());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getId()))
                .issuer(jwtProperties.getIssuer())
                .audience(jwtProperties.getAudience())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiry))
                .jwtID(UUID.randomUUID().toString())
                .claim("token_use", "access")
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(role -> role.getName()).toList())
                .build();

        return sign(claimsSet);
    }

    public String generateRefreshToken(User user, String tokenId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getRefreshTokenTtlSeconds());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getId()))
                .issuer(jwtProperties.getIssuer())
                .audience(jwtProperties.getAudience())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiry))
                .jwtID(tokenId)
                .claim("token_use", "refresh")
                .claim("email", user.getEmail())
                .build();

        return sign(claimsSet);
    }

    public JWTClaimsSet validateAndParse(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            RSAKey rsaKey = jwkService.getRsaKey();
            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            if (!signedJWT.verify(verifier)) {
                throw new IllegalArgumentException("Invalid token signature");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (!jwtProperties.getIssuer().equals(claimsSet.getIssuer())) {
                throw new IllegalArgumentException("Invalid token issuer");
            }
            if (claimsSet.getAudience() == null || !claimsSet.getAudience().contains(jwtProperties.getAudience())) {
                throw new IllegalArgumentException("Invalid token audience");
            }
            Date expiresAt = claimsSet.getExpirationTime();
            if (expiresAt == null || expiresAt.before(new Date())) {
                throw new IllegalArgumentException("Token expired");
            }

            return claimsSet;
        } catch (ParseException | JOSEException ex) {
            throw new IllegalArgumentException("Invalid token", ex);
        }
    }

    public long getAccessTokenTtlSeconds() {
        return jwtProperties.getAccessTokenTtlSeconds();
    }

    public long getRefreshTokenTtlSeconds() {
        return jwtProperties.getRefreshTokenTtlSeconds();
    }

    public List<String> extractRoles(JWTClaimsSet claimsSet) {
        Object rolesClaim = claimsSet.getClaim("roles");
        if (rolesClaim instanceof List<?> roles) {
            return roles.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private String sign(JWTClaimsSet claimsSet) {
        try {
            RSAKey rsaKey = jwkService.getRsaKey();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .type(JOSEObjectType.JWT)
                            .keyID(rsaKey.getKeyID())
                            .build(),
                    claimsSet
            );
            signedJWT.sign(new RSASSASigner(rsaKey.toRSAPrivateKey()));
            return signedJWT.serialize();
        } catch (JOSEException ex) {
            throw new IllegalStateException("Unable to sign token", ex);
        }
    }
}
