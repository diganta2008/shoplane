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
