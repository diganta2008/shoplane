/* auth.js — hybrid auth layer.
   Tries the backend API (window.ShopLaneApi) first; falls back to a pure
   localStorage simulation when the API is unreachable so the static demo
   still works standalone.

   Self-heal: on load, if we find a user marked "offline" (or a user with
   no JWT tokens at all) we clear it. Otherwise the browser stays stuck
   in a phantom "logged in" state where isLoggedIn() is true but every
   cart/order write silently goes to localStorage instead of the API. */

(function () {
  const KEY = 'shoplane.user';
  const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  function get()     { try { return JSON.parse(localStorage.getItem(KEY)); } catch { return null; } }
  function set(u)    { localStorage.setItem(KEY, JSON.stringify(u)); }
  function clear()   {
    localStorage.removeItem(KEY);
    if (window.ShopLaneApi) window.ShopLaneApi.tokens.write(null);
    if (window.ShopLane && typeof window.ShopLane.clearLocalUserData === 'function') {
      window.ShopLane.clearLocalUserData();
    }
  }
  function isLoggedIn() { return !!get(); }
  function hasApiSession() {
    return !!(window.ShopLaneApi && window.ShopLaneApi.tokens.read()?.accessToken);
  }

  function persistUser(dto) {
    const user = {
      id:    dto.id,
      email: dto.email,
      name:  dto.fullName || dto.email.split('@')[0],
      loggedInAt: new Date().toISOString(),
      profile: dto.profile || null,
    };
    set(user);
    return user;
  }

  function fallbackLogin(email, password) {
    if (!emailRe.test(email))         return { ok: false, error: 'Please enter a valid email address.' };
    if (!password || password.length < 6) return { ok: false, error: 'Password must be at least 6 characters.' };
    const user = {
      email,
      name: email.split('@')[0].replace(/[._-]/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase()),
      loggedInAt: new Date().toISOString(),
      offline: true,
    };
    set(user);
    return { ok: true, user, offline: true };
  }

  function fallbackRegister(name, email, password) {
    if (!name || name.trim().length < 2)  return { ok: false, error: 'Please enter your name.' };
    if (!emailRe.test(email))             return { ok: false, error: 'Please enter a valid email address.' };
    if (!password || password.length < 6) return { ok: false, error: 'Password must be at least 6 characters.' };
    const user = { email, name: name.trim(), loggedInAt: new Date().toISOString(), offline: true };
    set(user);
    return { ok: true, user, offline: true };
  }

  async function login(email, password) {
    if (!emailRe.test(email))         return { ok: false, error: 'Please enter a valid email address.' };
    if (!password || password.length < 6) return { ok: false, error: 'Password must be at least 6 characters.' };

    if (window.ShopLaneApi) {
      try {
        const res = await window.ShopLaneApi.login({ email, password });
        window.ShopLaneApi.tokens.write({
          accessToken:  res.data.accessToken,
          refreshToken: res.data.refreshToken,
        });
        const user = persistUser(res.data.user);
        if (window.ShopLane?.refreshFromServer) {
          try { await window.ShopLane.refreshFromServer({ mergeLocalCart: true }); } catch {}
        }
        return { ok: true, user };
      } catch (err) {
        if (err.status && err.status !== 0) {
          return { ok: false, error: err.message || 'Login failed' };
        }
      }
    }
    return fallbackLogin(email, password);
  }

  async function register(name, email, password) {
    if (!name || name.trim().length < 2)  return { ok: false, error: 'Please enter your name.' };
    if (!emailRe.test(email))             return { ok: false, error: 'Please enter a valid email address.' };
    if (!password || password.length < 6) return { ok: false, error: 'Password must be at least 6 characters.' };

    if (window.ShopLaneApi) {
      try {
        const res = await window.ShopLaneApi.register({
          email, password, fullName: name.trim(),
        });
        window.ShopLaneApi.tokens.write({
          accessToken:  res.data.accessToken,
          refreshToken: res.data.refreshToken,
        });
        const user = persistUser(res.data.user);
        if (window.ShopLane?.refreshFromServer) {
          try { await window.ShopLane.refreshFromServer({ mergeLocalCart: true }); } catch {}
        }
        return { ok: true, user };
      } catch (err) {
        if (err.status && err.status !== 0) {
          return { ok: false, error: err.message || 'Registration failed' };
        }
      }
    }
    return fallbackRegister(name, email, password);
  }

  function requireAuth(redirect) {
    if (!isLoggedIn()) {
      location.href = 'login.html' + (redirect ? '?next=' + encodeURIComponent(redirect) : '');
      return false;
    }
    return true;
  }

  /**
   * Detect broken sessions and reset them so the pill can't lie.
   *   • user present + `offline: true` and API is now reachable → clear so
   *     the user is prompted to re-register properly.
   *   • user present with no JWT tokens → same, this is the "phantom
   *     logged-in" state that hides real DB writes.
   * Fires once on DOMContentLoaded from every page.
   */
  async function selfHeal() {
    const user = get();
    if (!user) return;
    const tokensExist = hasApiSession();
    if (tokensExist) return;

    const apiUp = window.ShopLaneApi ? await window.ShopLaneApi.isReachable() : false;
    if (!apiUp) return;

    console.warn('[shoplane.auth] Stale offline session detected for %s — clearing so real login can proceed.', user.email);
    clear();
    if (window.ShopLane?.toast) {
      window.ShopLane.toast(
        'Session reset',
        'Previous offline session cleared. Please sign in again to save your cart & orders to the database.',
        'info'
      );
    }
  }

  document.addEventListener('DOMContentLoaded', () => { selfHeal(); });

  window.Auth = { get, set, clear, isLoggedIn, hasApiSession, login, register, requireAuth, selfHeal };
})();
