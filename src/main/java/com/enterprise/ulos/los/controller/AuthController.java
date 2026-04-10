package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public AuthApiModels.UserProfileResponse me() {
        return authService.me();
    }
}
