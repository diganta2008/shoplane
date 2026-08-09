package com.shoplane.chat;

import com.shoplane.chat.dto.ChatMessage;
import com.shoplane.chat.dto.ChatResponse.TokenUsage;
import com.shoplane.common.ApiException;
import com.shoplane.config.ShopLaneProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Thin HTTP client for the OpenAI-compatible /chat/completions endpoint.
 *
 * Kept intentionally minimal: no streaming, no function calling, no retries.
 * Errors from OpenAI (401 auth, 429 rate-limit, 5xx upstream) are mapped to
 * ApiException so the GlobalExceptionHandler produces a normal error envelope.
 */
@Component
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final ShopLaneProperties.OpenAi cfg;
    private final RestClient client;

    public OpenAiClient(ShopLaneProperties props) {
        this.cfg = props.openai();

        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        rf.setReadTimeout((int) Duration.ofSeconds(cfg.timeoutSeconds()).toMillis());

        this.client = RestClient.builder()
                .baseUrl(cfg.baseUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(rf)
                .build();
    }

    /** Sends the full message list (including the system prompt) and returns the assistant reply. */
    public Completion complete(List<ChatMessage> messages) {
        if (cfg.apiKey() == null || cfg.apiKey().isBlank()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "CHAT_DISABLED",
                    "AI chat is not configured on this server (missing OPENAI_API_KEY).");
        }

        Map<String, Object> body = Map.of(
                "model",       cfg.model(),
                "temperature", cfg.temperature(),
                "max_tokens",  cfg.maxTokens(),
                "messages",    messages
        );

        Map<?, ?> response;
        try {
            response = client.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + cfg.apiKey())
                    .body(body)
                    .retrieve()
                    .onStatus(status -> status.value() == 401, (req, res) -> {
                        throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "CHAT_DISABLED",
                                "AI chat backend rejected the API key.");
                    })
                    .onStatus(status -> status.value() == 429, (req, res) -> {
                        throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "CHAT_RATE_LIMITED",
                                "AI chat is temporarily rate-limited. Try again in a moment.");
                    })
                    .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                        throw new ApiException(HttpStatus.BAD_GATEWAY, "CHAT_UPSTREAM_ERROR",
                                "AI chat backend is unavailable.");
                    })
                    .body(Map.class);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.warn("OpenAI request failed", e);
            throw new ApiException(HttpStatus.BAD_GATEWAY, "CHAT_UPSTREAM_ERROR",
                    "Could not reach the AI chat backend.");
        }

        return parse(response);
    }

    @SuppressWarnings("unchecked")
    private static Completion parse(Map<?, ?> response) {
        String reply = null;
        String model = null;
        TokenUsage usage = null;

        if (response != null) {
            Object choicesObj = response.get("choices");
            if (choicesObj instanceof List<?> choices && !choices.isEmpty()) {
                Object first = choices.get(0);
                if (first instanceof Map<?, ?> choice) {
                    Object messageObj = choice.get("message");
                    if (messageObj instanceof Map<?, ?> message) {
                        Object content = message.get("content");
                        if (content != null) reply = content.toString().trim();
                    }
                }
            }
            Object modelObj = response.get("model");
            if (modelObj != null) model = modelObj.toString();

            Object usageObj = response.get("usage");
            if (usageObj instanceof Map<?, ?> u) {
                usage = new TokenUsage(
                        intField(u, "prompt_tokens"),
                        intField(u, "completion_tokens"),
                        intField(u, "total_tokens"));
            }
        }

        if (reply == null || reply.isBlank()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "CHAT_UPSTREAM_ERROR",
                    "AI chat backend returned no content.");
        }
        return new Completion(reply, model, usage);
    }

    private static int intField(Map<?, ?> m, String key) {
        Object v = m.get(key);
        if (v instanceof Number n) return n.intValue();
        return 0;
    }

    /** Minimal internal projection of OpenAI's response. */
    public record Completion(String reply, String model, TokenUsage usage) {}
}
