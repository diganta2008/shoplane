package com.shoplane.user;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name",  length = 80)  private String firstName;
    @Column(name = "last_name",   length = 80)  private String lastName;
    @Column(name = "display_name",length = 120) private String displayName;

    @Column(name = "date_of_birth") private LocalDate dateOfBirth;

    @Column(length = 20) private String gender;

    @Column(name = "avatar_url", length = 255) private String avatarUrl;
    @Column(length = 500)                       private String bio;

    @Column(name = "preferred_language", length = 10) private String preferredLanguage;
    @Column(name = "preferred_currency", length = 4)  private String preferredCurrency;
    @Column(length = 64)                              private String timezone;

    @Column(name = "marketing_opt_in")  private Boolean marketingOptIn;
    @Column(name = "newsletter_opt_in") private Boolean newsletterOptIn;
    @Column(name = "sms_opt_in")        private Boolean smsOptIn;

    @Column(name = "email_verified_at") private Instant emailVerifiedAt;
    @Column(name = "phone_verified_at") private Instant phoneVerifiedAt;

    @Column(name = "loyalty_points")     private Integer loyaltyPoints;
    @Column(name = "loyalty_tier", length = 20) private String loyaltyTier;

    @Column(name = "created_at", insertable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false) private Instant updatedAt;

    // getters / setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }        public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }          public void setLastName(String v) { this.lastName = v; }
    public String getDisplayName() { return displayName; }    public void setDisplayName(String v) { this.displayName = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(LocalDate v) { this.dateOfBirth = v; }
    public String getGender() { return gender; }              public void setGender(String v) { this.gender = v; }
    public String getAvatarUrl() { return avatarUrl; }        public void setAvatarUrl(String v) { this.avatarUrl = v; }
    public String getBio() { return bio; }                    public void setBio(String v) { this.bio = v; }
    public String getPreferredLanguage() { return preferredLanguage; } public void setPreferredLanguage(String v) { this.preferredLanguage = v; }
    public String getPreferredCurrency() { return preferredCurrency; } public void setPreferredCurrency(String v) { this.preferredCurrency = v; }
    public String getTimezone() { return timezone; }          public void setTimezone(String v) { this.timezone = v; }
    public Boolean getMarketingOptIn() { return marketingOptIn; }   public void setMarketingOptIn(Boolean v) { this.marketingOptIn = v; }
    public Boolean getNewsletterOptIn() { return newsletterOptIn; } public void setNewsletterOptIn(Boolean v) { this.newsletterOptIn = v; }
    public Boolean getSmsOptIn() { return smsOptIn; }               public void setSmsOptIn(Boolean v) { this.smsOptIn = v; }
    public Instant getEmailVerifiedAt() { return emailVerifiedAt; } public void setEmailVerifiedAt(Instant v) { this.emailVerifiedAt = v; }
    public Instant getPhoneVerifiedAt() { return phoneVerifiedAt; } public void setPhoneVerifiedAt(Instant v) { this.phoneVerifiedAt = v; }
    public Integer getLoyaltyPoints() { return loyaltyPoints; }     public void setLoyaltyPoints(Integer v) { this.loyaltyPoints = v; }
    public String getLoyaltyTier() { return loyaltyTier; }          public void setLoyaltyTier(String v) { this.loyaltyTier = v; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
