package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, Long> {
    Optional<AuthTokenEntity> findByTokenValueAndRevokedFlagFalse(String tokenValue);

    long deleteByRevokedFlagTrueOrExpiresAtBefore(LocalDateTime expiredAt);
}
