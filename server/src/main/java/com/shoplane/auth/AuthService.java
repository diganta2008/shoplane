package com.shoplane.auth;

import com.shoplane.auth.dto.AuthResponse;
import com.shoplane.auth.dto.LoginRequest;
import com.shoplane.auth.dto.RegisterRequest;
import com.shoplane.auth.dto.TokenResponse;
import com.shoplane.common.ApiException;
import com.shoplane.user.User;
import com.shoplane.user.UserCredential;
import com.shoplane.user.UserCredentialRepository;
import com.shoplane.user.UserProfile;
import com.shoplane.user.UserProfileRepository;
import com.shoplane.user.UserRepository;
import com.shoplane.user.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository users;
    private final UserProfileRepository profiles;
    private final UserCredentialRepository credentials;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users,
                       UserProfileRepository profiles,
                       UserCredentialRepository credentials,
                       PasswordEncoder encoder,
                       JwtService jwt) {
        this.users = users;
        this.profiles = profiles;
        this.credentials = credentials;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            throw ApiException.conflict("An account with this email already exists");
        }

        String hash = encoder.encode(req.password());

        User u = new User();
        u.setEmail(req.email());
        u.setFullName(req.fullName());
        u.setPhone(req.phone());
        u.setPasswordHash(hash);
        u.setActive(true);
        u = users.save(u);

        UserProfile p = new UserProfile();
        p.setUserId(u.getId());
        String[] parts = req.fullName().trim().split("\\s+", 2);
        p.setFirstName(parts[0]);
        p.setLastName(parts.length > 1 ? parts[1] : null);
        p.setDisplayName(req.fullName());
        p.setPreferredLanguage("en");
        p.setPreferredCurrency("INR");
        p.setTimezone("Asia/Kolkata");
        p.setMarketingOptIn(false);
        p.setNewsletterOptIn(false);
        p.setSmsOptIn(false);
        p.setLoyaltyPoints(0);
        p.setLoyaltyTier("bronze");
        profiles.save(p);

        UserCredential c = new UserCredential();
        c.setUserId(u.getId());
        c.setCredentialType("password");
        c.setIdentifier(u.getEmail());
        c.setSecretHash(hash);
        c.setHashAlgo("bcrypt");
        c.setActive(true);
        c.setPrimary(true);
        credentials.save(c);

        String access  = jwt.signAccess(u.getId(), u.getEmail());
        String refresh = jwt.signRefresh(u.getId(), u.getEmail());
        return AuthResponse.of(UserDto.from(u, p), access, refresh);
    }

    @Transactional
    public AuthResponse login(LoginRequest req, String ip, String ua) {
        User u = users.findByEmail(req.email())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (Boolean.FALSE.equals(u.getActive())) {
            throw ApiException.unauthorized("Account is disabled");
        }
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            recordFailure(u.getId());
            throw ApiException.unauthorized("Invalid email or password");
        }
        touchLogin(u.getId(), ip, ua);

        UserProfile p = profiles.findById(u.getId()).orElse(null);
        String access  = jwt.signAccess(u.getId(), u.getEmail());
        String refresh = jwt.signRefresh(u.getId(), u.getEmail());
        return AuthResponse.of(UserDto.from(u, p), access, refresh);
    }

    @Transactional(readOnly = true)
    public UserDto me(Long userId) {
        User u = users.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
        UserProfile p = profiles.findById(userId).orElse(null);
        return UserDto.from(u, p);
    }

    public TokenResponse refresh(String refreshToken) {
        Claims claims;
        try {
            claims = jwt.parseRefresh(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw ApiException.unauthorized("Invalid or expired refresh token");
        }
        Long userId = Long.valueOf(claims.getSubject());
        User u = users.findById(userId)
                .orElseThrow(() -> ApiException.unauthorized("User no longer active"));
        String access  = jwt.signAccess(u.getId(), u.getEmail());
        String refresh = jwt.signRefresh(u.getId(), u.getEmail());
        return TokenResponse.of(access, refresh);
    }

    private void touchLogin(Long userId, String ip, String ua) {
        credentials.findFirstByUserIdAndCredentialTypeAndPrimaryTrue(userId, "password")
                .ifPresent(c -> {
                    c.setLastUsedAt(Instant.now());
                    c.setLastUsedIp(ip);
                    if (ua != null) c.setLastUsedUa(ua.length() > 255 ? ua.substring(0, 255) : ua);
                    c.setFailedAttempts(0);
                    credentials.save(c);
                });
    }

    private void recordFailure(Long userId) {
        credentials.findFirstByUserIdAndCredentialTypeAndPrimaryTrue(userId, "password")
                .ifPresent(c -> {
                    c.setFailedAttempts((c.getFailedAttempts() == null ? 0 : c.getFailedAttempts()) + 1);
                    credentials.save(c);
                });
    }
}
