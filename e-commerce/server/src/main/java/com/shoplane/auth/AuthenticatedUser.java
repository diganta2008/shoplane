package com.shoplane.auth;

/**
 * Value object injected as the {@code Principal} into the Spring Security
 * context by {@link JwtAuthFilter}. Controllers reach for it via
 * {@link org.springframework.security.core.annotation.AuthenticationPrincipal}.
 */
public record AuthenticatedUser(Long id, String email) {}
