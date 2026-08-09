package com.shoplane.chat.dto;

/**
 * Result of a /api/v1/chat call — the assistant reply and rough token stats.
 * {@code model} is echoed back for observability.
 */
public record ChatResponse(String reply, String model, TokenUsage usage) {

    public record TokenUsage(int prompt, int completion, int total) {}
}
