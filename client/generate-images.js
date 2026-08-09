/* generate-images.js — writes SVG product images / hero art into images/
   Run:  node generate-images.js                                          */
const fs = require('fs');
const path = require('path');
const OUT = path.join(__dirname, 'images');
if (!fs.existsSync(OUT)) fs.mkdirSync(OUT, { recursive: true });

function svg({ id, w = 600, h = 600, from, to, art, tag }) {
  const gradId = `g_${id}`;
  return `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${w} ${h}" width="${w}" height="${h}">
  <defs>
    <linearGradient id="${gradId}" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="${from}"/>
      <stop offset="100%" stop-color="${to}"/>
    </linearGradient>
    <filter id="shadow_${id}" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur in="SourceAlpha" stdDeviation="10"/>
      <feOffset dx="0" dy="14" result="offsetblur"/>
      <feComponentTransfer><feFuncA type="linear" slope="0.20"/></feComponentTransfer>
      <feMerge><feMergeNode/><feMergeNode in="SourceGraphic"/></feMerge>
    </filter>
  </defs>
  <rect width="${w}" height="${h}" fill="url(#${gradId})"/>
  <circle cx="${w * 0.85}" cy="${h * 0.15}" r="${w * 0.18}" fill="rgba(255,255,255,0.10)"/>
  <circle cx="${w * 0.12}" cy="${h * 0.90}" r="${w * 0.12}" fill="rgba(0,0,0,0.06)"/>
  <g filter="url(#shadow_${id})">${art}</g>
  ${tag ? `<text x="${w / 2}" y="${h - 26}" font-family="Inter, sans-serif" font-size="${w * 0.045}" font-weight="700" fill="rgba(255,255,255,0.85)" text-anchor="middle" letter-spacing="4">${tag}</text>` : ''}
</svg>`;
}

// --- product art SVG snippets ---
const art = {
  headphones: `
    <path d="M180 300 Q300 130 420 300" stroke="white" stroke-width="18" fill="none" stroke-linecap="round"/>
    <rect x="150" y="290" width="70" height="120" rx="18" fill="white"/>
    <rect x="380" y="290" width="70" height="120" rx="18" fill="white"/>
    <rect x="160" y="310" width="50" height="80" rx="10" fill="#5B21B6"/>
    <rect x="390" y="310" width="50" height="80" rx="10" fill="#5B21B6"/>`,

  watch: `
    <rect x="220" y="140" width="160" height="80" rx="22" fill="#111827"/>
    <rect x="220" y="380" width="160" height="80" rx="22" fill="#111827"/>
    <rect x="190" y="210" width="220" height="180" rx="42" fill="#0F172A" stroke="white" stroke-width="6"/>
    <rect x="215" y="235" width="170" height="130" rx="28" fill="#1E293B"/>
    <text x="300" y="290" font-family="Inter, sans-serif" font-size="42" font-weight="800" fill="white" text-anchor="middle">10</text>
    <text x="300" y="340" font-family="Inter, sans-serif" font-size="22" fill="#94A3B8" text-anchor="middle">08 : 45</text>
    <circle cx="410" cy="280" r="10" fill="#111827"/>`,

  speaker: `
    <rect x="200" y="150" width="200" height="320" rx="32" fill="#111827"/>
    <circle cx="300" cy="240" r="46" fill="#1F2937" stroke="#F97316" stroke-width="6"/>
    <circle cx="300" cy="240" r="18" fill="#F97316"/>
    <circle cx="300" cy="380" r="58" fill="#1F2937" stroke="#F97316" stroke-width="6"/>
    <circle cx="300" cy="380" r="22" fill="#F97316"/>`,

  jacket: `
    <path d="M180 180 L260 140 L260 200 L340 200 L340 140 L420 180 L400 480 L200 480 Z" fill="#78350F" stroke="#451A03" stroke-width="6"/>
    <path d="M260 140 L300 220 L340 140" stroke="#451A03" stroke-width="6" fill="none"/>
    <rect x="288" y="220" width="24" height="240" fill="#451A03"/>
    <circle cx="300" cy="270" r="7" fill="#D4A373"/>
    <circle cx="300" cy="320" r="7" fill="#D4A373"/>
    <circle cx="300" cy="370" r="7" fill="#D4A373"/>
    <circle cx="300" cy="420" r="7" fill="#D4A373"/>`,

  shoe: `
    <path d="M120 380 Q140 300 210 290 L340 260 Q440 250 460 320 Q480 380 460 420 L140 420 Q120 420 120 400 Z" fill="white" stroke="#065F46" stroke-width="6"/>
    <path d="M210 290 Q260 285 300 300 L360 320" stroke="#10B981" stroke-width="8" fill="none"/>
    <ellipse cx="180" cy="360" rx="18" ry="8" fill="#065F46"/>
    <ellipse cx="230" cy="365" rx="18" ry="8" fill="#065F46"/>
    <ellipse cx="280" cy="370" rx="18" ry="8" fill="#065F46"/>
    <rect x="140" y="420" width="320" height="12" rx="6" fill="#065F46"/>`,

  sunglasses: `
    <path d="M120 280 Q120 240 160 240 L260 240 Q300 240 300 280 L300 340 Q300 380 260 380 L160 380 Q120 380 120 340 Z" fill="#1F2937" stroke="white" stroke-width="6"/>
    <path d="M300 280 Q300 240 340 240 L440 240 Q480 240 480 280 L480 340 Q480 380 440 380 L340 380 Q300 380 300 340 Z" fill="#1F2937" stroke="white" stroke-width="6"/>
    <line x1="300" y1="280" x2="300" y2="340" stroke="white" stroke-width="6"/>
    <path d="M140 265 Q210 255 280 285 L280 320 Q210 300 140 320 Z" fill="rgba(255,255,255,0.25)"/>
    <path d="M320 265 Q390 255 460 285 L460 320 Q390 300 320 320 Z" fill="rgba(255,255,255,0.25)"/>`,

  coffee: `
    <path d="M180 230 L180 400 Q180 460 240 460 L340 460 Q400 460 400 400 L400 230 Z" fill="white" stroke="#78350F" stroke-width="6"/>
    <path d="M400 260 Q470 260 470 320 Q470 380 400 380" fill="none" stroke="#78350F" stroke-width="10"/>
    <rect x="220" y="180" width="140" height="50" rx="10" fill="#78350F"/>
    <path d="M240 180 Q240 150 260 150" stroke="#F59E0B" stroke-width="4" fill="none" opacity="0.7"/>
    <path d="M290 180 Q290 145 320 145" stroke="#F59E0B" stroke-width="4" fill="none" opacity="0.7"/>
    <path d="M340 180 Q340 155 360 155" stroke="#F59E0B" stroke-width="4" fill="none" opacity="0.7"/>
    <circle cx="290" cy="320" r="20" fill="#78350F" opacity="0.15"/>`,

  lamp: `
    <path d="M300 150 L220 300 L380 300 Z" fill="white" stroke="#9F1239" stroke-width="6"/>
    <rect x="295" y="300" width="10" height="120" fill="#9F1239"/>
    <ellipse cx="300" cy="440" rx="90" ry="20" fill="#9F1239"/>
    <circle cx="300" cy="320" r="18" fill="#FDE68A" opacity="0.9"/>
    <ellipse cx="300" cy="325" rx="120" ry="90" fill="#FDE68A" opacity="0.15"/>`,

  knives: `
    <path d="M140 320 L360 260 L410 280 L390 340 L150 340 Z" fill="#E5E7EB" stroke="#374151" stroke-width="4"/>
    <rect x="380" y="270" width="80" height="70" rx="10" fill="#111827"/>
    <path d="M170 380 L390 380 L430 400 L410 460 L170 460 Z" fill="#F3F4F6" stroke="#374151" stroke-width="4"/>
    <rect x="400" y="390" width="80" height="70" rx="10" fill="#111827"/>
    <line x1="400" y1="300" x2="450" y2="300" stroke="#374151" stroke-width="3"/>
    <line x1="420" y1="420" x2="470" y2="420" stroke="#374151" stroke-width="3"/>`,

  bookJS: `
    <rect x="180" y="140" width="240" height="320" rx="14" fill="#FBBF24"/>
    <rect x="180" y="140" width="16" height="320" fill="#D97706"/>
    <text x="310" y="290" font-family="Inter, sans-serif" font-size="120" font-weight="900" fill="#111827" text-anchor="middle">JS</text>
    <text x="310" y="360" font-family="Inter, sans-serif" font-size="20" font-weight="700" fill="#111827" text-anchor="middle">JAVASCRIPT</text>
    <text x="310" y="390" font-family="Inter, sans-serif" font-size="14" font-weight="500" fill="#111827" text-anchor="middle">Complete Guide</text>`,

  bookDesign: `
    <rect x="180" y="140" width="240" height="320" rx="14" fill="white"/>
    <rect x="180" y="140" width="16" height="320" fill="#5B21B6"/>
    <circle cx="300" cy="260" r="55" fill="none" stroke="#5B21B6" stroke-width="8"/>
    <circle cx="270" cy="240" r="35" fill="none" stroke="#F59E0B" stroke-width="8"/>
    <circle cx="330" cy="280" r="35" fill="none" stroke="#EC4899" stroke-width="8"/>
    <text x="310" y="380" font-family="Inter, sans-serif" font-size="24" font-weight="800" fill="#111827" text-anchor="middle">DESIGN</text>
    <text x="310" y="410" font-family="Inter, sans-serif" font-size="14" fill="#64748B" text-anchor="middle">Handbook 2026</text>`,

  novels: `
    <rect x="150" y="200" width="70" height="260" rx="6" fill="#0F766E"/>
    <rect x="228" y="180" width="70" height="280" rx="6" fill="#B45309"/>
    <rect x="306" y="220" width="70" height="240" rx="6" fill="#7C2D12"/>
    <rect x="384" y="200" width="70" height="260" rx="6" fill="#164E63"/>
    <text x="185" y="330" font-family="Inter, sans-serif" font-size="12" font-weight="700" fill="white" text-anchor="middle" transform="rotate(-90 185 330)">CLASSIC</text>
    <text x="263" y="320" font-family="Inter, sans-serif" font-size="12" font-weight="700" fill="white" text-anchor="middle" transform="rotate(-90 263 320)">MODERN</text>
    <text x="341" y="340" font-family="Inter, sans-serif" font-size="12" font-weight="700" fill="white" text-anchor="middle" transform="rotate(-90 341 340)">NOVELS</text>
    <text x="419" y="330" font-family="Inter, sans-serif" font-size="12" font-weight="700" fill="white" text-anchor="middle" transform="rotate(-90 419 330)">CLASSIC</text>`,
};

const products = [
  { id: 'headphones',     from: '#7C3AED', to: '#4C1D95', art: art.headphones,  tag: 'PREMIUM AUDIO' },
  { id: 'smartwatch',     from: '#0EA5E9', to: '#1E40AF', art: art.watch,       tag: 'SMART SERIES 8' },
  { id: 'speaker',        from: '#F97316', to: '#C2410C', art: art.speaker,     tag: 'BASS MAX' },
  { id: 'leather-jacket', from: '#B45309', to: '#78350F', art: art.jacket,      tag: 'GENUINE LEATHER' },
  { id: 'running-shoes',  from: '#10B981', to: '#065F46', art: art.shoe,        tag: 'AIR RUNNER' },
  { id: 'sunglasses',     from: '#F59E0B', to: '#B45309', art: art.sunglasses,  tag: 'UV 400' },
  { id: 'coffee-maker',   from: '#D97706', to: '#7C2D12', art: art.coffee,      tag: 'BARISTA PRO' },
  { id: 'table-lamp',     from: '#F43F5E', to: '#9F1239', art: art.lamp,        tag: 'AMBIENT GLOW' },
  { id: 'knives-set',     from: '#64748B', to: '#0F172A', art: art.knives,      tag: 'PROFESSIONAL' },
  { id: 'js-book',        from: '#FCD34D', to: '#D97706', art: art.bookJS,      tag: 'BESTSELLER' },
  { id: 'design-book',    from: '#A78BFA', to: '#5B21B6', art: art.bookDesign,  tag: 'AWARD WINNER' },
  { id: 'novel-set',      from: '#2DD4BF', to: '#0F766E', art: art.novels,      tag: 'BOX SET' },
];

products.forEach((p) => {
  const filename = `product-${p.id}.svg`;
  fs.writeFileSync(path.join(OUT, filename), svg(p));
});

// hero illustration — shopping bags + phone
const heroSvg = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 520" width="640" height="520">
  <defs>
    <linearGradient id="hg1" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#7C3AED"/><stop offset="100%" stop-color="#4C1D95"/>
    </linearGradient>
    <linearGradient id="hg2" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#F59E0B"/><stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
    <linearGradient id="hg3" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#10B981"/><stop offset="100%" stop-color="#059669"/>
    </linearGradient>
  </defs>

  <!-- backdrop blob -->
  <path d="M60 260 Q60 60 300 60 Q560 60 590 240 Q620 420 400 460 Q180 500 120 400 Q60 320 60 260 Z"
        fill="#EEF2FF" opacity="0.9"/>
  <circle cx="530" cy="90" r="26" fill="#F59E0B" opacity="0.5"/>
  <circle cx="90"  cy="410" r="20" fill="#10B981" opacity="0.5"/>

  <!-- Phone / storefront -->
  <rect x="220" y="90" width="220" height="360" rx="30" fill="white" stroke="#E2E8F0" stroke-width="4"/>
  <rect x="240" y="110" width="180" height="320" rx="18" fill="#F5F3FF"/>
  <rect x="260" y="130" width="140" height="90" rx="12" fill="url(#hg1)"/>
  <text x="330" y="175" font-family="Inter, sans-serif" font-size="18" font-weight="800" fill="white" text-anchor="middle">SUMMER SALE</text>
  <text x="330" y="200" font-family="Inter, sans-serif" font-size="12" fill="white" opacity="0.85" text-anchor="middle">Up to 50% OFF</text>

  <rect x="260" y="235" width="65" height="85" rx="10" fill="#DDD6FE"/>
  <circle cx="292" cy="270" r="18" fill="#5B21B6"/>
  <rect x="266" y="300" width="53" height="4" rx="2" fill="#7C3AED"/>
  <rect x="266" y="308" width="35" height="4" rx="2" fill="#A78BFA"/>

  <rect x="335" y="235" width="65" height="85" rx="10" fill="#FED7AA"/>
  <circle cx="367" cy="270" r="18" fill="#EA580C"/>
  <rect x="341" y="300" width="53" height="4" rx="2" fill="#C2410C"/>
  <rect x="341" y="308" width="35" height="4" rx="2" fill="#F97316"/>

  <rect x="260" y="335" width="140" height="34" rx="10" fill="#5B21B6"/>
  <text x="330" y="357" font-family="Inter, sans-serif" font-size="13" font-weight="700" fill="white" text-anchor="middle">Add to Cart</text>
  <rect x="260" y="380" width="140" height="34" rx="10" fill="#F5F3FF" stroke="#5B21B6" stroke-width="2"/>
  <text x="330" y="402" font-family="Inter, sans-serif" font-size="13" font-weight="700" fill="#5B21B6" text-anchor="middle">Buy Now</text>

  <!-- Shopping bag left -->
  <path d="M60 300 L80 260 Q90 240 110 240 L150 240 Q170 240 180 260 L200 300 L200 440 Q200 460 180 460 L80 460 Q60 460 60 440 Z" fill="url(#hg2)"/>
  <path d="M100 260 Q100 220 130 220 Q160 220 160 260" stroke="#78350F" stroke-width="5" fill="none"/>
  <text x="130" y="360" font-family="Inter, sans-serif" font-size="18" font-weight="900" fill="white" text-anchor="middle">SHOP</text>

  <!-- Shopping bag right -->
  <path d="M460 320 L478 285 Q486 268 502 268 L540 268 Q556 268 564 285 L582 320 L582 445 Q582 462 566 462 L476 462 Q460 462 460 445 Z" fill="url(#hg3)"/>
  <path d="M498 285 Q498 250 520 250 Q542 250 542 285" stroke="#065F46" stroke-width="5" fill="none"/>
  <circle cx="521" cy="370" r="18" fill="white" opacity="0.85"/>
  <text x="521" y="376" font-family="Inter, sans-serif" font-size="16" font-weight="900" fill="#065F46" text-anchor="middle">%</text>

  <!-- Sparkles -->
  <path d="M170 90 l6 12 l12 6 l-12 6 l-6 12 l-6 -12 l-12 -6 l12 -6 z" fill="#F59E0B"/>
  <path d="M480 470 l4 8 l8 4 l-8 4 l-4 8 l-4 -8 l-8 -4 l8 -4 z" fill="#7C3AED"/>
</svg>`;
fs.writeFileSync(path.join(OUT, 'hero-shopping.svg'), heroSvg);

// Simple logo
const logoSvg = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64" width="64" height="64">
  <defs>
    <linearGradient id="lg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#7C3AED"/><stop offset="100%" stop-color="#5B21B6"/>
    </linearGradient>
  </defs>
  <rect width="64" height="64" rx="14" fill="url(#lg)"/>
  <path d="M18 22 L46 22 L42 46 L22 46 Z" fill="none" stroke="white" stroke-width="4" stroke-linejoin="round"/>
  <circle cx="26" cy="52" r="3" fill="white"/>
  <circle cx="40" cy="52" r="3" fill="white"/>
  <path d="M14 18 L20 18 L22 22" stroke="white" stroke-width="4" stroke-linecap="round" fill="none"/>
</svg>`;
fs.writeFileSync(path.join(OUT, 'logo.svg'), logoSvg);

// Empty-cart illustration
const emptyCart = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 240 180" width="240" height="180">
  <circle cx="120" cy="90" r="80" fill="#F5F3FF"/>
  <path d="M70 60 L84 60 L100 130 L180 130" stroke="#5B21B6" stroke-width="6" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="M100 130 Q92 90 106 78 L172 78 L164 118 L112 118" stroke="#5B21B6" stroke-width="6" fill="none" stroke-linejoin="round"/>
  <circle cx="112" cy="150" r="8" fill="#5B21B6"/>
  <circle cx="170" cy="150" r="8" fill="#5B21B6"/>
  <line x1="130" y1="88" x2="150" y2="108" stroke="#EF4444" stroke-width="4" stroke-linecap="round"/>
  <line x1="150" y1="88" x2="130" y2="108" stroke="#EF4444" stroke-width="4" stroke-linecap="round"/>
</svg>`;
fs.writeFileSync(path.join(OUT, 'empty-cart.svg'), emptyCart);

// Success check illustration
const successArt = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 240 180" width="240" height="180">
  <circle cx="120" cy="90" r="80" fill="#DCFCE7"/>
  <circle cx="120" cy="90" r="50" fill="#10B981"/>
  <path d="M96 92 L112 108 L146 74" stroke="white" stroke-width="10" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
  <circle cx="46" cy="46" r="6" fill="#10B981" opacity="0.5"/>
  <circle cx="200" cy="60" r="8" fill="#F59E0B" opacity="0.5"/>
  <circle cx="60" cy="150" r="8" fill="#5B21B6" opacity="0.5"/>
</svg>`;
fs.writeFileSync(path.join(OUT, 'order-success.svg'), successArt);

console.log('Generated', products.length + 4, 'images ->', OUT);
