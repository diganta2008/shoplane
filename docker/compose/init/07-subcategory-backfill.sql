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
