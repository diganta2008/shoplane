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
INSERT INTO coupons (code, description, discount_type, value, min_subtotal, is_active, valid_from, valid_until) VALUES
  ('WELCOME10', '10% off welcome coupon',            'percent', 10.00,   0.00, 1, NULL,         NULL),
  ('SAVE20',    '20% loyalty coupon',                'percent', 20.00, 100.00, 1, NULL,         NULL),
  ('FREESHIP',  'Free shipping',                     'flat',     9.99,   0.00, 1, NULL,         NULL),
  ('AZADI15',   '15% off (Independence Day)',        'percent', 15.00,   0.00, 1, '2026-08-01', '2026-08-20'),
  ('AZADI25',   '25% off over $200 (Independence Day)','percent',25.00, 200.00, 1, '2026-08-01', '2026-08-20')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- ---------- A demo user ------------------------------------------
-- password_hash is a bcrypt of "test1234" — safe placeholder.
INSERT INTO users (email, password_hash, full_name, phone) VALUES
  ('automation@example.com',
   '$2a$10$Nq9zJqRZ8n5g0zJ0mQ5rY.oXW3nZQm3xNQq0FZk8Xt0Z9wG7fQ2mK',
   'Automation Tester', '+91 9000000000')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);
