package com.enterprise.ulos.los.security;

import com.enterprise.ulos.los.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService appUserDetailsService;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtService jwtService, AppUserDetailsService appUserDetailsService, AuthService authService) {
        this.jwtService = jwtService;
        this.appUserDetailsService = appUserDetailsService;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Primary auth mode for LOS frontend: session token in X-Auth-Token.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String xAuthToken = request.getHeader("X-Auth-Token");
            if (xAuthToken != null && !xAuthToken.isBlank()) {
                try {
                    RequestUserPrincipal principal = authService.resolvePrincipal(xAuthToken.trim());
                    List<GrantedAuthority> authorities = principal.roles().stream()
                            .map(String::toUpperCase)
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .map(GrantedAuthority.class::cast)
                            .toList();

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal.username(),
                            null,
                            authorities
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (ResponseStatusException ignored) {
                    // Invalid/expired X-Auth-Token: keep anonymous and let security chain respond consistently.
                }
            }
        }

        // Optional JWT Bearer auth support (legacy/integration clients).
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = appUserDetailsService.loadUserByUsername(username);
            if (userDetails instanceof AuthenticatedUser authenticatedUser && jwtService.isTokenValid(token, authenticatedUser)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        authenticatedUser.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
