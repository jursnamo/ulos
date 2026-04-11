package com.enterprise.ulos.los.model;

import java.time.LocalDateTime;
import java.util.List;

public final class UserManagementApiModels {

    private UserManagementApiModels() {
    }

    public record UserUpsertRequest(
            String username,
            String fullName,
            String email,
            String password,
            Boolean active,
            List<String> roles
    ) {
    }

    public record PasswordResetRequest(
            String password
    ) {
    }

    public record UserResponse(
            Long userId,
            String username,
            String fullName,
            String email,
            boolean active,
            List<String> roles,
            LocalDateTime lastLoginAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
