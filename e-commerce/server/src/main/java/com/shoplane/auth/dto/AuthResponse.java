package com.shoplane.auth.dto;

import com.shoplane.user.dto.UserDto;

public record AuthResponse(
        UserDto user,
        String accessToken,
        String refreshToken,
        String tokenType) {

    public static AuthResponse of(UserDto user, String access, String refresh) {
        return new AuthResponse(user, access, refresh, "Bearer");
    }
}
