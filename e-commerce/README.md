# ShopLane — Full-Stack E-Commerce Demo

Two-tier layout used for UI + API + performance automation practice:

```
client/             Static HTML + CSS + JS storefront
server/             Spring Boot 3 REST API (business logic + JPA persistence)
docs/               API reference & change log
```

Related in this repo:

- `docker/compose/` — MySQL 8.4 + phpMyAdmin dev stack (schema & seed).

## Architecture

```
┌─────────┐  fetch/XHR   ┌────────────────────────┐  JDBC   ┌────────┐
│ client  │  :5500 →     │ server/ (Spring Boot)  │ ──────▶ │ MySQL  │
│  static │ ──────────▶ │ Boot 3 + JPA + Sec.    │         │  8.4   │
└─────────┘  :8080       │ auth · cart · orders   │         └────────┘
                         │ wishlist · profile     │
                         └────────────────────────┘
```

- **Client** talks directly to `:8080` (Spring Boot).
- **Server** holds all persistence, business rules, and Spring Security
  enforcement. Schema is owned by `docker/compose/init/*.sql`
  (`hibernate.ddl-auto = none`).

Response contract:

- Every response echoes an `X-Request-Id` header (generated if the
  client did not supply one) so log correlation is end-to-end.
- Uniform envelope: `{ "data": … }` on success,
  `{ "error": { code, message, details, requestId } }` on failure.

## Quick start

```bash
# 1. Start MySQL
cd docker/compose
docker compose up -d

# 2. Start the Java server
cd ../../e-commerce/server
mvn spring-boot:run        # http://localhost:8080/swagger-ui.html

# 3. Serve the static client
cd ../client
npx serve -l 5500 .        # http://localhost:5500
```

Or use the root workspace scripts from `e-commerce/`:

```bash
npm run server:dev    # mvn spring-boot:run
npm run web           # serve ./client
```

## Point the client at a different API host

Any HTML page:

```html
<meta name="shoplane-api" content="http://localhost:8080/api/v1">
<!-- or -->
<script>window.SHOPLANE_API_BASE = 'https://api.example.com/api/v1';</script>
```

## Docs

- [server/README.md](server/README.md) — Spring Boot module: layout, build, Docker.
- [docs/API.md](docs/API.md) — REST endpoint reference.
- Swagger UI (while the server is running): <http://localhost:8080/swagger-ui.html>
