# MySQL — Docker Compose stack

Local MySQL 8.4 for the ShopLane demo (and anything else you want to point at it). Ships with:

- **`mysql`** service (persistent volume, tuned my.cnf, healthcheck)
- **`phpmyadmin`** service on an optional `tools` profile
- Auto-executed **schema + seed** SQL on first boot
- Everything driven by a single `.env` file

## Layout

```
docker/compose/
├── docker-compose.yml
├── .env.example              # copy to .env before first run
├── .gitignore
├── conf.d/
│   └── custom.cnf            # my.cnf overrides (mounted read-only)
├── init/
│   ├── 01-schema.sql         # runs once on first boot
│   └── 02-seed.sql           # sample catalog matching e-commerce/
└── README.md
```

## Prerequisites

- Docker Desktop 4.x (Windows/macOS) or Docker Engine 24+ (Linux)
- Compose v2 (`docker compose` — no hyphen)

## First run

```powershell
cd c:\ssp\docker\compose

# 1. copy the env template
Copy-Item .env.example .env

# 2. edit .env — set MYSQL_ROOT_PASSWORD, MYSQL_PASSWORD etc.
notepad .env

# 3. start
docker compose up -d

# 4. tail logs until you see "ready for connections"
docker compose logs -f mysql
```

Everything in `init/` (schema + seed) is executed alphabetically on the **very first** container start. To re-run them, wipe the volume:

```powershell
docker compose down -v
docker compose up -d
```

## Connect

From your host machine (defaults from `.env.example`):

```
Host:      127.0.0.1
Port:      3306
Database:  shoplane
User:      shoplane        (or root)
Password:  <from .env>
```

CLI shell into the container:

```powershell
docker compose exec mysql mysql -uroot -p"$env:MYSQL_ROOT_PASSWORD" shoplane
```

Or as the app user:

```powershell
docker compose exec mysql mysql -ushoplane -pshoplane_change_me shoplane
```

## phpMyAdmin (optional GUI)

Enable the `tools` profile to bring up phpMyAdmin alongside MySQL:

```powershell
docker compose --profile tools up -d
```

Then open <http://localhost:8081> (change `PMA_PORT` in `.env` if 8081 is busy).

Stop just phpMyAdmin later:

```powershell
docker compose --profile tools stop phpmyadmin
```

## Common commands

| Task | Command |
|------|---------|
| Start / update      | `docker compose up -d` |
| Stop (keep data)    | `docker compose down` |
| Stop + drop volume  | `docker compose down -v` |
| Container status    | `docker compose ps` |
| Follow MySQL logs   | `docker compose logs -f mysql` |
| Restart after config edit | `docker compose restart mysql` |
| Backup DB           | `docker compose exec mysql mysqldump -uroot -p shoplane > backup.sql` |
| Restore DB          | `Get-Content backup.sql \| docker compose exec -T mysql mysql -uroot -p shoplane` |
| One-off SQL         | `docker compose exec mysql mysql -uroot -p -e "SHOW DATABASES;"` |

## What the seed gives you

- 4 categories, 12 products (matching the `e-commerce/` demo store's `data.js`)
- 3 coupons: `WELCOME10`, `SAVE20`, `FREESHIP`
- 1 test user: `automation@example.com` (bcrypt-hashed placeholder password)

Everything is idempotent — the seed uses `INSERT … ON DUPLICATE KEY UPDATE` so re-running it won't blow up.

## Ports & conflicts

If port 3306 is already in use on your host:

```powershell
# .env
MYSQL_PORT=13306
```

Then reconnect from clients on `127.0.0.1:13306`. Container-to-container traffic (e.g. phpMyAdmin) always uses the fixed internal port 3306.

## Data persistence

Data lives in the named volume `shoplane_mysql_data`. Inspect it:

```powershell
docker volume inspect shoplane_mysql_data
```

Nuke it (destructive — cannot be undone):

```powershell
docker compose down -v
```

## Troubleshooting

- **Healthcheck stays `starting`**: normal for the first ~30 s while MySQL initialises. If it doesn't turn `healthy` in 2 minutes, check `docker compose logs mysql` for auth / permission errors.
- **`Access denied for user 'shoplane'@'…'`**: the app credentials are ONLY created on the first ever boot. If you changed `.env` values after that first run, either update the user manually or wipe the volume (`docker compose down -v`).
- **Init scripts didn't run again after re-editing them**: they only run on an empty data directory. Wipe & recreate: `docker compose down -v && docker compose up -d`.
- **Windows path issues with bind mounts**: make sure `c:\ssp\docker\compose\init` is shared in Docker Desktop → Settings → Resources → File sharing.
