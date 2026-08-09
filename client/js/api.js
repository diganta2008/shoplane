/* api.js — thin fetch client for the ShopLane server.
   Base URL is configurable via <meta name="shoplane-api" content="..."> or
   window.SHOPLANE_API_BASE. Falls back to same-origin :8080/api/v1.        */

(function () {
  const META = document.querySelector('meta[name="shoplane-api"]');
  const BASE =
    (window.SHOPLANE_API_BASE)
    || (META && META.content)
    || (location.protocol + '//' + location.hostname + ':8080/api/v1');

  const TOK_KEY = 'shoplane.tokens';

  function readTokens()  { try { return JSON.parse(localStorage.getItem(TOK_KEY)) || null; } catch { return null; } }
  function writeTokens(t){ if (t) localStorage.setItem(TOK_KEY, JSON.stringify(t)); else localStorage.removeItem(TOK_KEY); }

  async function raw(method, path, { body, auth = false, retryOn401 = true } = {}) {
    const headers = { 'Content-Type': 'application/json', Accept: 'application/json' };
    const tokens = readTokens();
    if (auth && tokens?.accessToken) headers.Authorization = 'Bearer ' + tokens.accessToken;

    const res = await fetch(BASE + path, {
      method,
      headers,
      body: body != null ? JSON.stringify(body) : undefined,
    });

    if (res.status === 401 && auth && retryOn401 && tokens?.refreshToken) {
      const ok = await tryRefresh();
      if (ok) return raw(method, path, { body, auth, retryOn401: false });
    }

    const text = await res.text();
    let json = null;
    try { json = text ? JSON.parse(text) : null; } catch { /* ignore */ }

    if (!res.ok) {
      const err = new Error(json?.error?.message || `HTTP ${res.status}`);
      err.status = res.status;
      err.code   = json?.error?.code;
      err.details = json?.error?.details;
      throw err;
    }
    return json;
  }

  async function tryRefresh() {
    const tokens = readTokens();
    if (!tokens?.refreshToken) return false;
    try {
      const res = await raw('POST', '/auth/refresh', {
        body: { refreshToken: tokens.refreshToken },
        auth: false,
      });
      writeTokens({ ...tokens, ...res.data });
      return true;
    } catch {
      writeTokens(null);
      return false;
    }
  }

  async function isReachable() {
    try {
      const r = await fetch(BASE + '/health', { method: 'GET' });
      return r.ok;
    } catch { return false; }
  }

  const api = {
    base: BASE,
    tokens: { read: readTokens, write: writeTokens },
    isReachable,

    // Auth
    register: (body) => raw('POST', '/auth/register', { body }),
    login:    (body) => raw('POST', '/auth/login', { body }),
    me:       ()     => raw('GET',  '/auth/me', { auth: true }),
    logout:   ()     => raw('POST', '/auth/logout', { auth: true }).catch(() => null),

    // Catalog
    listProducts:    (query = {}) => {
      const qs = new URLSearchParams(query).toString();
      return raw('GET', '/products' + (qs ? '?' + qs : ''));
    },
    getProduct:      (id) => raw('GET', `/products/${id}`),
    listCategories:  ()   => raw('GET', '/categories'),

    // Cart
    getCart:          () => raw('GET',    '/cart', { auth: true }),
    addToCart:        (body) => raw('POST',   '/cart/items', { body, auth: true }),
    updateCartItem:   (itemId, qty) => raw('PATCH', `/cart/items/${itemId}`, { body: { qty }, auth: true }),
    removeCartItem:   (itemId) => raw('DELETE', `/cart/items/${itemId}`, { auth: true }),
    clearCart:        () => raw('DELETE', '/cart', { auth: true }),

    // Wishlist
    listWishlist:     () => raw('GET',    '/wishlist', { auth: true }),
    addToWishlist:    (productId) => raw('POST',   '/wishlist', { body: { productId }, auth: true }),
    removeWishlist:   (productId) => raw('DELETE', `/wishlist/${productId}`, { auth: true }),

    // Orders
    placeOrder:       (body) => raw('POST', '/orders', { body, auth: true }),
    listOrders:       () => raw('GET',    '/orders', { auth: true }),
    getOrder:         (orderNumber) => raw('GET', `/orders/${orderNumber}`, { auth: true }),

    // Profile
    getProfile:       () => raw('GET',    '/profile', { auth: true }),
    updateProfile:    (body) => raw('PATCH',  '/profile', { body, auth: true }),
    changePassword:   (body) => raw('POST',   '/profile/change-password', { body, auth: true }),

    // Addresses
    listAddresses:    () => raw('GET',    '/addresses', { auth: true }),
    getAddress:       (id) => raw('GET',    `/addresses/${id}`, { auth: true }),
    createAddress:    (body) => raw('POST',   '/addresses', { body, auth: true }),
    updateAddress:    (id, body) => raw('PATCH',  `/addresses/${id}`, { body, auth: true }),
    deleteAddress:    (id) => raw('DELETE', `/addresses/${id}`, { auth: true }),
    setDefaultAddress:(id) => raw('POST',   `/addresses/${id}/default`, { auth: true }),

    // Coupons
    validateCoupon:   (code, subtotal) => raw('POST', '/coupons/validate', { body: { code, subtotal } }),
  };

  window.ShopLaneApi = api;
})();
