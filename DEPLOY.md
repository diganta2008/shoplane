# Deploying ShopLane to the internet

**Target setup**

| Piece    | Where it runs        | URL example                                     | Cost |
|----------|----------------------|-------------------------------------------------|------|
| Client   | GitHub Pages         | `https://you.github.io/shoplane/`               | Free |
| Java API | Railway (Docker)     | `https://shoplane-api.up.railway.app/api/v1`    | Free tier (~500 h/mo) |
| MySQL    | Railway MySQL plugin | Internal `mysql.railway.internal:3306`          | Free tier |

Expect ~20 minutes end-to-end.

---

## Prerequisites

- [ ] A GitHub account
- [ ] A Railway account (`https://railway.app` — sign in with GitHub is easiest)
- [ ] Your ShopLane repo pushed to GitHub

If you have not pushed yet:

```powershell
cd c:\ssp
git init
git add .
git commit -m "Initial ShopLane commit"
# create the repo on github.com, then:
git remote add origin https://github.com/<your-username>/shoplane.git
git branch -M main
git push -u origin main
```

---

## Part 1 — Deploy the Java API + MySQL on Railway

### 1.1 Create a project

1. Go to [railway.app](https://railway.app), click **New Project**.
2. Pick **Deploy from GitHub repo** and authorise Railway to see your repo.
3. Select your `shoplane` repo.
4. Railway will start a build. **Cancel it for now** — we need to add MySQL first.

### 1.2 Add MySQL

1. In the project canvas, click **+ New** → **Database** → **Add MySQL**.
2. Wait ~30 seconds for it to spin up. Railway auto-generates `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASSWORD`, etc.

### 1.3 Load the schema + seed data

**Easiest (Railway CLI):**

```powershell
# Install once
npm i -g @railway/cli
railway login
cd c:\ssp
railway link          # pick your project when prompted

# Concatenate all init files and pipe into Railway's MySQL:
Get-Content docker\init\*.sql -Raw | railway run "mysql `$MYSQL_URL"
```

**Alternative (web UI):**

1. Click the MySQL service → **Data** tab → **Query** editor.
2. Open each file in `docker/init/` in order (01 → 08) and paste + run.

Verify:

```sql
SELECT COUNT(*) FROM products;         -- should return 100
SELECT COUNT(*) FROM categories;       -- should return 5
```

### 1.4 Configure the Java service

Click the service Railway created from your repo, then:

1. **Settings → Root Directory** → set to `server`.
   (Tells Railway "the Dockerfile is in this subfolder.")
2. **Settings → Build → Builder** → **Dockerfile**.
3. **Variables** tab → add the following (Railway auto-fills the `${{ MySQL... }}` values from the MySQL service):

   | Variable | Value |
   |---|---|
   | `DB_URL` | `jdbc:mysql://${{ MySQL.MYSQLHOST }}:${{ MySQL.MYSQLPORT }}/${{ MySQL.MYSQLDATABASE }}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8` |
   | `DB_USER` | `${{ MySQL.MYSQLUSER }}` |
   | `DB_PASSWORD` | `${{ MySQL.MYSQLPASSWORD }}` |
   | `JWT_SECRET` | run `openssl rand -hex 32` (or paste any 40+ random chars) |
   | `JWT_REFRESH_SECRET` | different 40+ random chars |
   | `CORS_ORIGINS` | `https://<your-github-username>.github.io` |
   | `SERVER_PORT` | `8080` |

4. **Settings → Networking → Generate Domain** → Railway gives you something like `shoplane-api-production.up.railway.app`.
5. Copy that domain — you'll need it for the client.
6. **Deploy** (top-right) → wait ~5 minutes for the first build.

### 1.5 Smoke test the API

```powershell
$api = "https://shoplane-api-production.up.railway.app"   # replace
curl "$api/api/v1/health"
curl "$api/api/v1/products?size=5"
```

Both should return JSON. If not, check **Deployments → Logs** in Railway.

---

## Part 2 — Deploy the client to GitHub Pages

### 2.1 Enable Pages

1. Go to your repo on GitHub → **Settings → Pages**.
2. Under **Build and deployment**, set **Source** to **GitHub Actions**.

### 2.2 Tell the client where the API lives

1. Same repo → **Settings → Secrets and variables → Actions → Variables tab → New repository variable**:
   - Name: `API_BASE`
   - Value: `https://shoplane-api-production.up.railway.app/api/v1`  ← use YOUR Railway domain + `/api/v1`

Note: `Variables` (not `Secrets`) — API base URL is not secret.

### 2.3 Trigger the deploy

Either push any change to `main`, or:

- GitHub → **Actions** tab → **Deploy client to GitHub Pages** → **Run workflow**.

The workflow file lives at `.github/workflows/pages.yml`. It copies `client/` into the Pages artifact, prepends the API_BASE override to `api.js`, and publishes.

After ~1 minute, your site is live at:

`https://<your-username>.github.io/<repo-name>/`

Because your `index.html` at the client root is a redirect stub, the URL will bounce to `.../pages/home.html` — which is the homepage.

---

## Part 3 — Verify end-to-end

1. Open the Pages URL in an **incognito window** (avoids any cached localStorage).
2. Register a new user. The "signed in offline" banner should NOT appear.
3. Add a product to cart. Log out and back in — the cart should still be there.
4. Open Railway → MySQL → **Data** → `users` table. Your new user should be listed.
5. `orders` and `cart_items` tables should reflect anything you did.

Congratulations — you are running the full stack on the internet.

---

## Common gotchas

- **CORS error in the browser console** — Your `CORS_ORIGINS` env var on Railway does not include your Pages URL. Add it, redeploy Java service.
- **`ECONNREFUSED` on API calls** — Railway service is asleep (free tier scales to zero after inactivity). First request wakes it up; retry after ~10 s.
- **Pages site is a 404** — GitHub Pages was not enabled, or the workflow has not finished. Check **Actions** tab.
- **500 error from API on register/login** — schema was not loaded. Re-run Part 1.3.
- **Client still uses localStorage** — `API_BASE` variable was not set before the workflow ran. Fix the variable and re-run the workflow from the Actions tab.

---

## Alternatives to Railway

Same recipe works with:

- **Render** — free tier, similar Dockerfile support, add a free PostgreSQL (would require swapping MySQL driver + dialect).
- **Fly.io** — free tier, `fly launch` on the `server/` folder detects the Dockerfile.
- **Koyeb** — free tier, GitHub-integrated builds.

The only per-platform change is where you get the connection string and the domain — the Dockerfile and env-var scheme stay the same.

---

## Rollback

To take the site down:

- **API** — Railway → project → **Danger zone → Remove project** (or just stop the service).
- **Client** — GitHub → **Settings → Pages → Source → None**.
