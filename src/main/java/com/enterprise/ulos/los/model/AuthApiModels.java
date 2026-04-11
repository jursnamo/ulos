package com.enterprise.ulos.los.model;

import java.time.LocalDateTime;
import java.util.List;

public final class AuthApiModels {

    private AuthApiModels() {
    }

    public record LoginRequest(
            String username,
            String password
    ) {
    }

    public record UserProfileResponse(
            Long userId,
            String username,
            String fullName,
            String email,
            boolean active,
            List<String> roles
    ) {
    }

    public record LoginResponse(
            String token,
            LocalDateTime expiresAt,
            UserProfileResponse user
    ) {
    }

    public record LogoutResponse(
            boolean success
    ) {
    }
}
