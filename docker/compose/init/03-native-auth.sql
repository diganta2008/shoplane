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
