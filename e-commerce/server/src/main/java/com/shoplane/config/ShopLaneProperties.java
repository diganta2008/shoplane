package com.shoplane.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Type-safe binding for all `shoplane.*` properties in application.yml.
 * See src/main/resources/application.yml for defaults and env-var mapping.
 */
@ConfigurationProperties(prefix = "shoplane")
public record ShopLaneProperties(Jwt jwt, Cors cors) {

    public record Jwt(
            String issuer,
            String accessSecret,
            String refreshSecret,
            long accessTtlMinutes,
            long refreshTtlDays
    ) {}

    public record Cors(List<String> allowedOrigins) {}
}
