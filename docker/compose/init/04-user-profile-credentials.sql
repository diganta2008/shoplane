-- ------------------------------------------------------------------
-- 04-user-profile-credentials.sql
--
-- Adds extended profile + credential storage for the ShopLane demo:
--
--   * user_profiles     - 1:1 extension of `users` with personal
--                         details, preferences, avatar, marketing
--                         opt-ins, etc.
--
--   * user_credentials  - multi-type credential store per user.
--                         One row per credential (password hash,
--                         OAuth link, MFA/TOTP secret, recovery code,
--                         API key, ...). Lets a user have multiple
--                         login methods and supports rotation without
--                         touching the primary `users` row.
--
-- Runs automatically after 03-native-auth.sql on FIRST boot. Safe to
-- run again against an existing DB because every statement uses
-- IF NOT EXISTS / ON DUPLICATE KEY UPDATE.
-- ------------------------------------------------------------------

USE shoplane;

-- =====================================================================
-- user_profiles - one row per user, extended personal information
-- =====================================================================
CREATE TABLE IF NOT EXISTS user_profiles (
  user_id            BIGINT UNSIGNED NOT NULL PRIMARY KEY,
  first_name         VARCHAR(80)     NULL,
  last_name          VARCHAR(80)     NULL,
  display_name       VARCHAR(120)    NULL,
  date_of_birth      DATE            NULL,
  gender             ENUM('male','female','other','prefer_not_to_say') NULL,
  avatar_url         VARCHAR(255)    NULL,
  bio                VARCHAR(500)    NULL,

  -- Locale / preferences
  preferred_language VARCHAR(10)     NOT NULL DEFAULT 'en',
  preferred_currency VARCHAR(4)      NOT NULL DEFAULT 'INR',
  timezone           VARCHAR(64)     NOT NULL DEFAULT 'Asia/Kolkata',

  -- Marketing / comms
  marketing_opt_in   TINYINT(1)      NOT NULL DEFAULT 0,
  newsletter_opt_in  TINYINT(1)      NOT NULL DEFAULT 0,
  sms_opt_in         TINYINT(1)      NOT NULL DEFAULT 0,

  -- Verification flags
  email_verified_at  TIMESTAMP       NULL DEFAULT NULL,
  phone_verified_at  TIMESTAMP       NULL DEFAULT NULL,

  -- Loyalty / gamification
  loyalty_points     INT UNSIGNED    NOT NULL DEFAULT 0,
  loyalty_tier       ENUM('bronze','silver','gold','platinum') NOT NULL DEFAULT 'bronze',

  created_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_profile_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

  INDEX ix_profile_tier (loyalty_tier)
) ENGINE=InnoDB;

-- =====================================================================
-- user_credentials - one row per credential, per user
-- Supports: password, OAuth (Google/GitHub/Facebook/Apple),
-- MFA TOTP secret, one-time recovery codes, API keys.
-- =====================================================================
CREATE TABLE IF NOT EXISTS user_credentials (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT UNSIGNED NOT NULL,

  credential_type ENUM(
    'password',
    'oauth_google',
    'oauth_github',
    'oauth_facebook',
    'oauth_apple',
    'mfa_totp',
    'recovery_code',
    'api_key'
  ) NOT NULL,

  -- Public identifier for the credential:
  --   * password       -> the email (or username) used at login
  --   * oauth_*        -> provider subject / provider user id
  --   * mfa_totp       -> device label ("iPhone 15", "Authy")
  --   * recovery_code  -> code prefix (first 4 chars) for display
  --   * api_key        -> key prefix (first 8 chars) for display
  identifier      VARCHAR(190)    NOT NULL,

  -- Hashed / encrypted secret. Never store plaintext.
  --   * password       -> bcrypt/argon2 hash
  --   * oauth_*        -> hashed refresh token (optional)
  --   * mfa_totp       -> encrypted TOTP shared secret
  --   * recovery_code  -> hash of the code
  --   * api_key        -> hash of the key
  secret_hash     VARCHAR(255)    NULL,

  -- Hash algorithm for `secret_hash` (bcrypt, argon2id, sha256, ...)
  hash_algo       VARCHAR(20)     NULL,

  -- Optional random salt (some algos embed salt in the hash itself).
  salt            VARCHAR(64)     NULL,

  -- Provider / device / channel specific JSON blob.
  -- Examples:
  --   { "provider": "google", "email": "x@gmail.com", "picture": "..." }
  --   { "totp_digits": 6, "totp_period": 30, "issuer": "ShopLane" }
  --   { "scopes": ["read:orders","write:cart"] }
  meta            JSON            NULL,

  is_active       TINYINT(1)      NOT NULL DEFAULT 1,
  is_primary      TINYINT(1)      NOT NULL DEFAULT 0,
  failed_attempts SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  locked_until    TIMESTAMP       NULL DEFAULT NULL,

  last_used_at    TIMESTAMP       NULL DEFAULT NULL,
  last_used_ip    VARCHAR(45)     NULL,
  last_used_ua    VARCHAR(255)    NULL,

  expires_at      TIMESTAMP       NULL DEFAULT NULL,
  created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_cred_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

  -- A user can have only one credential of a given type/identifier
  -- (e.g. only one Google account linked, only one API-key prefix).
  UNIQUE KEY uq_user_cred (user_id, credential_type, identifier),

  INDEX ix_cred_user_type (user_id, credential_type),
  INDEX ix_cred_active    (is_active),
  INDEX ix_cred_expires   (expires_at)
) ENGINE=InnoDB;

-- =====================================================================
-- Back-fill: create a profile + password credential row for every
-- existing user (idempotent via INSERT IGNORE).
-- =====================================================================
INSERT IGNORE INTO user_profiles (user_id, first_name, last_name, display_name)
SELECT
  u.id,
  SUBSTRING_INDEX(u.full_name, ' ', 1)                       AS first_name,
  NULLIF(TRIM(SUBSTRING(u.full_name,
                        LOCATE(' ', u.full_name) + 1)), '')  AS last_name,
  u.full_name                                                AS display_name
FROM users u;

INSERT IGNORE INTO user_credentials
  (user_id, credential_type, identifier, secret_hash, hash_algo, is_primary, is_active)
SELECT
  u.id,
  'password',
  u.email,
  u.password_hash,
  'bcrypt',
  1,
  u.is_active
FROM users u
WHERE u.password_hash IS NOT NULL;

-- =====================================================================
-- Demo data for the seeded automation user
-- =====================================================================
UPDATE user_profiles p
  JOIN users u ON u.id = p.user_id
  SET p.first_name       = 'Automation',
      p.last_name        = 'Tester',
      p.display_name     = 'Automation Tester',
      p.gender           = 'prefer_not_to_say',
      p.preferred_language = 'en',
      p.preferred_currency = 'INR',
      p.timezone         = 'Asia/Kolkata',
      p.marketing_opt_in = 1,
      p.newsletter_opt_in = 1,
      p.email_verified_at = CURRENT_TIMESTAMP,
      p.loyalty_points   = 1500,
      p.loyalty_tier     = 'silver'
WHERE u.email = 'automation@example.com';
