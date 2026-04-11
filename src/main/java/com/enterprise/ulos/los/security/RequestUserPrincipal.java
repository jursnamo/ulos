package com.enterprise.ulos.los.security;

import java.util.List;

public record RequestUserPrincipal(
        Long userId,
        String username,
        String fullName,
        List<String> roles
) {
    public boolean hasAnyRole(List<String> expected) {
        if (expected == null || expected.isEmpty()) {
            return true;
        }
        return roles.stream().anyMatch(expected::contains);
    }
}
