package com.shoplane.user;

import com.shoplane.common.ApiException;
import com.shoplane.user.dto.ChangePasswordRequest;
import com.shoplane.user.dto.UpdateProfileRequest;
import com.shoplane.user.dto.UserProfileDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ProfileService {

    private final UserRepository users;
    private final UserProfileRepository profiles;
    private final UserCredentialRepository credentials;
    private final PasswordEncoder encoder;

    public ProfileService(UserRepository users,
                          UserProfileRepository profiles,
                          UserCredentialRepository credentials,
                          PasswordEncoder encoder) {
        this.users = users;
        this.profiles = profiles;
        this.credentials = credentials;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public UserProfileDto get(Long userId) {
        return profiles.findById(userId)
                .map(UserProfileDto::from)
                .orElseThrow(() -> ApiException.notFound("Profile not found"));
    }

    @Transactional
    public UserProfileDto update(Long userId, UpdateProfileRequest req) {
        UserProfile p = profiles.findById(userId).orElseGet(() -> {
            UserProfile fresh = new UserProfile();
            fresh.setUserId(userId);
            return fresh;
        });

        if (req.firstName()         != null) p.setFirstName(req.firstName());
        if (req.lastName()          != null) p.setLastName(req.lastName());
        if (req.displayName()       != null) p.setDisplayName(req.displayName());
        if (req.dateOfBirth()       != null) p.setDateOfBirth(LocalDate.parse(req.dateOfBirth()));
        if (req.gender()            != null) p.setGender(req.gender());
        if (req.avatarUrl()         != null) p.setAvatarUrl(req.avatarUrl());
        if (req.bio()               != null) p.setBio(req.bio());
        if (req.preferredLanguage() != null) p.setPreferredLanguage(req.preferredLanguage());
        if (req.preferredCurrency() != null) p.setPreferredCurrency(req.preferredCurrency());
        if (req.timezone()          != null) p.setTimezone(req.timezone());
        if (req.marketingOptIn()    != null) p.setMarketingOptIn(req.marketingOptIn());
        if (req.newsletterOptIn()   != null) p.setNewsletterOptIn(req.newsletterOptIn());
        if (req.smsOptIn()          != null) p.setSmsOptIn(req.smsOptIn());

        return UserProfileDto.from(profiles.save(p));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        User u = users.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
        if (!encoder.matches(req.currentPassword(), u.getPasswordHash())) {
            throw ApiException.unauthorized("Current password is incorrect");
        }
        String hash = encoder.encode(req.newPassword());
        u.setPasswordHash(hash);
        users.save(u);

        credentials.findFirstByUserIdAndCredentialTypeAndPrimaryTrue(userId, "password")
                .ifPresent(c -> {
                    c.setSecretHash(hash);
                    c.setFailedAttempts(0);
                    credentials.save(c);
                });
    }
}
