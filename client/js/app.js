/* app.js — shared functionality for every page:
     • injects header + footer
     • hybrid cart / wishlist / orders: calls the ShopLane API when the user
       has a real JWT session; falls back to localStorage otherwise. The
       localStorage copy is also used as a synchronous read cache so that
       renders and product cards stay reactive without awaiting the network.
     • exposes window.ShopLane API used by both pages and automation scripts
   All interactive elements carry data-testid attributes to keep test
   selectors stable.                                                        */

(function () {
  const STORAGE = {
    cart:     'shoplane.cart',
    wishlist: 'shoplane.wishlist',
    orders:   'shoplane.orders',
    user:     'shoplane.user',
    coupon:   'shoplane.coupon',
  };

  function read(key, fallback) {
    try { return JSON.parse(localStorage.getItem(key)) ?? fallback; }
    catch (e) { return fallback; }
  }
  function write(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  function hasApiSession() {
    return !!(window.ShopLaneApi && window.ShopLaneApi.tokens.read()?.accessToken);
  }

  function lineKey(productId, size, color) {
    return `${productId}::${size || ''}::${color || ''}`;
  }

  function getCart()    { return read(STORAGE.cart, []); }
  function setCart(v)   { write(STORAGE.cart, v); dispatch('cart:change', v); refreshBadges(); }

  function cartFromServer(cartDto) {
    return (cartDto.items || []).map((it) => ({
      key:       lineKey(it.productId, it.size, it.color),
      itemId:    it.id,
      productId: it.productId,
      qty:       it.qty,
      size:      it.size  || null,
      color:     it.color || null,
      addedAt:   it.addedAt ? new Date(it.addedAt).getTime() : Date.now(),
    }));
  }

  async function addToCart(productId, qty = 1, options = {}) {
    const product = window.getProduct(productId);
    if (!product) return null;
    const cart = getCart();
    const key = lineKey(productId, options.size, options.color);
    const idx = cart.findIndex((c) => c.key === key);
    if (idx > -1) cart[idx].qty += qty;
    else cart.push({
      key, itemId: null, productId, qty,
      size:  options.size  || null,
      color: options.color || null,
      addedAt: Date.now(),
    });
    setCart(cart);
    toast('Added to cart', `${qty} × ${product.name}`, 'success');

    const user    = read(STORAGE.user, null);
    const hasSess = hasApiSession();
    if (user && !hasSess) {
      const reachable = window.ShopLaneApi ? await window.ShopLaneApi.isReachable() : false;
      if (reachable) {
        console.warn('[shoplane] phantom session detected on addToCart — clearing so DB writes can resume.');
        if (window.Auth?.clear) window.Auth.clear();
        toast('Session expired',
              'Please sign in again so cart items are saved to the database.',
              'error');
      } else {
        toast('Server offline',
              'Cart item is only in this browser — start the API server on :8080 to sync.',
              'warning');
      }
    }

    if (hasSess) {
      try {
        const res = await window.ShopLaneApi.addToCart({
          productId, qty, size: options.size || null, color: options.color || null,
        });
        setCart(cartFromServer(res.data));
      } catch (err) {
        console.warn('[shoplane] addToCart API failed:', err);
        toast('Sync failed',
              'Cart item saved locally only — retrying from server…',
              'error');
        refreshFromServer();
      }
    }
    return getCart();
  }

  async function updateQty(key, qty) {
    const cart = getCart();
    const idx = cart.findIndex((c) => c.key === key);
    if (idx < 0) return;
    const line = cart[idx];
    if (qty <= 0) cart.splice(idx, 1);
    else cart[idx].qty = qty;
    setCart(cart);

    if (hasApiSession() && line.itemId) {
      try {
        if (qty <= 0) await window.ShopLaneApi.removeCartItem(line.itemId);
        else {
          const res = await window.ShopLaneApi.updateCartItem(line.itemId, qty);
          if (res && res.data) setCart(cartFromServer(res.data));
        }
      } catch (err) {
        console.warn('[shoplane] updateQty API failed:', err);
        refreshFromServer();
      }
    }
  }

  async function removeFromCart(key) { return updateQty(key, 0); }

  async function clearCart() {
    setCart([]);
    if (hasApiSession()) {
      try { await window.ShopLaneApi.clearCart(); }
      catch (err) { console.warn('[shoplane] clearCart API failed:', err); refreshFromServer(); }
    }
  }

  function cartCount() { return getCart().reduce((n, l) => n + l.qty, 0); }

  function cartSummary() {
    const cart = getCart();
    let subtotal = 0;
    cart.forEach((line) => {
      const p = window.getProduct(line.productId);
      if (p) subtotal += p.price * line.qty;
    });
    const coupon = read(STORAGE.coupon, null);
    let discount = 0;
    if (coupon) {
      if (coupon.type === 'percent') discount = subtotal * (coupon.value / 100);
      else discount = coupon.value;
    }
    const shipping = subtotal > 0 && subtotal - discount < 100 ? 9.99 : 0;
    const tax = Math.max(0, (subtotal - discount) * 0.08);
    const total = Math.max(0, subtotal - discount + shipping + tax);
    return { subtotal, discount, shipping, tax, total, coupon };
  }

  function applyCoupon(code) {
    const codes = {
      'WELCOME10': { type: 'percent', value: 10, label: '10% off (welcome)' },
      'SAVE20':    { type: 'percent', value: 20, label: '20% off (loyalty)' },
      'FREESHIP':  { type: 'flat',    value: 9.99, label: 'Free shipping' },
    };
    const key = (code || '').trim().toUpperCase();
    const c = codes[key];
    if (!c) {
      toast('Invalid coupon', `We could not find "${code}".`, 'error');
      write(STORAGE.coupon, null);
      return false;
    }
    write(STORAGE.coupon, Object.assign({ code: key }, c));
    toast('Coupon applied', c.label, 'success');
    dispatch('cart:change', getCart());
    return true;
  }

  function getWishlist()  { return read(STORAGE.wishlist, []); }
  function setWishlist(v) { write(STORAGE.wishlist, v); dispatch('wishlist:change', v); refreshBadges(); }
  async function toggleWishlist(id) {
    const productId = Number(id);
    const list = getWishlist();
    const idx = list.indexOf(productId);
    const nowActive = idx < 0;
    if (nowActive) { list.push(productId); toast('Added to wishlist', '', 'success'); }
    else           { list.splice(idx, 1);  toast('Removed from wishlist', '', 'info'); }
    setWishlist(list);

    const user    = read(STORAGE.user, null);
    const hasSess = hasApiSession();
    if (user && !hasSess) {
      const reachable = window.ShopLaneApi ? await window.ShopLaneApi.isReachable() : false;
      if (reachable) {
        if (window.Auth?.clear) window.Auth.clear();
        toast('Session expired',
              'Please sign in again so wishlist is saved to the database.',
              'error');
      }
    }

    if (hasSess) {
      try {
        if (nowActive) await window.ShopLaneApi.addToWishlist(productId);
        else           await window.ShopLaneApi.removeWishlist(productId);
      } catch (err) {
        console.warn('[shoplane] toggleWishlist API failed:', err);
        refreshFromServer();
      }
    }
    return nowActive;
  }
  function inWishlist(id) { return getWishlist().includes(Number(id)); }

  function orderFromServer(o) {
    return {
      id: o.orderNumber,
      date: o.placedAt,
      status: o.status,
      items: (o.items || []).map((l) => {
        const p = window.getProduct ? window.getProduct(l.productId) : null;
        return {
          productId: l.productId,
          name: l.name,
          image: p ? p.image : '',
          qty: l.qty,
          price: Number(l.price),
          size: l.size, color: l.color,
        };
      }),
      shipping: o.shippingAddress,
      payment:  { method: o.paymentMethod },
      summary: {
        subtotal: Number(o.subtotal),
        discount: Number(o.discount),
        shipping: Number(o.shipping),
        tax:      Number(o.tax),
        total:    Number(o.total),
        coupon:   o.couponCode ? { code: o.couponCode } : null,
      },
    };
  }

  function getOrders() { return read(STORAGE.orders, []); }

  /**
   * placeOrder — server-first. Rules:
   *   1. If cart is empty ⇒ null.
   *   2. If signed in via localStorage but no API tokens, and the API IS reachable,
   *      wipe the phantom session and throw `PhantomSessionError` so the caller
   *      can send the user back to /login. (This is the exact scenario that
   *      caused "orders not in DB" — user thought they were logged in but every
   *      write was going to localStorage.)
   *   3. If tokens are present, always call the API and only persist to
   *      localStorage after the server confirms. Errors bubble up.
   *   4. If not signed in at all (guest checkout), use the localStorage path.
   */
  async function placeOrder(shipping, payment) {
    const cart = getCart();
    if (!cart.length) return null;
    const summary = cartSummary();
    const coupon  = read(STORAGE.coupon, null);

    const user     = read(STORAGE.user, null);
    const hasSess  = hasApiSession();
    const reachable = window.ShopLaneApi ? await window.ShopLaneApi.isReachable() : false;

    if (user && !hasSess && reachable) {
      if (window.Auth?.clear) window.Auth.clear();
      toast('Session expired',
            'Please sign in again — your order was NOT sent to the server.',
            'error');
      const err = new Error('Phantom session — please sign in again.');
      err.code = 'PHANTOM_SESSION';
      throw err;
    }

    if (user && !reachable) {
      toast('Server unreachable',
            'Order was NOT saved to the database. Start the API server on :8080 and try again.',
            'error');
      const err = new Error('API server is unreachable — order not saved to database.');
      err.code = 'API_UNREACHABLE';
      throw err;
    }

    if (hasSess) {
      const [firstName, ...rest] = String(shipping.name || '').trim().split(/\s+/);
      const req = {
        paymentMethod: (payment && payment.method) || 'card',
        couponCode:    coupon ? coupon.code : null,
        shippingFee:   Number(summary.shipping.toFixed(2)),
        taxRate:       summary.subtotal > 0
          ? Number((summary.tax / Math.max(0.01, (summary.subtotal - summary.discount))).toFixed(4))
          : 0,
        items: cart.map((l) => ({
          productId: l.productId, qty: l.qty,
          size: l.size || null, color: l.color || null,
        })),
      };

      if (shipping && shipping.addressId) {
        req.addressId = Number(shipping.addressId);
      } else {
        req.shipping = {
          name:    shipping.name    || `${firstName || 'Guest'} ${rest.join(' ')}`.trim() || 'Guest',
          email:   shipping.email   || 'guest@shoplane.test',
          phone:   shipping.phone   || '0000000000',
          address: shipping.address || 'N/A',
          city:    shipping.city    || 'N/A',
          state:   shipping.state   || 'NA',
          zip:     shipping.zip     || '00000',
          country: shipping.country || 'IN',
        };
        if (shipping.saveAsAddress && window.ShopLaneApi) {
          try {
            const savedRes = await window.ShopLaneApi.createAddress({
              label:    shipping.saveAsLabel || null,
              fullName: req.shipping.name,
              phone:    req.shipping.phone,
              street:   req.shipping.address,
              city:     req.shipping.city,
              state:    req.shipping.state,
              zip:      req.shipping.zip,
              country:  req.shipping.country,
              isDefault: !!shipping.saveAsDefault,
            });
            req.addressId = savedRes.data.id;
            delete req.shipping;
            toast('Address saved', 'Available for future orders.', 'success');
          } catch (e) {
            console.warn('[shoplane] createAddress failed, falling back to inline shipping:', e);
          }
        }
      }
      try {
        const res = await window.ShopLaneApi.placeOrder(req);
        const order = orderFromServer(res.data);
        const orders = getOrders();
        orders.unshift(order);
        write(STORAGE.orders, orders);
        setCart([]);
        write(STORAGE.coupon, null);
        refreshFromServer();
        return order;
      } catch (err) {
        console.error('[shoplane] placeOrder API failed:', err);
        toast('Order failed',
              (err.message || 'Could not place order.') +
              (err.details ? ' — ' + JSON.stringify(err.details) : ''),
              'error');
        throw err;
      }
    }

    const order = {
      id: 'ORD-' + Date.now().toString(36).toUpperCase(),
      date: new Date().toISOString(),
      status: 'Confirmed',
      items: cart.map((l) => {
        const p = window.getProduct(l.productId);
        return {
          productId: l.productId,
          name: p ? p.name : 'Unknown',
          image: p ? p.image : '',
          qty: l.qty, price: p ? p.price : 0,
          size: l.size, color: l.color,
        };
      }),
      shipping, payment, summary,
    };
    const orders = getOrders();
    orders.unshift(order);
    write(STORAGE.orders, orders);
    setCart([]);
    write(STORAGE.coupon, null);
    return order;
  }

  async function refreshFromServer(opts = {}) {
    if (!hasApiSession()) return;
    try {
      if (opts.mergeLocalCart) {
        const local = getCart().filter((l) => !l.itemId);
        for (const line of local) {
          try {
            await window.ShopLaneApi.addToCart({
              productId: line.productId, qty: line.qty,
              size: line.size || null, color: line.color || null,
            });
          } catch (e) {
            console.warn('[shoplane] could not merge guest cart line into server cart:', line, e);
          }
        }
      }

      const [cartRes, wishRes, ordersRes] = await Promise.all([
        window.ShopLaneApi.getCart(),
        window.ShopLaneApi.listWishlist(),
        window.ShopLaneApi.listOrders({ limit: 20 }).catch(() => ({ data: [] })),
      ]);
      write(STORAGE.cart,     cartFromServer(cartRes.data));
      write(STORAGE.wishlist, (wishRes.data || []).map((e) => e.productId));
      const localOrders = (ordersRes.data || []).map(orderFromServer);
      write(STORAGE.orders, localOrders);
      refreshBadges();
      dispatch('cart:change', getCart());
      dispatch('wishlist:change', getWishlist());
      dispatch('orders:change', localOrders);
    } catch (err) {
      console.warn('[shoplane] refreshFromServer failed:', err);
    }
  }

  function clearLocalUserData() {
    write(STORAGE.cart, []);
    write(STORAGE.wishlist, []);
    write(STORAGE.orders, []);
    write(STORAGE.coupon, null);
    refreshBadges();
    dispatch('cart:change', []);
    dispatch('wishlist:change', []);
  }

  function dispatch(name, detail) {
    window.dispatchEvent(new CustomEvent(name, { detail }));
  }

  const NAV_LINKS = [
    { href: 'home.html',       label: 'Home' },
    { href: 'shop.html',       label: 'Shop' },
    { href: 'healthcare.html', label: 'Healthcare' },
    { href: 'contact.html',    label: 'Contact' },
  ];

  function renderHeader() {
    const el = document.getElementById('site-header');
    if (!el) return;
    const currentPage = location.pathname.split('/').pop() || 'home.html';
    const linksHtml = NAV_LINKS.map((l) => `
      <li><a href="${l.href}" data-testid="nav-${l.label.toLowerCase()}"
             class="${currentPage === l.href ? 'active' : ''}">${l.label}</a></li>`).join('');

    el.innerHTML = `
      <div class="container nav">
        <a href="home.html" class="brand" data-testid="brand-logo">
          <span class="brand-mark">S</span>ShopLane
        </a>

        <div class="search-wrap">
          <span class="search-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
          </span>
          <input type="search" id="global-search" placeholder="Search for products, brands…"
                 data-testid="search-input" autocomplete="off"/>
        </div>

        <ul class="nav-links">${linksHtml}</ul>

        <div class="nav-actions">
          <a href="wishlist.html" class="icon-btn" title="Wishlist" data-testid="wishlist-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            <span class="badge hidden" id="wishlist-badge" data-testid="wishlist-count">0</span>
          </a>
          <a href="cart.html" class="icon-btn" title="Cart" data-testid="cart-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            <span class="badge hidden" id="cart-badge" data-testid="cart-count">0</span>
          </a>
          <a href="account.html" class="icon-btn" title="Account" data-testid="account-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
            </svg>
          </a>
        </div>
      </div>`;

    const search = document.getElementById('global-search');
    if (search) {
      search.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          const q = search.value.trim();
          location.href = 'shop.html' + (q ? '?q=' + encodeURIComponent(q) : '');
        }
      });
    }
  }

  function renderFooter() {
    const el = document.getElementById('site-footer');
    if (!el) return;
    el.innerHTML = `
      <div class="container">
        <div class="footer-grid">
          <div>
            <div class="footer-brand"><span class="brand-mark">S</span>ShopLane</div>
            <p style="color:#94A3B8;font-size:14px;line-height:1.6;">
              Curated products for everyday life — from premium electronics
              to timeless fashion and beautifully made home essentials.
            </p>
            <div class="social-links mt-16" data-testid="social-links">
              <a href="#" data-testid="social-ig" aria-label="Instagram" title="Instagram">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2.2c3.2 0 3.6 0 4.9.1a6.7 6.7 0 0 1 2.2.4 4.5 4.5 0 0 1 2.2 2.2 6.7 6.7 0 0 1 .4 2.2c.1 1.3.1 1.7.1 4.9s0 3.6-.1 4.9a6.7 6.7 0 0 1-.4 2.2 4.5 4.5 0 0 1-2.2 2.2 6.7 6.7 0 0 1-2.2.4c-1.3.1-1.7.1-4.9.1s-3.6 0-4.9-.1a6.7 6.7 0 0 1-2.2-.4 4.5 4.5 0 0 1-2.2-2.2 6.7 6.7 0 0 1-.4-2.2C2.2 15.6 2.2 15.2 2.2 12s0-3.6.1-4.9a6.7 6.7 0 0 1 .4-2.2 4.5 4.5 0 0 1 2.2-2.2 6.7 6.7 0 0 1 2.2-.4C8.4 2.2 8.8 2.2 12 2.2m0 2.2c-3.1 0-3.5 0-4.7.1a4.5 4.5 0 0 0-1.5.3 2.6 2.6 0 0 0-1.5 1.5 4.5 4.5 0 0 0-.3 1.5C4 8.5 4 8.9 4 12s0 3.5.1 4.7a4.5 4.5 0 0 0 .3 1.5 2.6 2.6 0 0 0 1.5 1.5 4.5 4.5 0 0 0 1.5.3c1.2.1 1.6.1 4.7.1s3.5 0 4.7-.1a4.5 4.5 0 0 0 1.5-.3 2.6 2.6 0 0 0 1.5-1.5 4.5 4.5 0 0 0 .3-1.5c.1-1.2.1-1.6.1-4.7s0-3.5-.1-4.7a4.5 4.5 0 0 0-.3-1.5 2.6 2.6 0 0 0-1.5-1.5 4.5 4.5 0 0 0-1.5-.3C15.5 4.5 15.1 4.4 12 4.4m0 3.7a3.9 3.9 0 1 1 0 7.8 3.9 3.9 0 0 1 0-7.8m0 6.4a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5m4.9-6.6a.9.9 0 1 1 0-1.8.9.9 0 0 1 0 1.8"/></svg>
              </a>
              <a href="#" data-testid="social-fb" aria-label="Facebook" title="Facebook">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M22 12a10 10 0 1 0-11.6 9.9v-7H8v-3h2.4V9.4c0-2.4 1.4-3.7 3.6-3.7 1 0 2.1.2 2.1.2v2.3h-1.2c-1.2 0-1.5.7-1.5 1.5V12H16l-.4 3h-2.2v7A10 10 0 0 0 22 12"/></svg>
              </a>
              <a href="#" data-testid="social-li" aria-label="LinkedIn" title="LinkedIn">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M20.45 20.45h-3.55v-5.57c0-1.33-.02-3.04-1.85-3.04-1.85 0-2.14 1.45-2.14 2.94v5.67H9.35V9h3.41v1.56h.05c.48-.9 1.64-1.85 3.37-1.85 3.6 0 4.27 2.37 4.27 5.46v6.28ZM5.34 7.43a2.06 2.06 0 1 1 0-4.12 2.06 2.06 0 0 1 0 4.12M7.11 20.45H3.56V9h3.55v11.45M22.22 0H1.77C.79 0 0 .77 0 1.72v20.56C0 23.23.79 24 1.77 24h20.45c.98 0 1.78-.77 1.78-1.72V1.72C24 .77 23.2 0 22.22 0"/></svg>
              </a>
              <a href="#" data-testid="social-x" aria-label="X (Twitter)" title="X">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/></svg>
              </a>
            </div>

            <div class="app-badges mt-16" data-testid="app-badges">
              <a href="#" class="app-badge" data-testid="app-badge-apple"
                 aria-label="Download on the App Store">
                <img src="../images/app-store-badge.svg" alt="Download on the App Store"/>
              </a>
              <a href="#" class="app-badge" data-testid="app-badge-google"
                 aria-label="Get it on Google Play">
                <img src="../images/google-play-badge.svg" alt="Get it on Google Play"/>
              </a>
            </div>
          </div>
          <div>
            <h4>Shop</h4>
            <ul>
              <li><a href="shop.html?category=Electronics" data-testid="footer-cat-electronics">Electronics</a></li>
              <li><a href="shop.html?category=Fashion"     data-testid="footer-cat-fashion">Fashion</a></li>
              <li><a href="shop.html?category=Home"        data-testid="footer-cat-home">Home &amp; Kitchen</a></li>
              <li><a href="shop.html?category=Books"       data-testid="footer-cat-books">Books</a></li>
              <li><a href="shop.html?category=Medical"     data-testid="footer-cat-medical">Medical</a></li>
            </ul>
          </div>
          <div>
            <h4>Support</h4>
            <ul>
              <li><a href="contact.html" data-testid="footer-contact">Contact us</a></li>
              <li><a href="#" data-testid="footer-faq">FAQ</a></li>
              <li><a href="#" data-testid="footer-shipping">Shipping</a></li>
              <li><a href="#" data-testid="footer-returns">Returns</a></li>
            </ul>
          </div>
          <div>
            <h4>Account</h4>
            <ul>
              <li><a href="login.html"    data-testid="footer-login">Sign in</a></li>
              <li><a href="register.html" data-testid="footer-register">Create account</a></li>
              <li><a href="account.html"  data-testid="footer-orders">My orders</a></li>
              <li><a href="wishlist.html" data-testid="footer-wishlist">Wishlist</a></li>
            </ul>
          </div>
        </div>
        <div class="footer-bottom">
          <div>© 2026 ShopLane. All rights reserved.</div>
          <div>Automation-friendly demo store — every interactive element has a stable <code style="background:#1E293B;color:#F59E0B;padding:2px 6px;border-radius:4px;">data-testid</code>.</div>
        </div>
      </div>`;
  }

  function refreshBadges() {
    const cartBadge = document.getElementById('cart-badge');
    const wishBadge = document.getElementById('wishlist-badge');
    if (cartBadge) {
      const n = cartCount();
      cartBadge.textContent = String(n);
      cartBadge.classList.toggle('hidden', n === 0);
    }
    if (wishBadge) {
      const n = getWishlist().length;
      wishBadge.textContent = String(n);
      wishBadge.classList.toggle('hidden', n === 0);
    }
    refreshSessionPill();
  }

  let _sessionState = { online: false, reachable: false, checkedAt: 0 };
  async function refreshSessionPill() {
    const pill  = document.getElementById('session-pill');
    if (!pill) return;
    const label = pill.querySelector('.label');
    const hasSess = hasApiSession();
    const user    = (function(){ try { return JSON.parse(localStorage.getItem(STORAGE.user)); } catch { return null; } })();

    let reachable = _sessionState.reachable;
    const stale   = Date.now() - _sessionState.checkedAt > 15000;
    if (window.ShopLaneApi && (stale || hasSess !== _sessionState.online)) {
      reachable = await window.ShopLaneApi.isReachable();
      _sessionState = { online: hasSess, reachable, checkedAt: Date.now() };
    }

    pill.classList.remove('online', 'local', 'offline');
    if (hasSess && reachable) {
      pill.classList.add('online');
      label.textContent = 'server \u25CF ' + (user?.email || 'signed in');
      pill.title = 'Signed in — cart / orders sync to MySQL';
    } else if (!hasSess) {
      pill.classList.add('local');
      label.textContent = 'local only';
      pill.title = 'Not signed in — cart / orders live only in this browser';
    } else {
      pill.classList.add('offline');
      label.textContent = 'server unreachable';
      pill.title = 'Signed in but API is down — mutations stay in this browser';
    }
  }

  function ensureToastRoot() {
    let root = document.getElementById('toast-root');
    if (!root) {
      root = document.createElement('div');
      root.id = 'toast-root';
      root.className = 'toast-container';
      root.setAttribute('data-testid', 'toast-root');
      document.body.appendChild(root);
    }
    return root;
  }
  function toast(title, msg, type) {
    const root = ensureToastRoot();
    const t = document.createElement('div');
    t.className = 'toast ' + (type || '');
    t.setAttribute('data-testid', 'toast');
    t.innerHTML = `
      <div class="toast-title">${title}</div>
      ${msg ? `<div class="toast-msg">${msg}</div>` : ''}
    `;
    root.appendChild(t);
    setTimeout(() => { t.style.opacity = '0'; t.style.transition = 'opacity 200ms'; }, 2600);
    setTimeout(() => { t.remove(); }, 2900);
  }

  function money(v) { return '$' + Number(v).toFixed(2); }
  function stars(rating) {
    const full = Math.round(rating);
    return '★★★★★☆☆☆☆☆'.substr(5 - full, 5);
  }
  function productCardHtml(p) {
    const wish = inWishlist(p.id) ? 'active' : '';
    const badge = p.badge
      ? `<span class="product-tag ${p.badge}">${p.badge}</span>` : '';
    return `
      <article class="product-card" data-testid="product-card" data-product-id="${p.id}">
        <a href="product.html?id=${p.id}" class="product-media" data-testid="product-link">
          ${badge}
          <img src="${p.image}" alt="${p.name}"/>
        </a>
        <button class="wishlist-btn ${wish}" data-testid="wishlist-toggle" data-product-id="${p.id}" aria-label="Wishlist">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="${wish ? 'currentColor' : 'none'}" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/></svg>
        </button>
        <div class="product-info">
          <span class="product-category" data-testid="product-category">
            ${p.category}${p.subCategory ? ` <span class="product-subcategory" data-testid="product-subcategory">&middot; ${p.subCategory}</span>` : ''}
          </span>
          <h3 class="product-title"><a href="product.html?id=${p.id}">${p.name}</a></h3>
          <div class="product-rating">
            <span class="stars">${stars(p.rating)}</span>
            <span>${p.rating.toFixed(1)} (${p.reviewCount})</span>
          </div>
          <div class="product-price-row">
            <div class="product-price">
              ${money(p.price)}
              ${p.oldPrice ? `<span class="old">${money(p.oldPrice)}</span>` : ''}
            </div>
            <button class="add-cart-btn" data-testid="add-to-cart-quick" data-product-id="${p.id}">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              Add
            </button>
          </div>
        </div>
      </article>`;
  }

  document.addEventListener('click', (e) => {
    const addBtn = e.target.closest('[data-testid="add-to-cart-quick"]');
    if (addBtn) {
      e.preventDefault();
      addToCart(Number(addBtn.dataset.productId), 1);
      return;
    }
    const wishBtn = e.target.closest('[data-testid="wishlist-toggle"]');
    if (wishBtn) {
      e.preventDefault();
      toggleWishlist(Number(wishBtn.dataset.productId)).then((active) => {
        wishBtn.classList.toggle('active', active);
        const svg = wishBtn.querySelector('svg');
        if (svg) svg.setAttribute('fill', active ? 'currentColor' : 'none');
      });
    }
  });

  window.ShopLane = {
    getCart, addToCart, updateQty, removeFromCart, clearCart,
    cartCount, cartSummary, applyCoupon,
    getWishlist, toggleWishlist, inWishlist,
    getOrders, placeOrder,
    money, stars, productCardHtml, toast,
    refreshFromServer, hasApiSession, clearLocalUserData,
  };

  document.addEventListener('DOMContentLoaded', () => {
    renderHeader();
    renderFooter();
    refreshBadges();
    if (hasApiSession()) refreshFromServer();
  });
})();
