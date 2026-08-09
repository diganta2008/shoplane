package com.shoplane.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Type-safe binding for all `shoplane.*` properties in application.yml.
 * See src/main/resources/application.yml for defaults and env-var mapping.
 */
@ConfigurationProperties(prefix = "shoplane")
public record ShopLaneProperties(Jwt jwt, Cors cors, OpenAi openai) {

    public record Jwt(
            String issuer,
            String accessSecret,
            String refreshSecret,
            long accessTtlMinutes,
            long refreshTtlDays
    ) {}

    public record Cors(List<String> allowedOrigins) {}

    /**
     * OpenAI-compatible chat completion config. Set apiKey via OPENAI_API_KEY env var.
     * Leaving apiKey blank disables /api/v1/chat with a 503 CHAT_DISABLED error.
     */
    public record OpenAi(
            String apiKey,
            String baseUrl,
            String model,
            double temperature,
            int maxTokens,
            int timeoutSeconds,
            String systemPrompt
    ) {}
}
