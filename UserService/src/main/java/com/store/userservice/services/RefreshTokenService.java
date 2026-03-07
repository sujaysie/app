package com.store.userservice.services;

import com.store.userservice.models.RefreshToken;
import com.store.userservice.models.User;
import com.store.userservice.repo.RefreshTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createToken(User user, long ttlSeconds) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(ttlSeconds));
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> validateActiveToken(String tokenId, Long userId) {
        return refreshTokenRepository.findByTokenId(tokenId)
                .filter(token -> !token.isRevoked())
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .filter(token -> token.getUser().getId().equals(userId));
    }

    @Transactional
    public void revoke(String tokenId) {
        refreshTokenRepository.findByTokenId(tokenId).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }
}
