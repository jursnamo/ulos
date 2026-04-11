package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.AuthTokenEntity;
import com.enterprise.ulos.los.entity.UserEntity;
import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.repository.AuthTokenRepository;
import com.enterprise.ulos.los.repository.UserRepository;
import com.enterprise.ulos.los.security.RequestUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private static final long TOKEN_EXPIRY_HOURS = 12;

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordHashService passwordHashService;

    public AuthService(
            UserRepository userRepository,
            AuthTokenRepository authTokenRepository,
            PasswordHashService passwordHashService
    ) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordHashService = passwordHashService;
    }

    public AuthApiModels.LoginResponse login(AuthApiModels.LoginRequest request) {
        if (request == null || request.username() == null || request.username().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required");
        }

        UserEntity user = userRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!user.isActiveFlag()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is inactive");
        }
        if (!passwordHashService.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        LocalDateTime now = LocalDateTime.now();
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString().substring(0, 8);

        AuthTokenEntity authToken = new AuthTokenEntity();
        authToken.setTokenValue(token);
        authToken.setUser(user);
        authToken.setIssuedAt(now);
        authToken.setExpiresAt(now.plusHours(TOKEN_EXPIRY_HOURS));
        authToken.setLastAccessedAt(now);
        authToken.setRevokedFlag(false);
        authTokenRepository.save(authToken);

        user.setLastLoginAt(now);
        userRepository.save(user);
        authTokenRepository.deleteByRevokedFlagTrueOrExpiresAtBefore(now.minusDays(1));

        return new AuthApiModels.LoginResponse(
                token,
                authToken.getExpiresAt(),
                toProfile(user)
        );
    }

    @Transactional(readOnly = true)
    public AuthApiModels.UserProfileResponse me(String token) {
        return toProfile(resolveTokenEntity(token).getUser());
    }

    public AuthApiModels.LogoutResponse logout(String token) {
        AuthTokenEntity authToken = resolveTokenEntity(token);
        authToken.setRevokedFlag(true);
        authTokenRepository.save(authToken);
        return new AuthApiModels.LogoutResponse(true);
    }

    public RequestUserPrincipal resolvePrincipal(String token) {
        AuthTokenEntity authToken = resolveTokenEntity(token);
        UserEntity user = authToken.getUser();
        authToken.setLastAccessedAt(LocalDateTime.now());
        authTokenRepository.save(authToken);
        return new RequestUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRoles().stream()
                        .map(role -> role.getRoleCode().toUpperCase())
                        .sorted(Comparator.naturalOrder())
                        .toList()
        );
    }

    private AuthTokenEntity resolveTokenEntity(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication token");
        }
        AuthTokenEntity authToken = authTokenRepository.findByTokenValueAndRevokedFlagFalse(token.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token"));

        if (!authToken.getUser().isActiveFlag()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is inactive");
        }
        if (authToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication token has expired");
        }
        return authToken;
    }

    private AuthApiModels.UserProfileResponse toProfile(UserEntity user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleCode().toUpperCase())
                .sorted(Comparator.naturalOrder())
                .toList();
        return new AuthApiModels.UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.isActiveFlag(),
                roles
        );
    }
}
