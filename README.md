# ShopLane

A full-stack e-commerce demo:

- **Client** (`client/`) — static HTML/CSS/JS storefront, deployable to GitHub Pages.
- **Server** (`server/`) — Java 21 + Spring Boot 3 REST API, MySQL persistence.
- **MySQL** (`docker/`) — Docker Compose stack for local development, plus a combined SQL bundle for cloud MySQL.

## Quick start (local)

```bash
# 1. Start MySQL
cd docker
docker compose up -d

# 2. Start the API
cd ../server
./mvnw spring-boot:run     # or: mvnw.cmd spring-boot:run on Windows

# 3. Serve the client
cd ../client
python -m http.server 9090
```

Then open <http://127.0.0.1:9090/>.

## Deployment

See [`DEPLOY.md`](DEPLOY.md) for the full step-by-step guide:

- Client → GitHub Pages (auto-deployed by `.github/workflows/pages.yml`).
- API + MySQL → Railway (or any Docker host).

## Repo layout

```
.
├── .github/workflows/pages.yml   # Deploy client to GitHub Pages
├── client/                        # Static frontend (deployed to Pages)
├── server/                        # Spring Boot API (deployed to Railway)
├── docs/                          # API reference & change log
├── docker/                        # Local MySQL + init SQL (Docker Compose stack)
├── DEPLOY.md                      # Deployment guide
└── README.md
```
