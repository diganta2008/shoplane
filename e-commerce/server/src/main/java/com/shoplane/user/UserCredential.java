package com.shoplane.user;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_credentials")
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false) private Long userId;

    @Column(name = "credential_type", nullable = false, length = 30) private String credentialType;
    @Column(nullable = false, length = 190)                          private String identifier;

    @Column(name = "secret_hash", length = 255) private String secretHash;
    @Column(name = "hash_algo",   length = 20)  private String hashAlgo;
    @Column(length = 64)                        private String salt;

    @Column(columnDefinition = "json") private String meta;

    @Column(name = "is_active",  nullable = false) private Boolean active = true;
    @Column(name = "is_primary", nullable = false) private Boolean primary = false;
    @Column(name = "failed_attempts", nullable = false) private Integer failedAttempts = 0;
    @Column(name = "locked_until") private Instant lockedUntil;

    @Column(name = "last_used_at") private Instant lastUsedAt;
    @Column(name = "last_used_ip", length = 45)  private String lastUsedIp;
    @Column(name = "last_used_ua", length = 255) private String lastUsedUa;

    @Column(name = "expires_at") private Instant expiresAt;
    @Column(name = "created_at", insertable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false) private Instant updatedAt;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }         public void setUserId(Long v) { this.userId = v; }
    public String getCredentialType() { return credentialType; } public void setCredentialType(String v) { this.credentialType = v; }
    public String getIdentifier() { return identifier; } public void setIdentifier(String v) { this.identifier = v; }
    public String getSecretHash() { return secretHash; } public void setSecretHash(String v) { this.secretHash = v; }
    public String getHashAlgo() { return hashAlgo; }     public void setHashAlgo(String v) { this.hashAlgo = v; }
    public String getSalt() { return salt; }             public void setSalt(String v) { this.salt = v; }
    public String getMeta() { return meta; }             public void setMeta(String v) { this.meta = v; }
    public Boolean getActive() { return active; }        public void setActive(Boolean v) { this.active = v; }
    public Boolean getPrimary() { return primary; }      public void setPrimary(Boolean v) { this.primary = v; }
    public Integer getFailedAttempts() { return failedAttempts; } public void setFailedAttempts(Integer v) { this.failedAttempts = v; }
    public Instant getLockedUntil() { return lockedUntil; } public void setLockedUntil(Instant v) { this.lockedUntil = v; }
    public Instant getLastUsedAt() { return lastUsedAt; } public void setLastUsedAt(Instant v) { this.lastUsedAt = v; }
    public String getLastUsedIp() { return lastUsedIp; } public void setLastUsedIp(String v) { this.lastUsedIp = v; }
    public String getLastUsedUa() { return lastUsedUa; } public void setLastUsedUa(String v) { this.lastUsedUa = v; }
    public Instant getExpiresAt() { return expiresAt; }  public void setExpiresAt(Instant v) { this.expiresAt = v; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
