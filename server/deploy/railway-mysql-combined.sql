-- ==================================================================
-- railway-mysql-combined.sql  (AUTO-GENERATED)
-- Concatenation of docker/init/*.sql in numeric order.
-- Paste this ENTIRE file into Railway MySQL Data -> Query editor.
-- Safe to re-run (all statements are idempotent).
-- ==================================================================


-- ---------- 01-schema.sql ----------

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


-- ---------- 02-seed.sql ----------

-- ------------------------------------------------------------------
-- 02-seed.sql — sample data matching the ShopLane demo store.
-- Runs automatically on FIRST container start (after 01-schema.sql).
-- ------------------------------------------------------------------

USE shoplane;

-- ---------- Categories -------------------------------------------
INSERT INTO categories (id, slug, name, description, sort_order) VALUES
  (1, 'electronics', 'Electronics', 'Tech that keeps up with you',        10),
  (2, 'fashion',     'Fashion',     'Wardrobe essentials, elevated',      20),
  (3, 'home',        'Home',        'Beautiful spaces, thoughtful things',30),
  (4, 'books',       'Books',       'Read, learn, escape',                40)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------- Products ---------------------------------------------
INSERT INTO products (sku, name, category_id, price, old_price, rating, review_count, stock, image_url, description) VALUES
  ('AUD-101', 'Aurora Wireless Headphones',        1,  89.99, 129.99, 4.70, 214, 42,  'images/product-headphones.svg',      'Studio-grade wireless over-ear headphones with ANC and 40 hour battery life.'),
  ('WCH-202', 'Nova Smart Watch Series 8',         1, 149.99, 199.99, 4.60, 388, 27,  'images/product-smartwatch.svg',      'Track workouts and sleep on a bright always-on AMOLED display.'),
  ('SPK-303', 'Boom Bluetooth Speaker',            1,  59.99,   NULL, 4.40, 156, 88,  'images/product-speaker.svg',         '30 W of stereo output, 24 hour battery, IPX7 waterproof.'),
  ('JKT-404', 'Ranger Leather Jacket',             2, 199.99, 259.00, 4.80,  97, 14,  'images/product-leather-jacket.svg',  'Full-grain cowhide leather, hand-stitched, brass hardware.'),
  ('SHO-505', 'AirRunner Pro Running Shoes',       2,  79.99,  99.99, 4.50, 512, 63,  'images/product-running-shoes.svg',   'Lightweight cushioned midsole with breathable knit upper.'),
  ('ACC-606', 'Solstice UV400 Sunglasses',         2,  39.99,  59.99, 4.30,  71, 132, 'images/product-sunglasses.svg',      'Polarised UV400 lenses in a lightweight polymer frame.'),
  ('HOM-707', 'Barista Pro Coffee Maker',          3, 129.99,   NULL, 4.60, 189, 21,  'images/product-coffee-maker.svg',    '15-bar Italian pump with integrated milk frother.'),
  ('HOM-808', 'Halo Ambient Table Lamp',           3,  45.99,  59.99, 4.50,  64, 40,  'images/product-table-lamp.svg',      '2700 K LED bulb in a hand-thrown ceramic base with touch dim.'),
  ('HOM-909', 'Master Chef Knives Set (6-pc)',     3,  89.99, 119.99, 4.70, 231, 17,  'images/product-knives-set.svg',      'German high-carbon stainless steel with pakkawood handles.'),
  ('BOK-010', 'JavaScript — The Complete Guide',   4,  34.99,  49.99, 4.90, 812, 200, 'images/product-js-book.svg',         'Definitive reference for modern JavaScript, ES2015 → ES2025.'),
  ('BOK-011', 'Design Handbook 2026',              4,  29.99,   NULL, 4.70, 143, 92,  'images/product-design-book.svg',     'A visual playbook of typographic systems and layout patterns.'),
  ('BOK-012', 'Timeless Novels Box Set (4 vols)',  4,  24.99,  39.99, 4.60,  78, 34,  'images/product-novel-set.svg',       'A curated box set of four unabridged classics.'),
  ('ELE-013', 'Apple iPhone 16 Pro',               1,1099.00,1199.00, 4.80,1420, 45,  'images/product-iphone.svg',          'Titanium body, 6.3-inch ProMotion OLED, A18 Pro chip, redesigned pro camera system with 5x tetraprism telephoto.'),
  ('ELE-014', 'Samsung Galaxy S25 Ultra',          1,1299.00,1399.00, 4.70, 987, 32,  'images/product-galaxy.svg',          '6.9-inch QHD+ Dynamic AMOLED, built-in S Pen, 200 MP main camera, Snapdragon 8 Elite for Galaxy.'),
  ('ELE-015', 'Apple iPad Pro 13" (M5)',           1, 899.00, 999.00, 4.90, 612, 40,  'images/product-ipad.svg',            'The thinnest iPad ever, powered by M5. Ultra Retina XDR tandem-OLED display, Apple Pencil Pro ready.'),
  ('ELE-016', 'Apple MacBook Air 15" (M4)',        1,1349.00,1499.00, 4.80, 730, 22,  'images/product-macbook.svg',         'Silent, fanless 15-inch MacBook Air with the M4 chip, up to 20 hours battery life, and a 1.51 kg body.'),
  ('ELE-017', 'Google Pixel 10 Pro',               1, 999.00,   NULL, 4.60, 421, 55,  'images/product-pixel.svg',           'Tensor G5 chip with on-device Gemini Nano. 6.7-inch LTPO OLED and a triple 5x telephoto camera system.'),
  ('ELE-018', 'Dell XPS 15 (2026)',                1,1699.00,1899.00, 4.50, 289, 15,  'images/product-laptop.svg',          '15.6-inch 3.5K OLED touch display, Intel Core Ultra 9 with NPU, RTX 4070 dGPU, 1 TB NVMe SSD.'),
  ('ELE-019', 'Nintendo Switch 2 OLED',            1, 449.00,   NULL, 4.70,1834, 88,  'images/product-switch.svg',          '8-inch OLED, DLSS-powered 4K docked output, magnetic Joy-Con 2 controllers. Backwards compatible.'),
  ('ELE-020', 'Sony PlayStation 5 Slim',           1, 499.00, 549.00, 4.80,2103, 40,  'images/product-ps5.svg',             'PS5 Slim: 30 percent smaller, detachable UHD Blu-ray drive, 1 TB SSD, 4K ray-traced gaming.'),
  -- ---- Electronics fillers (21-24) -------------------------------
  ('ELE-021', 'Sonic AirPods Pro 3',                1, 249.00, 279.00, 4.70, 612, 74,  'images/product-earbuds.svg',        'True wireless earbuds with adaptive audio, personalised spatial sound, 6 h + 30 h with MagSafe USB-C case.'),
  ('ELE-022', 'Lumen 4K Studio Webcam',             1, 189.00, 219.00, 4.50, 243, 52,  'images/product-webcam.svg',         '4K UHD webcam with AI framing, dual beamforming mics, magnetic privacy shutter.'),
  ('ELE-023', 'MeshNet Wi-Fi 7 Router (3-pack)',    1, 599.00, 699.00, 4.60, 187, 30,  'images/product-router.svg',         'Whole-home Wi-Fi 7 mesh, tri-band, up to 21 Gbps, covers 8,000 sq ft.'),
  ('ELE-024', 'VoltEdge 32" 4K Gaming Monitor',     1, 649.00, 799.00, 4.70, 356, 18,  'images/product-monitor.svg',        '32-inch 4K IPS 165 Hz, 1 ms GtG, HDR600, KVM and 90 W USB-C passthrough.'),
  -- ---- Fashion fillers (25-36) ----------------------------------
  ('FSH-025', 'Slim-fit Denim Jeans',               2,  59.99,  79.99, 4.50, 421, 210, 'images/product-jeans.svg',          'Mid-rise stretch denim with a slim tapered leg. Everyday wearable.'),
  ('FSH-026', 'Merino Wool Crew Sweater',           2,  89.99, 119.00, 4.70, 178, 68,  'images/product-sweater.svg',        'Ultra-soft 18.5-micron machine-washable merino, layering-friendly.'),
  ('FSH-027', 'Chelsea Leather Boots',              2, 149.99, 189.00, 4.60, 234, 48,  'images/product-boots.svg',          'Full-grain calf leather chelsea boots with Goodyear-welted rubber sole.'),
  ('FSH-028', 'Classic Baseball Cap',               2,  24.99,  34.99, 4.40, 512, 320, 'images/product-cap.svg',            'Six-panel cotton twill cap with adjustable brass buckle strap.'),
  ('FSH-029', 'Everyday Cotton T-shirt (5-pack)',   2,  45.00,  60.00, 4.50, 903, 480, 'images/product-jeans.svg',          'Pack of 5 heavyweight, pre-shrunk, side-seamed crew tees.'),
  ('FSH-030', 'Cashmere Turtleneck',                2, 179.00, 229.00, 4.80, 132, 26,  'images/product-sweater.svg',        'Grade-A Mongolian cashmere, feather-light and ultra warm.'),
  ('FSH-031', 'White Sneakers Court Low',           2,  74.99,  99.99, 4.50, 588, 145, 'images/product-boots.svg',          'Minimal leather court sneakers with vulcanised rubber outsole.'),
  ('FSH-032', 'Wide-brim Wool Fedora',              2,  69.00,  89.00, 4.60,  84, 40,  'images/product-cap.svg',            '100% wool felt fedora with grosgrain band and 7 cm brim.'),
  ('FSH-033', 'Vintage Wash Denim Jacket',          2,  89.99, 119.99, 4.40, 267, 72,  'images/product-jeans.svg',          'Selvedge denim jacket with a stone-washed finish and chest flap pockets.'),
  ('FSH-034', 'Puffer Vest Recycled Down',          2, 129.00, 159.00, 4.50, 145, 36,  'images/product-sweater.svg',        'Featherweight puffer vest, 700-fill recycled down, packs into own pocket.'),
  ('FSH-035', 'Silk Twill Neck Scarf',              2,  49.00,  69.00, 4.60,  92, 88,  'images/product-cap.svg',            'Hand-rolled 100% silk twill scarf, screen-printed in Como, Italy.'),
  ('FSH-036', 'Yoga Leggings High-rise',            2,  54.99,  79.99, 4.70, 719, 264, 'images/product-jeans.svg',          'Buttery-soft four-way stretch high-rise leggings with waistband pocket.'),
  -- ---- Home fillers (37-48) -------------------------------------
  ('HOM-037', 'AtmoFresh HEPA Air Purifier',        3, 249.00, 299.00, 4.70, 421, 44,  'images/product-air-purifier.svg',   'True-HEPA H13 air purifier, 400 m3/h CADR, whisper quiet at 24 dB.'),
  ('HOM-038', 'CleanBot X1 Robot Vacuum',           3, 429.00, 549.00, 4.60, 852, 22,  'images/product-vacuum.svg',         'LiDAR-mapped robot vacuum-mop combo, 5,200 Pa suction, auto-empty base.'),
  ('HOM-039', 'ForgeIron 12" Skillet',              3,  79.99,  99.99, 4.80, 611, 130, 'images/product-skillet.svg',        'Pre-seasoned cast iron skillet, oven-safe to 260 degC, lifetime warranty.'),
  ('HOM-040', 'Whirlwind 1500W Blender Pro',        3, 189.00, 229.00, 4.50, 297, 48,  'images/product-blender.svg',        '1,500 W blender with 6-point steel blade and 8 preset programs.'),
  ('HOM-041', 'CozyBlend Bedsheet Set 400TC',       3,  79.99, 109.00, 4.50, 348, 210, 'images/product-air-purifier.svg',   '400-TC sateen cotton bedsheet set, OEKO-TEX certified, 40 cm deep pocket.'),
  ('HOM-042', 'Mistwood Aroma Diffuser',            3,  44.99,  59.99, 4.40, 176, 132, 'images/product-air-purifier.svg',   'Ultrasonic essential-oil diffuser with 7-colour ambient light, 300 ml.'),
  ('HOM-043', 'Sunrise Wall Clock',                 3,  34.99,  49.99, 4.30,  89, 75,  'images/product-vacuum.svg',         'Silent-sweep wall clock with a solid walnut frame, 30 cm diameter.'),
  ('HOM-044', 'Egyptian Cotton Bath Towel Set',     3,  59.99,  79.99, 4.60, 421, 168, 'images/product-air-purifier.svg',   'Plush 600 gsm Egyptian cotton towel set (6-piece).'),
  ('HOM-045', 'HandThrown Ceramic Dinner Set 16pc', 3, 129.00, 169.00, 4.70, 156, 34,  'images/product-skillet.svg',        'Reactive-glaze stoneware dinner set, service for four, oven safe.'),
  ('HOM-046', 'WeavRug 5x8 Handloom',               3, 199.00, 279.00, 4.50,  63, 22,  'images/product-vacuum.svg',         'Hand-loomed 100% wool 5x8 ft rug, low-pile geometric pattern, reversible.'),
  ('HOM-047', 'StackBins Storage Set (6)',          3,  49.99,  69.99, 4.40, 214, 190, 'images/product-blender.svg',        'Stackable, snap-lid ventilated storage bins (set of 6, 12 L each).'),
  ('HOM-048', 'PrecisionKitchen Digital Scale',     3,  24.99,  34.99, 4.60, 508, 260, 'images/product-skillet.svg',        'Slim digital kitchen scale, 5 kg capacity, +/- 1 g accuracy, backlit LCD.'),
  -- ---- Books fillers (49-60) ------------------------------------
  ('BOK-049', 'Atomic Habits',                      4,  18.99,  26.99, 4.80,12045, 500, 'images/product-book-yellow.svg',   'Proven framework for building good habits and breaking bad ones.'),
  ('BOK-050', 'The Great Gatsby (Classic Edition)', 4,  12.99,  16.99, 4.70, 6321, 420, 'images/product-book-blue.svg',     'Fitzgerald Jazz Age masterpiece, cloth-bound classic edition.'),
  ('BOK-051', 'Sapiens: A Brief History of Humankind', 4, 22.99, 29.99, 4.60, 8974, 305, 'images/product-book-yellow.svg', 'Harari sweeping tour of the entire arc of human history.'),
  ('BOK-052', 'Zero to One',                        4,  19.99,  27.00, 4.50, 4210, 220, 'images/product-book-red.svg',      'Peter Thiel notes on startups and how to build the future.'),
  ('BOK-053', 'Clean Code',                         4,  34.99,  44.99, 4.70, 5104, 180, 'images/product-book-green.svg',    'A handbook of agile software craftsmanship by Robert C. Martin.'),
  ('BOK-054', 'Deep Work',                          4,  17.99,  24.99, 4.60, 3892, 260, 'images/product-book-yellow.svg',   'Cal Newport rules for focused success in a distracted world.'),
  ('BOK-055', 'Educated: A Memoir',                 4,  16.99,  22.99, 4.70, 6543, 190, 'images/product-book-red.svg',      'Tara Westover memoir on learning her way out of an isolated upbringing.'),
  ('BOK-056', 'The Pragmatic Programmer 20th Anniv',4,  39.99,  49.99, 4.80, 3287, 145, 'images/product-book-green.svg',    'Journey to mastery, modernised edition of the classic guide.'),
  ('BOK-057', 'Cosmos',                             4,  21.99,  29.99, 4.80, 4126, 132, 'images/product-book-blue.svg',     'Carl Sagan lyrical tour of the universe, richly illustrated.'),
  ('BOK-058', 'Grit',                               4,  16.49,  22.99, 4.50, 2941, 210, 'images/product-book-red.svg',      'Angela Duckworth on how passion and perseverance beat raw talent.'),
  ('BOK-059', 'The Alchemist',                      4,  14.99,  19.99, 4.70, 9812, 480, 'images/product-book-yellow.svg',   'Paulo Coelho modern parable of dreams, destiny and courage.'),
  ('BOK-060', 'Steal Like an Artist',               4,  13.99,  18.99, 4.60, 3542, 340, 'images/product-book-blue.svg',     'Austin Kleon 10 things nobody told you about being creative.')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------- Coupons ----------------------------------------------
INSERT INTO coupons (code, description, discount_type, value, min_subtotal, is_active) VALUES
  ('WELCOME10', '10% off welcome coupon',   'percent', 10.00,   0.00, 1),
  ('SAVE20',    '20% loyalty coupon',       'percent', 20.00, 100.00, 1),
  ('FREESHIP',  'Free shipping',            'flat',     9.99,   0.00, 1)
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- ---------- A demo user ------------------------------------------
-- password_hash is a bcrypt of "test1234" — safe placeholder.
INSERT INTO users (email, password_hash, full_name, phone) VALUES
  ('automation@example.com',
   '$2a$10$Nq9zJqRZ8n5g0zJ0mQ5rY.oXW3nZQm3xNQq0FZk8Xt0Z9wG7fQ2mK',
   'Automation Tester', '+91 9000000000')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);


-- ---------- 03-native-auth.sql ----------

-- ------------------------------------------------------------------
-- 03-native-auth.sql
--
-- Switches root and the application user to `mysql_native_password`
-- so that older / GUI clients (MySQL Workbench, DBeaver, JDBC without
-- allowPublicKeyRetrieval, PHP-PDO on old distros, …) can connect out
-- of the box without hitting:
--
--     "Public Key Retrieval is not allowed"
--
-- The compose file already passes `--mysql-native-password=ON`, which
-- is required in MySQL 8.4 (plugin ships disabled).
--
-- This script runs AFTER 01-schema.sql and 02-seed.sql on the FIRST
-- boot only. To re-run:  docker compose down -v && docker compose up -d
-- ------------------------------------------------------------------

-- Root — accessible via any host inside the compose network
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root_change_me';

-- Application user (created via MYSQL_USER / MYSQL_PASSWORD env vars)
ALTER USER 'shoplane'@'%' IDENTIFIED WITH mysql_native_password BY 'shoplane_change_me';

FLUSH PRIVILEGES;


-- ---------- 04-user-profile-credentials.sql ----------

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


-- ---------- 05-addresses.sql ----------

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


-- ---------- 06-subcategory.sql ----------

-- ------------------------------------------------------------------
-- 06-subcategory.sql — adds a free-form sub_category on products.
-- Idempotent: safe to re-run against an existing container.
-- ------------------------------------------------------------------

USE shoplane;

-- Add column if not already present -------------------------------
SET @has_col := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'shoplane'
    AND TABLE_NAME   = 'products'
    AND COLUMN_NAME  = 'sub_category');

SET @stmt := IF(@has_col = 0,
  'ALTER TABLE products ADD COLUMN sub_category VARCHAR(80) NULL AFTER category_id, ADD INDEX ix_products_subcategory (sub_category)',
  'SELECT "sub_category already present"');

PREPARE s FROM @stmt;
EXECUTE s;
DEALLOCATE PREPARE s;


-- ---------- 07-subcategory-backfill.sql ----------

-- ------------------------------------------------------------------
-- 07-subcategory-backfill.sql
--   * Backfills sub_category for every seeded product (SKU-keyed so
--     it is stable across reseeds and idempotent).
--   * Inserts the "wave 2" products (SKU 061-085) that bring each
--     category into the requested 15-25 range.
--   * Safe to run repeatedly against an existing container.
-- ------------------------------------------------------------------

USE shoplane;

-- ---------- Wave 2 products (61-85) ------------------------------
INSERT INTO products (sku, name, category_id, sub_category, price, old_price, rating, review_count, stock, image_url, description) VALUES
  -- Electronics fillers (61-63)
  ('ELE-061', 'HoloLens Air VR Headset',             1, 'VR & AR',      599.00, 699.00, 4.40, 189, 24,  'images/product-vr.svg',            'Standalone VR/AR headset with 4K per-eye micro-OLED, inside-out tracking, hand + eye gestures. Under 400 g.'),
  ('ELE-062', 'Kindle Scribe 2 (16 GB)',              1, 'E-Readers',    339.00, 379.00, 4.50, 421, 66,  'images/product-ipad.svg',          '10.2-inch 300 ppi Paperwhite e-reader with included Premium Pen and up to 12 weeks battery.'),
  ('ELE-063', 'Sony WH-1000XM6 Wireless Headphones',  1, 'Audio',        429.00, 499.00, 4.80, 934, 52,  'images/product-headphones.svg',    'Industry-leading ANC over-ear headphones with dual QN2 processors and 40 h battery.'),
  -- Fashion fillers (64-70)
  ('FSH-064', 'Wool Overcoat Camel',                  2, 'Outerwear',    289.00, 359.00, 4.70, 152, 24,  'images/product-leather-jacket.svg','Single-breasted knee-length overcoat in an Italian wool-cashmere blend.'),
  ('FSH-065', 'Two-ply Cotton Dress Shirt',           2, 'Formalwear',    79.00,  99.00, 4.60, 421, 168, 'images/product-shirt.svg',         'Two-ply 100/2 poplin dress shirt with mother-of-pearl buttons.'),
  ('FSH-066', 'Stretch Chino Trousers',               2, 'Trousers',      54.99,  79.99, 4.50, 519, 240, 'images/product-jeans.svg',         'All-day comfort stretch chinos with a slim tapered leg.'),
  ('FSH-067', 'Full-grain Leather Belt',              2, 'Accessories',   49.00,  69.00, 4.70, 302, 220, 'images/product-boots.svg',         '3.5 mm veg-tanned full-grain leather belt with solid brass buckle.'),
  ('FSH-068', 'Woven Silk Necktie',                   2, 'Formalwear',    39.00,  55.00, 4.50, 128, 140, 'images/product-cap.svg',           'Hand-tipped Italian woven-silk necktie, 60 gm silk, 8 cm blade.'),
  ('FSH-069', 'Arctic Down Parka',                    2, 'Outerwear',    349.00, 449.00, 4.80, 231, 34,  'images/product-sweater.svg',       '800-fill responsible down parka with taped critical seams and faux fur trim.'),
  ('FSH-070', 'Performance Training Shorts',          2, 'Activewear',    34.99,  49.99, 4.60, 612, 380, 'images/product-jeans.svg',         'Featherweight 4-way stretch training shorts with hidden zip pocket.'),
  -- Home fillers (71-75)
  ('HOM-071', 'BrewMaster Espresso Machine',          3, 'Appliances',   549.00, 649.00, 4.60, 289, 26,  'images/product-coffee-maker.svg',  'Single-boiler espresso machine with PID temperature control and integrated conical burr grinder.'),
  ('HOM-072', 'CozyCotton Weighted Blanket',          3, 'Bedding',      129.00, 169.00, 4.70, 812, 84,  'images/product-air-purifier.svg',  '100% cotton weighted blanket with glass-bead fill and minky plush removable cover.'),
  ('HOM-073', 'Beam LED Floor Lamp',                  3, 'Lighting',      89.99, 119.99, 4.40, 154, 46,  'images/product-table-lamp.svg',    'Tall arc floor lamp, dimmable warm 3000 K LED, marble base.'),
  ('HOM-074', 'Handcrafted Ceramic Vase Set (3)',     3, 'Decor',         79.99, 109.00, 4.50,  96, 62,  'images/product-vase.svg',          'Set of three hand-thrown stoneware vases in coordinated matte glazes.'),
  ('HOM-075', 'Bamboo Cutting Board Set',             3, 'Kitchen',       29.99,  44.99, 4.60, 421, 320, 'images/product-knives-set.svg',    'Set of three FSC-certified bamboo cutting boards with juice grooves.'),
  -- Books fillers (76-85)
  ('BOK-076', 'The Hobbit',                           4, 'Fiction',       15.99,  21.99, 4.90,14203, 520,'images/product-book-green.svg',    'Tolkien beloved fantasy adventure, illustrated 75th anniversary edition.'),
  ('BOK-077', 'Rich Dad Poor Dad',                    4, 'Business',      15.99,  21.99, 4.50, 9876, 420,'images/product-book-yellow.svg',   'Kiyosaki classic on money mindset and financial literacy.'),
  ('BOK-078', 'Thinking, Fast and Slow',              4, 'Psychology',    19.99,  27.99, 4.60, 7423, 260,'images/product-book-blue.svg',     'Daniel Kahneman on the two systems that drive the way we think.'),
  ('BOK-079', 'To Kill a Mockingbird',                4, 'Fiction',       13.99,  18.99, 4.80,12045, 340,'images/product-book-red.svg',      'Harper Lee Pulitzer Prize-winning novel of moral courage.'),
  ('BOK-080', "Man's Search for Meaning",             4, 'Memoir',        14.49,  19.99, 4.80, 8934, 290,'images/product-book-red.svg',      'Viktor Frankl Holocaust-survivor memoir and foundational text of logotherapy.'),
  ('BOK-081', 'Meditations',                          4, 'Philosophy',    11.99,  16.99, 4.70, 5602, 380,'images/product-book-green.svg',    'Marcus Aurelius private notebook, an enduring Stoic guide.'),
  ('BOK-082', 'The Lean Startup',                     4, 'Business',      18.99,  25.99, 4.50, 4123, 220,'images/product-book-yellow.svg',   'Eric Ries on build-measure-learn and continuous innovation.'),
  ('BOK-083', 'Refactoring (2nd Edition)',            4, 'Programming',   42.99,  54.99, 4.80, 3210, 150,'images/product-book-green.svg',    'Martin Fowler definitive catalogue of code-smells and refactorings.'),
  ('BOK-084', 'Homo Deus: A Brief History of Tomorrow',4,'Science',       21.99,  28.99, 4.50, 4890, 210,'images/product-book-blue.svg',     'Harari sequel to Sapiens, imagining where humanity might be heading.'),
  ('BOK-085', 'Ikigai',                               4, 'Self-help',     13.49,  18.99, 4.60, 6712, 400,'images/product-book-yellow.svg',   'The Japanese secret to a long and happy life.')
ON DUPLICATE KEY UPDATE
  sub_category = VALUES(sub_category),
  name         = VALUES(name);

-- ---------- Backfill sub_category for existing SKUs (1-60) -------
UPDATE products SET sub_category = 'Audio'        WHERE sku = 'AUD-101';
UPDATE products SET sub_category = 'Wearables'    WHERE sku = 'WCH-202';
UPDATE products SET sub_category = 'Audio'        WHERE sku = 'SPK-303';
UPDATE products SET sub_category = 'Outerwear'    WHERE sku = 'JKT-404';
UPDATE products SET sub_category = 'Footwear'     WHERE sku = 'SHO-505';
UPDATE products SET sub_category = 'Accessories'  WHERE sku = 'ACC-606';
UPDATE products SET sub_category = 'Kitchen'      WHERE sku = 'HOM-707';
UPDATE products SET sub_category = 'Lighting'     WHERE sku = 'HOM-808';
UPDATE products SET sub_category = 'Kitchen'      WHERE sku = 'HOM-909';
UPDATE products SET sub_category = 'Programming'  WHERE sku = 'BOK-010';
UPDATE products SET sub_category = 'Design'       WHERE sku = 'BOK-011';
UPDATE products SET sub_category = 'Fiction'      WHERE sku = 'BOK-012';
UPDATE products SET sub_category = 'Smartphones'  WHERE sku = 'ELE-013';
UPDATE products SET sub_category = 'Smartphones'  WHERE sku = 'ELE-014';
UPDATE products SET sub_category = 'Tablets'      WHERE sku = 'ELE-015';
UPDATE products SET sub_category = 'Laptops'      WHERE sku = 'ELE-016';
UPDATE products SET sub_category = 'Smartphones'  WHERE sku = 'ELE-017';
UPDATE products SET sub_category = 'Laptops'      WHERE sku = 'ELE-018';
UPDATE products SET sub_category = 'Gaming'       WHERE sku = 'ELE-019';
UPDATE products SET sub_category = 'Gaming'       WHERE sku = 'ELE-020';
UPDATE products SET sub_category = 'Audio'        WHERE sku = 'ELE-021';
UPDATE products SET sub_category = 'Cameras'      WHERE sku = 'ELE-022';
UPDATE products SET sub_category = 'Networking'   WHERE sku = 'ELE-023';
UPDATE products SET sub_category = 'Displays'     WHERE sku = 'ELE-024';
UPDATE products SET sub_category = 'Denim'        WHERE sku = 'FSH-025';
UPDATE products SET sub_category = 'Knitwear'     WHERE sku = 'FSH-026';
UPDATE products SET sub_category = 'Footwear'     WHERE sku = 'FSH-027';
UPDATE products SET sub_category = 'Headwear'     WHERE sku = 'FSH-028';
UPDATE products SET sub_category = 'Basics'       WHERE sku = 'FSH-029';
UPDATE products SET sub_category = 'Knitwear'     WHERE sku = 'FSH-030';
UPDATE products SET sub_category = 'Footwear'     WHERE sku = 'FSH-031';
UPDATE products SET sub_category = 'Headwear'     WHERE sku = 'FSH-032';
UPDATE products SET sub_category = 'Outerwear'    WHERE sku = 'FSH-033';
UPDATE products SET sub_category = 'Outerwear'    WHERE sku = 'FSH-034';
UPDATE products SET sub_category = 'Accessories'  WHERE sku = 'FSH-035';
UPDATE products SET sub_category = 'Activewear'   WHERE sku = 'FSH-036';
UPDATE products SET sub_category = 'Wellness'     WHERE sku = 'HOM-037';
UPDATE products SET sub_category = 'Appliances'   WHERE sku = 'HOM-038';
UPDATE products SET sub_category = 'Kitchen'      WHERE sku = 'HOM-039';
UPDATE products SET sub_category = 'Kitchen'      WHERE sku = 'HOM-040';
UPDATE products SET sub_category = 'Bedding'      WHERE sku = 'HOM-041';
UPDATE products SET sub_category = 'Wellness'     WHERE sku = 'HOM-042';
UPDATE products SET sub_category = 'Decor'        WHERE sku = 'HOM-043';
UPDATE products SET sub_category = 'Bath'         WHERE sku = 'HOM-044';
UPDATE products SET sub_category = 'Kitchen'      WHERE sku = 'HOM-045';
UPDATE products SET sub_category = 'Decor'        WHERE sku = 'HOM-046';
UPDATE products SET sub_category = 'Storage'      WHERE sku = 'HOM-047';
UPDATE products SET sub_category = 'Kitchen'      WHERE sku = 'HOM-048';
UPDATE products SET sub_category = 'Self-help'    WHERE sku = 'BOK-049';
UPDATE products SET sub_category = 'Fiction'      WHERE sku = 'BOK-050';
UPDATE products SET sub_category = 'History'      WHERE sku = 'BOK-051';
UPDATE products SET sub_category = 'Business'     WHERE sku = 'BOK-052';
UPDATE products SET sub_category = 'Programming'  WHERE sku = 'BOK-053';
UPDATE products SET sub_category = 'Self-help'    WHERE sku = 'BOK-054';
UPDATE products SET sub_category = 'Memoir'       WHERE sku = 'BOK-055';
UPDATE products SET sub_category = 'Programming'  WHERE sku = 'BOK-056';
UPDATE products SET sub_category = 'Science'      WHERE sku = 'BOK-057';
UPDATE products SET sub_category = 'Self-help'    WHERE sku = 'BOK-058';
UPDATE products SET sub_category = 'Fiction'      WHERE sku = 'BOK-059';
UPDATE products SET sub_category = 'Design'       WHERE sku = 'BOK-060';


-- ---------- 08-medical.sql ----------

-- ------------------------------------------------------------------
-- 08-medical.sql - adds the "Medical" category (id=5) and the 15
-- instruments listed on the Healthcare page. Idempotent.
-- ------------------------------------------------------------------

USE shoplane;

INSERT INTO categories (id, slug, name, description, sort_order) VALUES
  (5, 'medical', 'Medical', 'Instruments for home & clinic use', 50)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO products (sku, name, category_id, sub_category, price, old_price, rating, review_count, stock, image_url, description) VALUES
  -- Diagnostic
  ('MED-086', 'CardioPro Cardiology Stethoscope',   5, 'Diagnostic',      189.00, 239.00, 4.80, 421, 42,  'images/product-stethoscope.svg', 'Dual-lumen cardiology stethoscope with a hand-polished stainless chest piece.'),
  ('MED-087', 'Otoscope + Ophthalmoscope Set',       5, 'Diagnostic',      249.00, 299.00, 4.60, 132, 22,  'images/product-stethoscope.svg', 'Fiber-optic otoscope and ophthalmoscope diagnostic set with rechargeable handle.'),
  ('MED-088', 'Reflex Hammer (Buck Model)',          5, 'Diagnostic',       19.99,  29.99, 4.50, 214, 180, 'images/product-stethoscope.svg', 'Buck-style reflex hammer with soft rubber head and chrome-plated handle.'),
  ('MED-089', 'Diagnostic Penlight (5-pack)',        5, 'Diagnostic',       14.99,  24.99, 4.40, 302, 320, 'images/product-stethoscope.svg', 'Pack of 5 pupil-gauge diagnostic penlights, cool white LED.'),
  ('MED-090', 'Tuning Fork Set (C128, C512)',        5, 'Diagnostic',       39.99,  54.99, 4.60,  96, 88,  'images/product-stethoscope.svg', 'Aluminium tuning forks 128 Hz and 512 Hz in a felt-lined case.'),
  -- Monitoring
  ('MED-091', 'HomePulse Digital BP Monitor',        5, 'Monitoring',       89.00, 119.00, 4.70, 812, 132, 'images/product-bp-monitor.svg',  'Clinically validated upper-arm BP monitor with irregular-heartbeat detection.'),
  ('MED-092', 'PulseOx Fingertip Pulse Oximeter',    5, 'Monitoring',       34.99,  49.99, 4.60, 921, 260, 'images/product-bp-monitor.svg',  'Fingertip pulse oximeter with OLED display and plethysmograph waveform.'),
  ('MED-093', 'ThermScan Infrared Thermometer',      5, 'Monitoring',       44.99,  59.99, 4.50, 512, 210, 'images/product-bp-monitor.svg',  'Non-contact infrared forehead thermometer, 1-second reading, fever alarm.'),
  ('MED-094', 'GlucoCare Glucometer Starter Kit',    5, 'Monitoring',       39.99,  54.99, 4.50, 623, 168, 'images/product-bp-monitor.svg',  'Blood glucose meter with 50 test strips, 50 lancets and Bluetooth sync.'),
  ('MED-095', 'HeartTrack ECG Home Monitor',         5, 'Monitoring',      129.00, 179.00, 4.60, 289, 42,  'images/product-bp-monitor.svg',  'FDA-cleared single-lead ECG for at-home use with AI arrhythmia classifier.'),
  -- Aid & Mobility
  ('MED-096', 'FoldGo Lightweight Wheelchair',       5, 'Aid & Mobility',  349.00, 449.00, 4.50, 132, 22,  'images/product-first-aid.svg',   'Foldable lightweight aluminium wheelchair with dual brakes.'),
  ('MED-097', 'StrideEase Rollator Walker',          5, 'Aid & Mobility',  149.00, 199.00, 4.60, 214, 68,  'images/product-first-aid.svg',   'Four-wheel rollator walker with padded fold-down seat.'),
  ('MED-098', 'ReadyKit 150-piece First Aid Kit',    5, 'Aid & Mobility',   39.99,  59.99, 4.70, 921, 320, 'images/product-first-aid.svg',   'Compact 150-piece first aid kit in a soft-shell zip case.'),
  ('MED-099', 'AeroMist Ultrasonic Nebulizer',       5, 'Aid & Mobility',   79.99, 109.00, 4.40, 156, 84,  'images/product-first-aid.svg',   'Portable ultrasonic nebulizer with adult and paediatric masks.'),
  ('MED-100', 'ReliefWave TENS/EMS Unit',            5, 'Aid & Mobility',   59.99,  89.99, 4.50, 421, 132, 'images/product-first-aid.svg',   'Dual-channel TENS + EMS unit with 24 preset modes for pain relief.')
ON DUPLICATE KEY UPDATE
  sub_category = VALUES(sub_category),
  name         = VALUES(name);

