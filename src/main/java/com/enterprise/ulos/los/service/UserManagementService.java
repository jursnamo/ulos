package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.AppUserEntity;
import com.enterprise.ulos.los.entity.RoleEntity;
import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.repository.AppUserRepository;
import com.enterprise.ulos.los.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserManagementService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserManagementService(
            AppUserRepository appUserRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthService authService
    ) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<AuthApiModels.UserProfileResponse> listUsers() {
        return appUserRepository.findAll().stream()
                .map(authService::toProfile)
                .sorted((left, right) -> left.username().compareToIgnoreCase(right.username()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuthApiModels.RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new AuthApiModels.RoleResponse(
                        role.getId(),
                        role.getCode(),
                        role.getName(),
                        role.getDescription()
                ))
                .sorted((left, right) -> left.code().compareToIgnoreCase(right.code()))
                .toList();
    }

    public AuthApiModels.UserProfileResponse saveUser(Long userId, AuthApiModels.UserRequest request) {
        validateRequest(request, userId == null);

        AppUserEntity user = userId == null
                ? new AppUserEntity()
                : appUserRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userId == null && appUserRepository.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        user.setUsername(request.username());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setActive(request.active() == null || request.active());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        } else if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required for new user");
        }

        Set<RoleEntity> roles = new LinkedHashSet<>(roleRepository.findByCodeIn(defaultRoles(request.roleCodes())));
        if (roles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one valid role is required");
        }
        user.setRoles(roles);

        return authService.toProfile(appUserRepository.save(user));
    }

    private void validateRequest(AuthApiModels.UserRequest request, boolean creating) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User request is required");
        }
        if (request.username() == null || request.username().isBlank() || request.fullName() == null || request.fullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and full name are required");
        }
        if (creating && (request.password() == null || request.password().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
    }

    private List<String> defaultRoles(List<String> roleCodes) {
        return roleCodes == null || roleCodes.isEmpty() ? List.of("RM") : roleCodes;
    }
}
