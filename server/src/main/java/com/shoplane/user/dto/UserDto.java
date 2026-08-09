package com.shoplane.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shoplane.user.User;
import com.shoplane.user.UserProfile;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(
        Long id,
        String email,
        String fullName,
        String phone,
        Instant createdAt,
        UserProfileDto profile) {

    public static UserDto from(User u, UserProfile p) {
        return new UserDto(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(),
                u.getCreatedAt(), p == null ? null : UserProfileDto.from(p));
    }
}
