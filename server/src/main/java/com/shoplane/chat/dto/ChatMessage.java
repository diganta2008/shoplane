package com.shoplane.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * One turn in a chat conversation, matching the OpenAI chat/completions wire format.
 * {@code role} must be one of "system", "user", or "assistant".
 */
public record ChatMessage(
        @NotBlank
        @Pattern(regexp = "system|user|assistant",
                 message = "role must be system, user, or assistant")
        String role,

        @NotBlank
        @Size(max = 4000)
        String content
) {}
