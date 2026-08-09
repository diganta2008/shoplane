# ShopLane API — Reference

The API is served by the **Spring Boot server** on `:8080`.

| Environment | Base URL |
|-------------|----------|
| Frontend / production callers | `http://localhost:8080/api/v1` |
| Interactive Swagger UI | `http://localhost:8080/swagger-ui.html` |

## Response envelope

Success:
```json
{ "data": { ... }, "meta": { ... } }
```

Error:
```json
{ "error": {
    "code": "UNPROCESSABLE",
    "message": "Validation failed",
    "details": { "email": "must be a well-formed email address" },
    "requestId": "b1c9-..."
} }
```

Every response echoes an `X-Request-Id` header. If the client supplies
one on the request, the server passes it through so log correlation
is end-to-end.

## Auth

| Method | Path | Auth | Body |
|--------|------|------|------|
| POST | `/auth/register` | – | `{ email, password (≥6), fullName, phone? }` |
| POST | `/auth/login`    | – | `{ email, password }` |
| POST | `/auth/refresh`  | – | `{ refreshToken }` |
| POST | `/auth/logout`   | ✅ | – |
| GET  | `/auth/me`       | ✅ | – |

`accessToken` TTL defaults to **15 minutes**, `refreshToken` to **7 days**.
Both are HS256-signed with `JWT_SECRET` / `JWT_REFRESH_SECRET`.

## Catalog

| Method | Path | Query |
|--------|------|-------|
| GET | `/products` | `categorySlug`, `categoryId`, `minPrice`, `maxPrice`, `minRating`, `inStock`, `search`, `sort` (`price_asc`\|`price_desc`\|`rating_desc`\|`newest`\|`popular`), `limit` (≤100), `offset` |
| GET | `/products/:id` | – |
| GET | `/categories` | – |

## Cart (auth required)

| Method | Path | Body |
|--------|------|------|
| GET    | `/cart` | – |
| POST   | `/cart/items` | `{ productId, qty=1, size?, color? }` |
| PATCH  | `/cart/items/:itemId` | `{ qty }` (qty=0 removes) |
| DELETE | `/cart/items/:itemId` | – |
| DELETE | `/cart` | Clear cart |

## Wishlist (auth required)

| Method | Path | Body |
|--------|------|------|
| GET    | `/wishlist` | – |
| POST   | `/wishlist` | `{ productId }` |
| DELETE | `/wishlist/:productId` | – |

## Orders (auth required)

| Method | Path | Body / Query |
|--------|------|--------------|
| POST | `/orders` | `{ paymentMethod: 'card'\|'upi'\|'cod', couponCode?, shippingFee=0, taxRate=0, shipping: {name,email,phone,address,city,state,zip,country}, items?: [{productId, qty, size?, color?}] }` (items defaults to the current cart) |
| GET  | `/orders` | `limit`, `offset` |
| GET  | `/orders/:orderNumber` | – |

Order creation runs inside a Spring `@Transactional` boundary: each line
is resolved against `products`, stock is atomically decremented via a
conditional UPDATE (rejects on oversell), and the user's cart is
cleared.

## Profile (auth required)

| Method | Path | Body |
|--------|------|------|
| GET   | `/profile` | – |
| PATCH | `/profile` | `{ firstName?, lastName?, displayName?, dateOfBirth?, gender?, avatarUrl?, bio?, preferredLanguage?, preferredCurrency?, timezone?, marketingOptIn?, newsletterOptIn?, smsOptIn? }` |
| POST  | `/profile/change-password` | `{ currentPassword, newPassword (≥6) }` |

## Coupons

| Method | Path | Body |
|--------|------|------|
| POST | `/coupons/validate` | `{ code, subtotal }` → computed `discount` |

Seeded coupons: `WELCOME10` (10%), `SAVE20` (20% ≥ 100 subtotal),
`FREESHIP` (flat 9.99).

## Health

- `GET /api/v1/health` — returns `{ status, db, uptime }`.
- `GET /actuator/health` — Spring Boot Actuator.

## Curl cheat-sheet

```bash
curl http://localhost:8080/api/v1/health
curl 'http://localhost:8080/api/v1/products?limit=3'

# register + login
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"qa@shoplane.test","password":"test1234","fullName":"QA"}'

TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"qa@shoplane.test","password":"test1234"}' | jq -r .data.accessToken)

curl http://localhost:8080/api/v1/cart -H "Authorization: Bearer $TOKEN"
curl -X POST http://localhost:8080/api/v1/cart/items \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"productId":1,"qty":2}'
```
