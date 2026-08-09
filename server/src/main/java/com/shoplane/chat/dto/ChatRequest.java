package com.shoplane.chat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Incoming payload for POST /api/v1/chat.
 *
 * @param message  The user's new message (validated, capped).
 * @param history  Optional prior turns (user + assistant only). Trimmed server-side
 *                 to the most recent turns to bound token cost.
 */
public record ChatRequest(
        @NotBlank
        @Size(max = 1000, message = "message must be at most 1000 characters")
        String message,

        @Valid
        @Size(max = 40, message = "history is capped at 40 messages")
        List<ChatMessage> history
) {}
