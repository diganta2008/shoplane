package com.shoplane.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shoplane.user.UserProfile;

import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileDto(
        String firstName,
        String lastName,
        String displayName,
        LocalDate dateOfBirth,
        String gender,
        String avatarUrl,
        String bio,
        String preferredLanguage,
        String preferredCurrency,
        String timezone,
        Boolean marketingOptIn,
        Boolean newsletterOptIn,
        Boolean smsOptIn,
        Instant emailVerifiedAt,
        Integer loyaltyPoints,
        String loyaltyTier) {

    public static UserProfileDto from(UserProfile p) {
        return new UserProfileDto(
                p.getFirstName(), p.getLastName(), p.getDisplayName(),
                p.getDateOfBirth(), p.getGender(), p.getAvatarUrl(), p.getBio(),
                p.getPreferredLanguage(), p.getPreferredCurrency(), p.getTimezone(),
                p.getMarketingOptIn(), p.getNewsletterOptIn(), p.getSmsOptIn(),
                p.getEmailVerifiedAt(), p.getLoyaltyPoints(), p.getLoyaltyTier());
    }
}
