package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.service.UserManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public List<AuthApiModels.UserProfileResponse> listUsers() {
        return userManagementService.listUsers();
    }

    @GetMapping("/roles")
    public List<AuthApiModels.RoleResponse> listRoles() {
        return userManagementService.listRoles();
    }

    @PostMapping
    public AuthApiModels.UserProfileResponse createUser(@RequestBody AuthApiModels.UserRequest request) {
        return userManagementService.saveUser(null, request);
    }

    @PutMapping("/{userId}")
    public AuthApiModels.UserProfileResponse updateUser(@PathVariable Long userId, @RequestBody AuthApiModels.UserRequest request) {
        return userManagementService.saveUser(userId, request);
    }
}
