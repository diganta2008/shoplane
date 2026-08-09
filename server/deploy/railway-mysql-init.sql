-- ------------------------------------------------------------------
-- railway-mysql-init.sql
-- Combined schema + seed script for a fresh Railway MySQL instance.
-- ------------------------------------------------------------------
-- HOW TO USE
--   1. Provision a MySQL plugin on Railway.
--   2. In the Railway MySQL panel, open "Data" -> "Query".
--   3. Copy the ENTIRE contents of this file into the SQL editor and Run.
--
-- The script sources every init/*.sql from docker/init in order.
-- ------------------------------------------------------------------

-- Just source the files inline. All originals are idempotent (CREATE
-- TABLE IF NOT EXISTS + ON DUPLICATE KEY UPDATE) so re-running is safe.

SOURCE ../../docker/init/01-schema.sql;
SOURCE ../../docker/init/02-seed.sql;
SOURCE ../../docker/init/03-native-auth.sql;
SOURCE ../../docker/init/04-user-profiles.sql;
SOURCE ../../docker/init/05-addresses.sql;
SOURCE ../../docker/init/06-subcategory.sql;
SOURCE ../../docker/init/07-subcategory-backfill.sql;
SOURCE ../../docker/init/08-medical.sql;

-- NOTE: Railway's web SQL editor does NOT support the `SOURCE` command;
-- it is a client-side directive of the mysql CLI. If you are using the
-- web editor, run the files individually in numeric order, OR use the
-- Railway CLI:
--
--     railway login
--     railway link <project-id>
--     railway run "cat server/deploy/railway-mysql-init.sql | mysql \$MYSQL_URL"
--
-- OR concatenate all the init files locally and paste the result:
--
--     Get-Content docker\init\*.sql -Raw | Set-Clipboard
--
-- Then paste into Railway's SQL editor.
