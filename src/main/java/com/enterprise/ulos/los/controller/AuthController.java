package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthApiModels.LoginResponse login(@RequestBody AuthApiModels.LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthApiModels.UserProfileResponse me(
            @RequestHeader(value = "X-Auth-Token", required = false) String xAuthToken,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return authService.me(resolveToken(xAuthToken, authorization));
    }

    @PostMapping("/logout")
    public AuthApiModels.LogoutResponse logout(
            @RequestHeader(value = "X-Auth-Token", required = false) String xAuthToken,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return authService.logout(resolveToken(xAuthToken, authorization));
    }

    private String resolveToken(String xAuthToken, String authorization) {
        if (xAuthToken != null && !xAuthToken.isBlank()) {
            return xAuthToken.trim();
        }
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        return null;
    }
}
