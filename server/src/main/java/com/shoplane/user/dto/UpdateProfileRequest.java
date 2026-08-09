package com.shoplane.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,
        @Size(max = 120) String displayName,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "dateOfBirth must be yyyy-MM-dd") String dateOfBirth,
        @Pattern(regexp = "^(male|female|other|prefer_not_to_say)$",
                message = "gender must be one of: male, female, other, prefer_not_to_say") String gender,
        @Size(max = 255) String avatarUrl,
        @Size(max = 500) String bio,
        @Size(max = 10) String preferredLanguage,
        @Size(max = 4)  String preferredCurrency,
        @Size(max = 64) String timezone,
        Boolean marketingOptIn,
        Boolean newsletterOptIn,
        Boolean smsOptIn) {}
