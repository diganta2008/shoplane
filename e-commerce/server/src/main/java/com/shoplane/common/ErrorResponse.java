package com.shoplane.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Uniform error envelope. Mirrors the shape used by the Node middleware
 * so clients see identical JSON no matter which tier answered.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(Error error) {

    public static ErrorResponse of(String code, String message, Object details, String requestId) {
        return new ErrorResponse(new Error(code, message, details, requestId));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Error(String code, String message, Object details, String requestId) {}
}
