-- ------------------------------------------------------------------
-- 01-schema.sql — ShopLane demo schema
-- Executed automatically the FIRST time the container starts on a
-- fresh volume. To re-run, wipe the volume:
--   docker compose down -v && docker compose up -d
-- ------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS shoplane
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE shoplane;

-- ---------- Users -------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email          VARCHAR(190)    NOT NULL UNIQUE,
  password_hash  VARCHAR(255)    NOT NULL,
  full_name      VARCHAR(120)    NOT NULL,
  phone          VARCHAR(32)     NULL,
  is_active      TINYINT(1)      NOT NULL DEFAULT 1,
  created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX ix_users_email (email)
) ENGINE=InnoDB;

-- ---------- Catalog -----------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
  id         SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  slug       VARCHAR(64)  NOT NULL UNIQUE,
  name       VARCHAR(120) NOT NULL,
  description VARCHAR(255) NULL,
  sort_order  SMALLINT    NOT NULL DEFAULT 100
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sku           VARCHAR(40)  NOT NULL UNIQUE,
  name          VARCHAR(200) NOT NULL,
  category_id   SMALLINT UNSIGNED NOT NULL,
  price         DECIMAL(10, 2) NOT NULL,
  old_price     DECIMAL(10, 2) NULL,
  rating        DECIMAL(3, 2)  NOT NULL DEFAULT 0,
  review_count  INT UNSIGNED   NOT NULL DEFAULT 0,
  stock         INT            NOT NULL DEFAULT 0,
  image_url     VARCHAR(255)   NULL,
  description   TEXT           NULL,
  is_active     TINYINT(1)     NOT NULL DEFAULT 1,
  created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
  INDEX ix_products_category (category_id),
  INDEX ix_products_price    (price)
) ENGINE=InnoDB;

-- ---------- Cart --------------------------------------------------
CREATE TABLE IF NOT EXISTS carts (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT UNSIGNED NULL,
  session_id  VARCHAR(64)     NULL,
  created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX ix_carts_user    (user_id),
  INDEX ix_carts_session (session_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cart_items (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cart_id    BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  qty        SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  size       VARCHAR(32) NULL,
  color      VARCHAR(32) NULL,
  added_at   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ci_cart    FOREIGN KEY (cart_id)    REFERENCES carts(id)    ON DELETE CASCADE,
  CONSTRAINT fk_ci_product FOREIGN KEY (product_id) REFERENCES products(id),
  UNIQUE KEY uq_cart_line (cart_id, product_id, size, color)
) ENGINE=InnoDB;

-- ---------- Orders ------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
  id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_number      VARCHAR(32)     NOT NULL UNIQUE,
  user_id           BIGINT UNSIGNED NULL,
  status            ENUM('pending','confirmed','shipped','delivered','cancelled','refunded') NOT NULL DEFAULT 'pending',
  subtotal          DECIMAL(10, 2) NOT NULL,
  discount          DECIMAL(10, 2) NOT NULL DEFAULT 0,
  shipping          DECIMAL(10, 2) NOT NULL DEFAULT 0,
  tax               DECIMAL(10, 2) NOT NULL DEFAULT 0,
  total             DECIMAL(10, 2) NOT NULL,
  coupon_code       VARCHAR(32) NULL,
  payment_method    ENUM('card','upi','cod') NOT NULL,
  ship_name         VARCHAR(120) NOT NULL,
  ship_email        VARCHAR(190) NOT NULL,
  ship_phone        VARCHAR(32)  NOT NULL,
  ship_address      VARCHAR(255) NOT NULL,
  ship_city         VARCHAR(80)  NOT NULL,
  ship_state        VARCHAR(80)  NOT NULL,
  ship_zip          VARCHAR(20)  NOT NULL,
  ship_country      VARCHAR(4)   NOT NULL,
  placed_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX ix_orders_user    (user_id),
  INDEX ix_orders_status  (status),
  INDEX ix_orders_placed  (placed_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id    BIGINT UNSIGNED NOT NULL,
  product_id  BIGINT UNSIGNED NOT NULL,
  name        VARCHAR(200) NOT NULL,          -- captured at order time
  price       DECIMAL(10, 2) NOT NULL,
  qty         SMALLINT UNSIGNED NOT NULL,
  size        VARCHAR(32) NULL,
  color       VARCHAR(32) NULL,
  CONSTRAINT fk_oi_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
  CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id),
  INDEX ix_oi_order (order_id)
) ENGINE=InnoDB;

-- ---------- Wishlist ---------------------------------------------
CREATE TABLE IF NOT EXISTS wishlists (
  user_id     BIGINT UNSIGNED NOT NULL,
  product_id  BIGINT UNSIGNED NOT NULL,
  added_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, product_id),
  CONSTRAINT fk_wl_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
  CONSTRAINT fk_wl_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Coupons ----------------------------------------------
CREATE TABLE IF NOT EXISTS coupons (
  code         VARCHAR(32) NOT NULL PRIMARY KEY,
  description  VARCHAR(120) NOT NULL,
  discount_type ENUM('percent','flat') NOT NULL,
  value        DECIMAL(10, 2) NOT NULL,
  min_subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0,
  is_active    TINYINT(1) NOT NULL DEFAULT 1,
  valid_from   DATE NULL,
  valid_until  DATE NULL
) ENGINE=InnoDB;
