package com.shoplane.chat.dto;

import java.util.List;

/**
 * Result of a /api/v1/chat call — the assistant reply, rough token stats, and
 * up to a handful of follow-up prompts the UI can render as clickable chips.
 * {@code model} is echoed back for observability.
 */
public record ChatResponse(
        String reply,
        String model,
        TokenUsage usage,
        List<String> suggestions
) {

    public ChatResponse(String reply, String model, TokenUsage usage) {
        this(reply, model, usage, List.of());
    }

    public record TokenUsage(int prompt, int completion, int total) {}
}
