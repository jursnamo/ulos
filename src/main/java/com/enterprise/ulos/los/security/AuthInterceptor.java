package com.enterprise.ulos.los.security;

import com.enterprise.ulos.los.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/system/routes",
            "/api/master/portfolio-lookups",
            "/api/public/editor-images"
    );

    private final AuthService authService;
    private final RequestAuthContext requestAuthContext;

    public AuthInterceptor(AuthService authService, RequestAuthContext requestAuthContext) {
        this.authService = authService;
        this.requestAuthContext = requestAuthContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) {
            return true;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return true;
        }

        String token = resolveToken(request);
        RequestUserPrincipal principal = authService.resolvePrincipal(token);
        requestAuthContext.set(principal);

        if (handler instanceof HandlerMethod handlerMethod) {
            RequiresRoles requiresRoles = resolveAnnotation(handlerMethod);
            if (requiresRoles != null) {
                List<String> expected = Arrays.stream(requiresRoles.value())
                        .map(role -> role == null ? "" : role.trim().toUpperCase())
                        .filter(role -> !role.isBlank())
                        .toList();
                if (!principal.hasAnyRole(expected)) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN,
                            "Insufficient role to access this endpoint"
                    );
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        requestAuthContext.clear();
    }

    private RequiresRoles resolveAnnotation(HandlerMethod handlerMethod) {
        RequiresRoles methodAnnotation = handlerMethod.getMethodAnnotation(RequiresRoles.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return handlerMethod.getBeanType().getAnnotation(RequiresRoles.class);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("X-Auth-Token");
        if (token != null && !token.isBlank()) {
            return token;
        }
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        return null;
    }
}
