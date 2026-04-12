package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.RoleEntity;
import com.enterprise.ulos.los.entity.UserEntity;
import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.model.UserManagementApiModels;
import com.enterprise.ulos.los.repository.RoleRepository;
import com.enterprise.ulos.los.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashService passwordHashService;

    public UserManagementService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordHashService passwordHashService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHashService = passwordHashService;
    }

    @Transactional(readOnly = true)
    public List<UserManagementApiModels.UserResponse> list() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(UserEntity::getUsername))
                .map(this::toResponse)
                .toList();
    }

    public UserManagementApiModels.UserResponse saveUser(Long userId, AuthApiModels.UserRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User payload is required");
        }
        UserManagementApiModels.UserUpsertRequest payload = new UserManagementApiModels.UserUpsertRequest(
                request.username(),
                request.fullName(),
                request.email(),
                request.password(),
                request.active(),
                request.roles()
        );
        if (userId == null) {
            return create(payload);
        }
        return update(userId, payload);
    }

    public UserManagementApiModels.UserResponse create(UserManagementApiModels.UserUpsertRequest request) {
        validateCreateRequest(request);
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByUsernameIgnoreCase(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists: " + username);
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email());
        user.setActiveFlag(request.active() == null || request.active());
        user.setPasswordHash(passwordHashService.hash(defaultPassword(request.password())));
        user.setRoles(resolveRoles(request.roles(), true));
        return toResponse(userRepository.save(user));
    }

    public UserManagementApiModels.UserResponse update(Long userId, UserManagementApiModels.UserUpsertRequest request) {
        UserEntity user = getEntity(userId);
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User payload is required");
        }
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }
        if (request.email() != null) {
            user.setEmail(request.email().isBlank() ? null : request.email().trim());
        }
        if (request.active() != null) {
            user.setActiveFlag(request.active());
        }
        if (request.roles() != null) {
            user.setRoles(resolveRoles(request.roles(), false));
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordHashService.hash(request.password().trim()));
        }
        return toResponse(userRepository.save(user));
    }

    public UserManagementApiModels.UserResponse resetPassword(Long userId, UserManagementApiModels.PasswordResetRequest request) {
        if (request == null || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required");
        }
        UserEntity user = getEntity(userId);
        user.setPasswordHash(passwordHashService.hash(request.password().trim()));
        return toResponse(userRepository.save(user));
    }

    private UserEntity getEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }

    private Set<RoleEntity> resolveRoles(List<String> roleCodes, boolean fallbackViewer) {
        Set<String> normalized = new LinkedHashSet<>();
        if (roleCodes != null) {
            roleCodes.stream()
                    .filter(item -> item != null && !item.isBlank())
                    .map(item -> item.trim().toUpperCase(Locale.ROOT))
                    .forEach(normalized::add);
        }
        if (normalized.isEmpty() && fallbackViewer) {
            normalized.add("VIEWER");
        }
        List<RoleEntity> resolved = roleRepository.findByRoleCodeInAndActiveFlagTrue(normalized);
        if (resolved.size() != normalized.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role code(s): " + normalized);
        }
        return new LinkedHashSet<>(resolved);
    }

    private void validateCreateRequest(UserManagementApiModels.UserUpsertRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User payload is required");
        }
        if (request.username() == null || request.username().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
        }
        if (request.fullName() == null || request.fullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName is required");
        }
    }

    private String defaultPassword(String requestedPassword) {
        if (requestedPassword != null && !requestedPassword.isBlank()) {
            return requestedPassword.trim();
        }
        return "Temp-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private UserManagementApiModels.UserResponse toResponse(UserEntity user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleCode().toUpperCase(Locale.ROOT))
                .sorted(Comparator.naturalOrder())
                .toList();
        return new UserManagementApiModels.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.isActiveFlag(),
                roles,
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
