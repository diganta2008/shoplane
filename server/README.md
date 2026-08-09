# ShopLane Server (Java)

Spring Boot 3.4 REST API — the persistence and business-logic tier
of the ShopLane demo. Talks to MySQL 8 via Spring Data JPA; the static
`client/` calls it directly on `:8080`.

## Stack

- Java 21, Spring Boot 3.4, Maven
- Spring Web + Spring Validation
- Spring Data JPA + Hibernate 6 (`ddl-auto=none`, schema owned by `docker/init/*.sql`)
- Spring Security 6 with a custom stateless JWT filter (JJWT 0.12)
- BCrypt password hashing (strength 10)
- springdoc-openapi 2.7 → Swagger UI at `/swagger-ui.html`
- Spring Boot Actuator → `/actuator/health`

## Layout

```
server/
├── pom.xml
├── Dockerfile
└── src/main/
    ├── java/com/shoplane/
    │   ├── ShopLaneApplication.java
    │   ├── config/               ShopLaneProperties, SecurityConfig, CorsConfig, OpenApiConfig
    │   ├── common/               ApiException, ApiResponse, ErrorResponse, PageMeta,
    │   │                         GlobalExceptionHandler, RequestIdFilter, HealthController
    │   ├── auth/                 AuthService, AuthController, JwtService, JwtAuthFilter,
    │   │                         AuthenticatedUser, dto/…
    │   ├── user/                 User, UserProfile, UserCredential (entities) +
    │   │                         repos, ProfileService, ProfileController, dto/…
    │   ├── product/              Product, Category, repos, ProductSpecs (JPA Specifications),
    │   │                         ProductService, ProductController, dto/…
    │   ├── cart/                 Cart, CartItem, CartService, CartController, dto/…
    │   ├── order/                Order, OrderItem, OrderService, OrderController, dto/…
    │   ├── wishlist/             Wishlist (composite id), WishlistService, WishlistController
    │   └── coupon/               Coupon, CouponService, CouponController, dto/…
    └── resources/application.yml
```

## Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8 running with the ShopLane schema (see `docker/`).

## Run locally

```bash
cd server
mvn spring-boot:run
```

Server binds to `http://localhost:8080`. Verify with:

```bash
curl http://localhost:8080/api/v1/health
```

Swagger UI: <http://localhost:8080/swagger-ui.html>

## Configuration

Everything is read from environment variables (see `application.yml`).
Defaults match the compose stack.

| Env var | Default | Purpose |
|---------|---------|---------|
| `SERVER_PORT` | `8080` | HTTP port |
| `DB_URL` | `jdbc:mysql://127.0.0.1:3306/shoplane?…` | Full JDBC URL |
| `DB_USER` | `shoplane` | MySQL user |
| `DB_PASSWORD` | `shoplane_change_me` | MySQL password |
| `JWT_SECRET` | dev-only | Access-token HS256 secret (≥32 bytes recommended) |
| `JWT_REFRESH_SECRET` | dev-only | Refresh-token HS256 secret |
| `JWT_ACCESS_TTL_MINUTES` | `15` | Access token TTL |
| `JWT_REFRESH_TTL_DAYS` | `7` | Refresh token TTL |
| `CORS_ORIGINS` | `localhost:5500,127.0.0.1:5500,localhost:3000` | Comma-separated allow-list |

## Package as a jar

```bash
mvn -DskipTests package
java -jar target/shoplane-backend.jar
```

## Docker

```bash
docker build -t shoplane-server .
docker run --rm --network shoplane-net -p 8080:8080 \
  -e DB_URL="jdbc:mysql://mysql:3306/shoplane?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -e DB_USER=shoplane -e DB_PASSWORD=shoplane_change_me \
  shoplane-server
```

## API reference

See [`../docs/API.md`](../docs/API.md) for the full endpoint list, and
Swagger UI at `/swagger-ui.html` for interactive testing.
