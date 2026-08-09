-- Independence-Day promo coupons.
-- Run this against an already-seeded database (Railway, an older Docker volume,
-- or any environment where docker/init/02-seed.sql has already executed and
-- won't run again). Idempotent — safe to run more than once.

INSERT INTO coupons (code, description, discount_type, value, min_subtotal,
                     is_active, valid_from, valid_until)
VALUES
  ('AZADI15', '15% off (Independence Day)',           'percent', 15.00,   0.00, 1, '2026-08-01', '2026-08-20'),
  ('AZADI25', '25% off over $200 (Independence Day)', 'percent', 25.00, 200.00, 1, '2026-08-01', '2026-08-20')
ON DUPLICATE KEY UPDATE
  description  = VALUES(description),
  discount_type= VALUES(discount_type),
  value        = VALUES(value),
  min_subtotal = VALUES(min_subtotal),
  is_active    = 1,
  valid_from   = VALUES(valid_from),
  valid_until  = VALUES(valid_until);
