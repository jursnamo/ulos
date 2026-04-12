package com.enterprise.ulos.los.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.security.jwt-secret:ulos-local-dev-jwt-secret-key-2026-please-change}") String secret,
            @Value("${app.security.jwt-expiration-seconds:36000}") long expirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(normalizeSecret(secret).getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getRoles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object roles = parseClaims(token).get("roles");
        if (roles instanceof List<?> values) {
            return values.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    public boolean isTokenValid(String token, AuthenticatedUser user) {
        Claims claims = parseClaims(token);
        return user.getUsername().equals(claims.getSubject()) && claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String normalizeSecret(String secret) {
        String value = secret == null ? "" : secret.trim();
        if (value.length() >= 32) {
            return value;
        }
        StringBuilder builder = new StringBuilder(value);
        while (builder.length() < 32) {
            builder.append("0");
        }
        return builder.toString();
    }
}
