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
