/**
 * Real India gold medalists from the 2026 Commonwealth Games in Glasgow
 * (23 July – 2 August 2026). India finished 4th with 13 gold, 17 silver
 * and 9 bronze medals (source: The Hindu, JagranJosh, en.wikipedia.org).
 *
 * `photo` is a Wikimedia-Commons hosted image where we could verify one
 * exists via the Wikipedia REST summary API; empty string otherwise —
 * the UI falls back to a gold initials avatar in that case. Each card
 * still links to Wikipedia so the actual photo is one click away.
 */
window.CWG_MEDALISTS = [
  {
    id:      'mirabai-chanu',
    name:    'Mirabai Chanu',
    sport:   'Weightlifting',
    event:   "Women's 48 kg",
    date:    '2026-07-26',
    photo:   'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Saikhom_Mirabai_Chanu.jpg/330px-Saikhom_Mirabai_Chanu.jpg',
    wiki:    'https://en.wikipedia.org/wiki/Mirabai_Chanu',
    country: 'India',
    tag:     'First gold for India in Glasgow',
  },
  {
    id:      'sharmila-dhankar',
    name:    'Sharmila Dhankar',
    sport:   'Para Athletics',
    event:   "Women's Shot Put F57",
    date:    '2026-07-27',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Sharmila_(athlete)',
    country: 'India',
    tag:     'India\u2019s first para-athletics gold at CWG',
  },
  {
    id:      'dilip-gavit',
    name:    'Dilip Mahadu Gavit',
    sport:   'Para Athletics',
    event:   "Men's 100 m T47",
    date:    '2026-07-29',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Dilip_Gavit',
    country: 'India',
  },
  {
    id:      'asmita-dey',
    name:    'Asmita Dey',
    sport:   'Judo',
    event:   "Women's 48 kg",
    date:    '2026-07-31',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Asmita_Dey',
    country: 'India',
    tag:     'India\u2019s first-ever CWG judo gold',
  },
  {
    id:      'harsh-singh',
    name:    'Harsh Singh',
    sport:   'Judo',
    event:   "Men's 60 kg",
    date:    '2026-07-31',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Harsh_Singh_(judoka)',
    country: 'India',
  },
  {
    id:      'soman-rana',
    name:    'Soman Rana',
    sport:   'Para Athletics',
    event:   "Men's Shot Put F57",
    date:    '2026-08-01',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Soman_Rana',
    country: 'India',
  },
  {
    id:      'preeti-pawar',
    name:    'Preeti Pawar',
    sport:   'Boxing',
    event:   "Women's 54 kg",
    date:    '2026-08-01',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Preeti_Panwar',
    country: 'India',
  },
  {
    id:      'jaismine-lamboria',
    name:    'Jaismine Lamboria',
    sport:   'Boxing',
    event:   "Women's 57 kg",
    date:    '2026-08-01',
    photo:   'https://upload.wikimedia.org/wikipedia/commons/c/c6/Jaismine_Lamboria.jpg',
    wiki:    'https://en.wikipedia.org/wiki/Jaismine_Lamboria',
    country: 'India',
  },
  {
    id:      'sakshi-chaudhary',
    name:    'Sakshi Chaudhary',
    sport:   'Boxing',
    event:   "Women's 51 kg",
    date:    '2026-08-01',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Sakshi_Chaudhary_(boxer)',
    country: 'India',
  },
  {
    id:      'priya-ghanghas',
    name:    'Priya Ghanghas',
    sport:   'Boxing',
    event:   "Women's 60 kg",
    date:    '2026-08-01',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Priya_Ghanghas',
    country: 'India',
  },
  {
    id:      'arundhati-choudhary',
    name:    'Arundhati Choudhary',
    sport:   'Boxing',
    event:   "Women's 70 kg",
    date:    '2026-08-01',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Arundhati_Choudhary',
    country: 'India',
  },
  {
    id:      'sachin-siwach',
    name:    'Sachin Siwach',
    sport:   'Boxing',
    event:   "Men's 60 kg",
    date:    '2026-08-01',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Sachin_Siwach',
    country: 'India',
  },
  {
    id:      'ankush-panghal',
    name:    'Ankush Panghal',
    sport:   'Boxing',
    event:   "Men's 80 kg",
    date:    '2026-08-01',
    photo:   '',
    wiki:    'https://en.wikipedia.org/wiki/Ankush_Panghal',
    country: 'India',
  },
];

/** Distinct sport list — used to build filter chips. */
window.CWG_SPORTS = (function() {
  const seen = {};
  window.CWG_MEDALISTS.forEach((m) => { seen[m.sport] = true; });
  return Object.keys(seen);
})();
