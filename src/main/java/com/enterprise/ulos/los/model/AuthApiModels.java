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

    public record LoginResponse(
            String accessToken,
            String tokenType,
            UserProfileResponse user
    ) {
    }

    public record UserRequest(
            String username,
            String password,
            String fullName,
            String email,
            Boolean active,
            List<String> roleCodes
    ) {
    }

    public record UserProfileResponse(
            Long id,
            String username,
            String fullName,
            String email,
            boolean active,
            List<String> roles,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record RoleResponse(
            Long id,
            String code,
            String name,
            String description
    ) {
    }
}
