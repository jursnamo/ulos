package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.UserManagementApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.los.service.UserManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiresRoles({"ADMIN"})
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public List<UserManagementApiModels.UserResponse> list() {
        return userManagementService.list();
    }

    @PostMapping
    public UserManagementApiModels.UserResponse create(@RequestBody UserManagementApiModels.UserUpsertRequest request) {
        return userManagementService.create(request);
    }

    @PutMapping("/{userId}")
    public UserManagementApiModels.UserResponse update(
            @PathVariable Long userId,
            @RequestBody UserManagementApiModels.UserUpsertRequest request
    ) {
        return userManagementService.update(userId, request);
    }

    @PutMapping("/{userId}/password")
    public UserManagementApiModels.UserResponse resetPassword(
            @PathVariable Long userId,
            @RequestBody UserManagementApiModels.PasswordResetRequest request
    ) {
        return userManagementService.resetPassword(userId, request);
    }
}
