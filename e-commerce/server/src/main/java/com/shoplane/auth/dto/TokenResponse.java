package com.shoplane.auth.dto;

public record TokenResponse(String accessToken, String refreshToken, String tokenType) {
    public static TokenResponse of(String access, String refresh) {
        return new TokenResponse(access, refresh, "Bearer");
    }
}
