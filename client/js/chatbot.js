/* chatbot.js — ShopLane AI Chat widget.
   Self-contained floating bubble + panel. Talks to POST {API_BASE}/chat.
   Resolves the API base URL the same way api.js does:
     1) window.SHOPLANE_API_BASE  (set by pages.yml at deploy time)
     2) <meta name="shoplane-api" content="...">
     3) fallback: <location.protocol>//<location.hostname>:8080/api/v1
*/
(function () {
  if (window.__shoplaneChatbotLoaded) return;
  window.__shoplaneChatbotLoaded = true;

  const HISTORY_KEY = 'shoplane.chat.history';
  const OPEN_KEY    = 'shoplane.chat.open';
  const MAX_TURNS_STORED = 20;

  function resolveBase() {
    const meta = document.querySelector('meta[name="shoplane-api"]');
    return (window.SHOPLANE_API_BASE)
        || (meta && meta.content)
        || (location.protocol + '//' + location.hostname + ':8080/api/v1');
  }
  const API_BASE = resolveBase();

  const styles = `
    .sl-chat-bubble{position:fixed;bottom:20px;right:20px;width:56px;height:56px;
      border-radius:50%;background:#0d6efd;color:#fff;border:0;cursor:pointer;
      box-shadow:0 6px 20px rgba(0,0,0,.25);display:flex;align-items:center;
      justify-content:center;z-index:9999;transition:transform .2s ease}
    .sl-chat-bubble:hover{transform:scale(1.06)}
    .sl-chat-bubble svg{width:24px;height:24px}
    .sl-chat-panel{position:fixed;bottom:88px;right:20px;width:360px;max-width:calc(100vw - 40px);
      height:520px;max-height:calc(100vh - 120px);background:#fff;
      border-radius:14px;box-shadow:0 12px 40px rgba(0,0,0,.22);
      display:none;flex-direction:column;overflow:hidden;z-index:9999;
      font-family:Inter,system-ui,-apple-system,sans-serif;color:#1a1a1a}
    .sl-chat-panel.open{display:flex;animation:sl-slide-up .2s ease}
    @keyframes sl-slide-up{from{opacity:0;transform:translateY(12px)}to{opacity:1;transform:none}}
    .sl-chat-head{padding:14px 16px;background:linear-gradient(135deg,#0d6efd,#0850c9);
      color:#fff;display:flex;align-items:center;justify-content:space-between}
    .sl-chat-head strong{font-size:15px;font-weight:600}
    .sl-chat-head small{display:block;font-size:11px;opacity:.85;margin-top:2px;font-weight:400}
    .sl-chat-head-btns{display:flex;gap:4px}
    .sl-chat-head button{background:transparent;border:0;color:#fff;cursor:pointer;
      padding:6px;border-radius:6px;display:flex;align-items:center}
    .sl-chat-head button:hover{background:rgba(255,255,255,.15)}
    .sl-chat-body{flex:1;overflow-y:auto;padding:14px;background:#f7f8fa;
      display:flex;flex-direction:column;gap:10px}
    .sl-msg{max-width:80%;padding:9px 12px;border-radius:12px;font-size:14px;
      line-height:1.4;word-wrap:break-word;white-space:pre-wrap}
    .sl-msg.user{align-self:flex-end;background:#0d6efd;color:#fff;border-bottom-right-radius:4px}
    .sl-msg.bot{align-self:flex-start;background:#fff;color:#1a1a1a;
      border:1px solid #e6e8ec;border-bottom-left-radius:4px}
    .sl-msg.err{align-self:center;background:#fff2f2;color:#b42318;
      border:1px solid #fecdca;font-size:12.5px}
    .sl-typing{align-self:flex-start;background:#fff;color:#666;
      border:1px solid #e6e8ec;padding:9px 12px;border-radius:12px;
      border-bottom-left-radius:4px;font-size:14px}
    .sl-typing span{display:inline-block;width:6px;height:6px;margin:0 1px;
      background:#999;border-radius:50%;animation:sl-blink 1.2s infinite}
    .sl-typing span:nth-child(2){animation-delay:.2s}
    .sl-typing span:nth-child(3){animation-delay:.4s}
    @keyframes sl-blink{0%,80%,100%{opacity:.3}40%{opacity:1}}
    .sl-suggestions{display:flex;flex-wrap:wrap;gap:6px;margin:2px 0 4px 0;
      align-self:flex-start;max-width:100%}
    .sl-suggestions.starter{align-self:stretch}
    .sl-chip{background:#fff;border:1px solid #cfd6de;color:#0d6efd;font-family:inherit;
      font-size:12.5px;line-height:1.2;padding:6px 10px;border-radius:999px;
      cursor:pointer;transition:background .15s ease,border-color .15s ease}
    .sl-chip:hover{background:#eef4ff;border-color:#0d6efd}
    .sl-chip:disabled{opacity:.55;cursor:not-allowed}
    .sl-chat-foot{padding:10px 12px;border-top:1px solid #e6e8ec;background:#fff;
      display:flex;gap:8px;align-items:flex-end}
    .sl-chat-foot textarea{flex:1;resize:none;border:1px solid #d5d9df;border-radius:10px;
      padding:8px 12px;font-family:inherit;font-size:14px;line-height:1.4;max-height:96px;outline:none}
    .sl-chat-foot textarea:focus{border-color:#0d6efd;box-shadow:0 0 0 3px rgba(13,110,253,.15)}
    .sl-chat-foot button{background:#0d6efd;color:#fff;border:0;border-radius:10px;
      padding:0 14px;height:38px;cursor:pointer;font-weight:500;font-size:14px}
    .sl-chat-foot button:disabled{background:#a8bff6;cursor:not-allowed}
    @media (max-width:480px){
      .sl-chat-panel{right:10px;left:10px;bottom:80px;width:auto;max-width:none;
        height:calc(100vh - 100px)}
      .sl-chat-bubble{bottom:14px;right:14px}
    }
  `;
  const styleTag = document.createElement('style');
  styleTag.textContent = styles;
  document.head.appendChild(styleTag);

  const bubble = document.createElement('button');
  bubble.className = 'sl-chat-bubble';
  bubble.setAttribute('aria-label', 'Open chat');
  bubble.setAttribute('data-testid', 'chatbot-bubble');
  bubble.innerHTML = `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"
         stroke-linecap="round" stroke-linejoin="round">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
    </svg>`;

  const panel = document.createElement('div');
  panel.className = 'sl-chat-panel';
  panel.setAttribute('role', 'dialog');
  panel.setAttribute('aria-label', 'ShopLane Assistant');
  panel.innerHTML = `
    <div class="sl-chat-head">
      <div>
        <strong>ShopLane Assistant</strong>
        <small>AI · ask about products, orders, or shipping</small>
      </div>
      <div class="sl-chat-head-btns">
        <button type="button" aria-label="Clear conversation" data-testid="chatbot-clear" title="Clear conversation">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"
               stroke-linecap="round" stroke-linejoin="round">
            <polyline points="3 6 5 6 21 6"/>
            <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
            <path d="M10 11v6M14 11v6"/>
          </svg>
        </button>
        <button type="button" aria-label="Close chat" data-testid="chatbot-close">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"
               stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>
    </div>
    <div class="sl-chat-body" data-testid="chatbot-body"></div>
    <form class="sl-chat-foot">
      <textarea rows="1" placeholder="Ask about products, shipping, returns…"
                data-testid="chatbot-input" maxlength="1000"></textarea>
      <button type="submit" data-testid="chatbot-send">Send</button>
    </form>`;

  document.body.appendChild(bubble);
  document.body.appendChild(panel);

  const body   = panel.querySelector('.sl-chat-body');
  const form   = panel.querySelector('form');
  const input  = panel.querySelector('textarea');
  const send   = panel.querySelector('button[type="submit"]');
  const closeB = panel.querySelector('[data-testid="chatbot-close"]');
  const clearB = panel.querySelector('[data-testid="chatbot-clear"]');

  function loadHistory() {
    try { return JSON.parse(localStorage.getItem(HISTORY_KEY)) || []; }
    catch { return []; }
  }
  function saveHistory(h) {
    const trimmed = h.slice(-MAX_TURNS_STORED * 2);
    try { localStorage.setItem(HISTORY_KEY, JSON.stringify(trimmed)); } catch { /* quota */ }
  }
  function addBubble(role, text) {
    const el = document.createElement('div');
    el.className = 'sl-msg ' + (role === 'user' ? 'user' : role === 'error' ? 'err' : 'bot');
    el.textContent = text;
    body.appendChild(el);
    body.scrollTop = body.scrollHeight;
    return el;
  }
  function addTyping() {
    const el = document.createElement('div');
    el.className = 'sl-typing';
    el.setAttribute('data-testid', 'chatbot-typing');
    el.innerHTML = '<span></span><span></span><span></span>';
    body.appendChild(el);
    body.scrollTop = body.scrollHeight;
    return el;
  }

  function clearSuggestions() {
    body.querySelectorAll('.sl-suggestions').forEach(el => el.remove());
  }

  function renderSuggestions(items, opts) {
    if (!items || items.length === 0) return;
    clearSuggestions();
    const wrap = document.createElement('div');
    wrap.className = 'sl-suggestions' + (opts && opts.starter ? ' starter' : '');
    wrap.setAttribute('data-testid', 'chatbot-suggestions');
    for (const q of items.slice(0, 6)) {
      const chip = document.createElement('button');
      chip.type = 'button';
      chip.className = 'sl-chip';
      chip.textContent = q;
      chip.addEventListener('click', () => {
        clearSuggestions();
        ask(q);
      });
      wrap.appendChild(chip);
    }
    body.appendChild(wrap);
    body.scrollTop = body.scrollHeight;
  }

  const STARTER_FALLBACK = [
    "How long does shipping take?",
    "What's your return policy?",
    "Any coupon codes?",
    "Best-selling headphones",
    "Recommend a laptop",
    "How do I track my order?"
  ];

  async function fetchStarterSuggestions() {
    try {
      const res = await fetch(API_BASE + '/chat/suggestions', { headers: { 'Accept': 'application/json' } });
      if (!res.ok) return STARTER_FALLBACK;
      const json = await res.json();
      const items = json && json.data;
      return (Array.isArray(items) && items.length) ? items : STARTER_FALLBACK;
    } catch { return STARTER_FALLBACK; }
  }

  async function rehydrate() {
    body.innerHTML = '';
    const h = loadHistory();
    if (h.length === 0) {
      addBubble('bot', "Hi! I'm the ShopLane assistant. Ask me about products, orders, shipping, or returns.");
      renderSuggestions(await fetchStarterSuggestions(), { starter: true });
      return;
    }
    for (const m of h) addBubble(m.role, m.content);
  }

  async function ask(text) {
    const history = loadHistory();
    history.push({ role: 'user', content: text });
    saveHistory(history);
    clearSuggestions();
    addBubble('user', text);

    send.disabled = true;
    input.disabled = true;
    const typing = addTyping();

    try {
      const res = await fetch(API_BASE + '/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        body: JSON.stringify({ message: text, history }),
      });
      typing.remove();

      const json = await res.json().catch(() => null);
      if (!res.ok) {
        const code = json && json.error && json.error.code;
        let msg;
        if (code === 'CHAT_DISABLED') {
          msg = "AI chat isn't enabled for this demo yet. The site owner needs to configure an OPENAI_API_KEY on the server.";
          input.disabled = true;
          input.placeholder = 'Chat unavailable';
          send.disabled = true;
        } else if (code === 'CHAT_RATE_LIMITED') {
          msg = "I'm getting a lot of traffic right now — give me a moment and try again.";
        } else if (code === 'CHAT_UPSTREAM_ERROR') {
          msg = "The AI service is temporarily unavailable. Please try again in a bit.";
        } else {
          msg = (json && json.error && json.error.message)
                || ('Chat failed (HTTP ' + res.status + ')');
        }
        addBubble('error', msg);
        return;
      }
      const reply = json && json.data && json.data.reply;
      if (!reply) { addBubble('error', 'Chat returned an empty reply.'); return; }

      history.push({ role: 'assistant', content: reply });
      saveHistory(history);
      addBubble('bot', reply);
      const suggs = json && json.data && json.data.suggestions;
      if (Array.isArray(suggs)) renderSuggestions(suggs);
    } catch (e) {
      typing.remove();
      addBubble('error', 'Cannot reach the chat backend. Is the API running?');
    } finally {
      send.disabled = false;
      input.disabled = false;
      input.focus();
    }
  }

  bubble.addEventListener('click', () => {
    panel.classList.add('open');
    sessionStorage.setItem(OPEN_KEY, '1');
    setTimeout(() => input.focus(), 60);
  });
  closeB.addEventListener('click', () => {
    panel.classList.remove('open');
    sessionStorage.removeItem(OPEN_KEY);
  });
  clearB.addEventListener('click', () => {
    if (!confirm('Clear this conversation?')) return;
    localStorage.removeItem(HISTORY_KEY);
    rehydrate();
  });

  input.addEventListener('input', () => {
    input.style.height = 'auto';
    input.style.height = Math.min(input.scrollHeight, 96) + 'px';
  });
  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      form.requestSubmit();
    }
  });
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const text = input.value.trim();
    if (!text) return;
    input.value = '';
    input.style.height = 'auto';
    ask(text);
  });

  rehydrate();
  if (sessionStorage.getItem(OPEN_KEY) === '1') {
    panel.classList.add('open');
  }
})();
