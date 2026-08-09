-- ------------------------------------------------------------------
-- 05-addresses.sql — user address book
-- Runs on a fresh volume automatically. Idempotent so it's safe to
-- re-apply against an existing database via:
--   Get-Content 05-addresses.sql | docker exec -i shoplane-mysql mysql -uroot -p...
-- ------------------------------------------------------------------
USE shoplane;

CREATE TABLE IF NOT EXISTS addresses (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id      BIGINT UNSIGNED NOT NULL,
  label        VARCHAR(40)  NULL,               -- 'Home', 'Office', 'Mom's place' etc.
  full_name    VARCHAR(120) NOT NULL,
  phone        VARCHAR(32)  NOT NULL,
  street       VARCHAR(255) NOT NULL,
  city         VARCHAR(80)  NOT NULL,
  state        VARCHAR(80)  NOT NULL,
  zip          VARCHAR(20)  NOT NULL,
  country      VARCHAR(4)   NOT NULL DEFAULT 'IN',
  is_default   TINYINT(1)   NOT NULL DEFAULT 0,
  created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX ix_addresses_user (user_id),
  INDEX ix_addresses_default (user_id, is_default)
) ENGINE=InnoDB;
