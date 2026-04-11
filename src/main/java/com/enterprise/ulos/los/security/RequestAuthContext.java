package com.enterprise.ulos.los.security;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RequestAuthContext {

    private static final ThreadLocal<RequestUserPrincipal> CURRENT_USER = new ThreadLocal<>();

    public void set(RequestUserPrincipal principal) {
        CURRENT_USER.set(principal);
    }

    public Optional<RequestUserPrincipal> get() {
        return Optional.ofNullable(CURRENT_USER.get());
    }

    public RequestUserPrincipal require() {
        RequestUserPrincipal principal = CURRENT_USER.get();
        if (principal == null) {
            throw new IllegalStateException("Request user context is not available");
        }
        return principal;
    }

    public void clear() {
        CURRENT_USER.remove();
    }
}
