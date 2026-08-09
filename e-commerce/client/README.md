# ShopLane — Automation-friendly e-commerce demo

A complete, self-contained e-commerce web site designed for writing UI-automation scripts (Playwright, Selenium, Cypress, WebdriverIO, etc.). Every interactive element has a stable `data-testid`, all state is stored in `localStorage`, and there are no external backend dependencies.

## Quick start

```powershell
# from the workspace root
npx http-server c:\ssp\e-commerce\client -p 9090
```

Then open <http://127.0.0.1:9090/> — the root `index.html` immediately
redirects to `/pages/home.html`.

Any static server works (`python -m http.server 9090`, VS Code Live Server, etc.). No build step required.

## Folder layout

```
client/
├── index.html               Root redirect stub → pages/home.html
├── pages/
│   ├── home.html            Home
│   ├── shop.html            Product listing with filters + sort
│   ├── product.html         Product detail (?id=N)
│   ├── cart.html            Shopping cart
│   ├── checkout.html        Checkout form
│   ├── order-confirmation.html Order success
│   ├── login.html           Sign in
│   ├── register.html        Create account
│   ├── account.html         Profile + order history
│   ├── wishlist.html        Saved items
│   ├── contact.html         Contact form
│   └── 404.html             Not-found page
├── generate-images.js       Regenerates SVGs if you edit the palette
├── css/
│   └── styles.css           Single shared stylesheet
├── js/
│   ├── data.js              window.CATALOG, window.CATEGORIES, getProduct()
│   ├── app.js               window.ShopLane API + header/footer inject
│   ├── api.js               window.ShopLaneApi (server fetch client)
│   └── auth.js              window.Auth API (localStorage-backed)
└── images/                  12 product SVGs + hero + logo + empty-cart + success
```

## URL map

Everything below is relative to `http://127.0.0.1:9090/`.

| Path | Purpose | Interesting query params |
|------|---------|--------------------------|
| `/` | Redirects to `/pages/home.html` (root `index.html` stub) | — |
| `/pages/home.html` | Home | — |
| `/pages/shop.html` | Catalog | `?category=Electronics\|Fashion\|Home\|Books`, `?q=...` |
| `/pages/product.html` | Product detail | `?id=1..12` |
| `/pages/cart.html` | Cart | — |
| `/pages/checkout.html` | Checkout | Redirects to `cart.html` when cart is empty |
| `/pages/order-confirmation.html` | Confirmation | Reads last order id from `sessionStorage.shoplane.last-order` |
| `/pages/login.html` | Sign-in | `?next=<url>` — redirect after login |
| `/pages/register.html` | Sign-up | — |
| `/pages/account.html` | Account | Requires login (shows sign-in card otherwise) |
| `/pages/wishlist.html` | Wishlist | — |
| `/pages/contact.html` | Contact | — |
| `/pages/404.html` | Not-found | — |

## Local state (persisted in `localStorage`)

| Key | Value |
|-----|-------|
| `shoplane.cart`     | `[{ key, productId, qty, size, color, addedAt }]` |
| `shoplane.wishlist` | `[productId, …]` |
| `shoplane.orders`   | `[{ id, date, status, items, shipping, payment, summary }]` |
| `shoplane.user`     | `{ email, name, loggedInAt }` |
| `shoplane.coupon`   | `{ code, type, value, label }` |

Wipe the whole demo state in DevTools console:

```js
Object.keys(localStorage).filter(k => k.startsWith('shoplane.')).forEach(k => localStorage.removeItem(k));
```

## Coupon codes (built-in)

| Code | Effect |
|------|--------|
| `WELCOME10` | 10 % off |
| `SAVE20`    | 20 % off |
| `FREESHIP`  | Waives shipping |

Any other code returns an "Invalid coupon" toast.

## Login (simulated)

`Auth.login(email, password)` accepts **any well-formed email** with a **password ≥ 6 characters** — no real backend. Use whatever credentials your automation prefers, e.g. `automation@example.com` / `test1234`.

## Public JS APIs (great for asserting state in tests)

```js
// via window.ShopLane
ShopLane.getCart()               // → line items
ShopLane.addToCart(id, qty, {size, color})
ShopLane.updateQty(key, qty)
ShopLane.removeFromCart(key)
ShopLane.clearCart()
ShopLane.cartCount()             // → total items
ShopLane.cartSummary()           // → { subtotal, discount, shipping, tax, total, coupon }
ShopLane.applyCoupon(code)       // → boolean
ShopLane.getWishlist()
ShopLane.toggleWishlist(id)
ShopLane.getOrders()
ShopLane.placeOrder(shipping, payment)  // → order object
ShopLane.money(n)                // → "$12.34"
ShopLane.stars(n)                // → "★★★★☆"

// via window.Auth
Auth.get()       // → user | null
Auth.isLoggedIn()
Auth.login(email, password)
Auth.register(name, email, password)
Auth.clear()

// catalog
window.CATALOG            // array of 12 products
window.CATEGORIES         // 4 categories
window.getProduct(id)     // lookup by id
```

## data-testid selector reference

Every clickable, editable or assertion-worthy element has a `data-testid`. Below is a curated list — grep the source for others.

### Global header / footer

| Selector | Element |
|----------|---------|
| `[data-testid="brand-logo"]`     | ShopLane logo (returns to `pages/home.html`) |
| `[data-testid="search-input"]`   | Global search input (submits to `pages/shop.html?q=...`) |
| `[data-testid="nav-home"]`       | Nav link → Home |
| `[data-testid="nav-shop"]`       | Nav link → Shop |
| `[data-testid="nav-contact"]`    | Nav link → Contact |
| `[data-testid="cart-btn"]`       | Cart icon in header |
| `[data-testid="cart-count"]`     | Cart badge count |
| `[data-testid="wishlist-btn"]`   | Wishlist icon |
| `[data-testid="wishlist-count"]` | Wishlist badge count |
| `[data-testid="account-btn"]`    | Account icon |
| `[data-testid="toast-root"]`     | Container for transient notifications |
| `[data-testid="toast"]`          | Individual toast |
| `[data-testid="footer-*"]`       | Footer links |

### Product cards (used on many pages)

| Selector | Element |
|----------|---------|
| `[data-testid="product-card"]`         | Any product card. Also carries `data-product-id="N"` |
| `[data-testid="product-link"]`         | Card image / link to product page |
| `[data-testid="add-to-cart-quick"]`    | "+ Add" quick-add button on card |
| `[data-testid="wishlist-toggle"]`      | Heart on card (also `data-product-id`) |

### Home (`pages/home.html`)

`hero`, `hero-eyebrow`, `hero-shop-now`, `hero-explore`, `hero-stats`, `stat-products|customers|rating`, `category-grid`, `category-tile` (also `data-category`), `featured-grid`, `view-all-products`, `promo-banner`, `promo-cta`, `testimonials`, `testimonial`, `newsletter`, `newsletter-email`, `newsletter-submit`, `newsletter-feedback`.

### Shop (`shop.html`)

`page-title`, `page-subtitle`, `breadcrumbs`, `filter-panel`,
`filter-category-all|electronics|fashion|home|books`,
`filter-price` (range input), `price-max-label`,
`filter-rating-any|4|4-5`, `filter-in-stock`, `reset-filters`,
`shop-toolbar`, `result-count`, `filter-chips`, `chip-category|price|rating|stock|query`,
`sort-select`, `product-grid`, `no-results`,
`count-Electronics|Fashion|Home|Books`.

### Product detail (`product.html`)

`gallery`, `gallery-main`, `gallery-thumbs`, `thumb-1..4`,
`product-info`, `product-name`, `product-rating`, `product-price`,
`product-old-price`, `product-save`, `product-description`,
`size-group`, `color-group`, `size-<VALUE>`, `color-<VALUE>`,
`qty-wrap`, `qty-decrement`, `qty-input`, `qty-increment`, `stock-status`,
`add-to-cart`, `buy-now`, `add-wishlist`,
`tabs-block`, `tabs`, `tab-description|specifications|reviews`,
`panel-description|specifications|reviews`, `spec-list`, `review`, `related-grid`.

### Cart (`cart.html`)

`page-title`, `breadcrumbs`, `empty-cart`, `empty-cart-cta`, `cart-list`,
`cart-row` (carries `data-line-key`), `cart-item-name`, `cart-item-meta`,
`cart-item-price`, `cart-qty`, `cart-qty-dec`, `cart-qty-input`, `cart-qty-inc`,
`cart-item-subtotal`, `cart-item-remove`,
`order-summary`, `coupon-input`, `apply-coupon`, `summary-subtotal`,
`summary-discount`, `summary-shipping`, `summary-tax`, `summary-total`,
`checkout-btn`, `continue-shopping`, `clear-cart`.

### Checkout (`checkout.html`)

`checkout-form`, `checkout-email`, `checkout-phone`,
`checkout-first-name`, `checkout-last-name`, `checkout-address`,
`checkout-city`, `checkout-state`, `checkout-zip`, `checkout-country`,
`checkout-save-address`, `payment-methods`,
`payment-card`, `payment-upi`, `payment-cod`,
`card-number`, `card-expiry`, `card-cvv`, `agree-terms`,
`checkout-feedback`, `place-order`,
`checkout-summary`, `summary-item`, `checkout-total`.

### Order confirmation (`order-confirmation.html`)

`no-order`, `order-success`, `order-details`, `order-number`, `order-date`,
`order-status`, `order-item`, `order-shipping-address`, `order-payment-method`,
`order-summary`, `order-total`, `view-orders`, `continue-shopping`, `order-email`.

### Auth pages

**Login**: `login-card`, `login-email`, `login-password`, `login-remember`, `login-forgot`, `login-feedback`, `login-submit`, `link-register`.
**Register**: `register-card`, `register-name`, `register-email`, `register-password`, `register-confirm`, `register-terms`, `register-feedback`, `register-submit`, `link-login`.

### Account (`account.html`)

`auth-required`, `go-login`, `account-nav`,
`nav-profile|orders|wishlist|cart`, `logout`, `account-panel`,
`profile-card`, `user-name`, `user-email`, `no-orders`, `order-row` (with `data-order-id`), `order-id`, `order-status`, `reorder`.

### Wishlist

`empty-wishlist`, `empty-wishlist-cta`, `wishlist-count`, `add-all-to-cart`, `wishlist-grid`.

### Contact

`contact-card`, `contact-info`, `contact-name`, `contact-email`,
`contact-subject`, `contact-message`, `contact-feedback`, `contact-submit`,
`map-placeholder`.

## End-to-end journey (happy path)

1. Home → click a category tile → land on `/pages/shop.html?category=X`
2. Click any `[data-testid="product-card"]` → land on `/pages/product.html?id=X`
3. Select size (if any), color (if any), qty → `[data-testid="add-to-cart"]`
4. `[data-testid="cart-btn"]` → cart with the item + `data-testid="checkout-btn"`
5. `/pages/checkout.html` → fill form → `[data-testid="place-order"]`
6. `/pages/order-confirmation.html` → assert `[data-testid="order-number"]` matches `ShopLane.getOrders()[0].id`

## Example Playwright test

```js
import { test, expect } from '@playwright/test';

test('add product to cart and checkout', async ({ page }) => {
  await page.goto('http://127.0.0.1:9090/');
  await page.getByTestId('hero-shop-now').click();
  await expect(page).toHaveURL(/shop\.html/);

  const firstCard = page.getByTestId('product-card').first();
  await firstCard.getByTestId('add-to-cart-quick').click();
  await expect(page.getByTestId('cart-count')).toHaveText('1');

  await page.getByTestId('cart-btn').click();
  await page.getByTestId('coupon-input').fill('WELCOME10');
  await page.getByTestId('apply-coupon').click();
  await expect(page.getByTestId('summary-discount')).toBeVisible();

  await page.getByTestId('checkout-btn').click();
  await page.getByTestId('checkout-email').fill('automation@example.com');
  await page.getByTestId('checkout-phone').fill('+91 9000000000');
  await page.getByTestId('checkout-first-name').fill('Auto');
  await page.getByTestId('checkout-last-name').fill('Mation');
  await page.getByTestId('checkout-address').fill('123 Test Lane');
  await page.getByTestId('checkout-city').fill('Gurugram');
  await page.getByTestId('checkout-state').fill('Haryana');
  await page.getByTestId('checkout-zip').fill('122002');
  await page.getByTestId('agree-terms').check();
  await page.getByTestId('place-order').click();

  await expect(page).toHaveURL(/order-confirmation\.html/);
  await expect(page.getByTestId('order-number')).toContainText(/^ORD-/);
});
```

## Example Selenium (Python) test

```py
from selenium import webdriver
from selenium.webdriver.common.by import By

d = webdriver.Chrome()
d.get('http://127.0.0.1:9090/')

# search
box = d.find_element(By.CSS_SELECTOR, '[data-testid="search-input"]')
box.send_keys('coffee\n')

# add first result to cart
d.find_element(By.CSS_SELECTOR, '[data-testid="product-card"] [data-testid="add-to-cart-quick"]').click()

# open cart
d.find_element(By.CSS_SELECTOR, '[data-testid="cart-btn"]').click()

assert 'Barista' in d.find_element(By.CSS_SELECTOR, '[data-testid="cart-item-name"]').text
d.quit()
```

## Notes

- All product images are inline SVGs — no binary files, no external assets besides Google Fonts.
- If you want to re-theme the product art, edit `generate-images.js` and re-run `node generate-images.js`.
- No animations depend on real time except toast fade-outs (~2.6s) — set `page.setDefaultTimeout(5000)` in Playwright to keep tests snappy.
