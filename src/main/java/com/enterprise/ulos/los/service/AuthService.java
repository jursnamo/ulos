package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.AppUserEntity;
import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.repository.AppUserRepository;
import com.enterprise.ulos.los.security.AuthenticatedUser;
import com.enterprise.ulos.los.security.JwtService;
import com.enterprise.ulos.los.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final SecurityUtils securityUtils;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AppUserRepository appUserRepository,
            SecurityUtils securityUtils
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
        this.securityUtils = securityUtils;
    }

    public AuthApiModels.LoginResponse login(AuthApiModels.LoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        AppUserEntity user = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        AuthenticatedUser principal = new AuthenticatedUser(user);

        return new AuthApiModels.LoginResponse(
                jwtService.generateToken(principal),
                "Bearer",
                toProfile(user)
        );
    }

    @Transactional(readOnly = true)
    public AuthApiModels.UserProfileResponse me() {
        String username = securityUtils.currentUsername();
        AppUserEntity user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toProfile(user);
    }

    public AuthApiModels.UserProfileResponse toProfile(AppUserEntity user) {
        return new AuthApiModels.UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.isActive(),
                user.getRoles().stream().map(role -> role.getCode()).sorted().toList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
