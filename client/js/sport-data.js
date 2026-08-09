/* sport-data.js
 * ----------------------------------------------------------------
 * Reference data for the ShopLane Sport vertical. Kept separate so
 * automation tests can import it directly, e.g.:
 *
 *   const running = window.SP_BLOG.filter(b => b.category === 'Running');
 *
 * Products live in window.CATALOG under category: 'Sports' — this file
 * only provides the disciplines strip, news blog, and a scoreboard.
 * All content is fictional and safe for demos.
 * ---------------------------------------------------------------- */

window.SP_DISCIPLINES = [
  { id: 'running',    name: 'Running',    icon: '🏃' },
  { id: 'fitness',    name: 'Fitness',    icon: '⌚' },
  { id: 'yoga',       name: 'Yoga',       icon: '🧘' },
  { id: 'strength',   name: 'Strength',   icon: '🏋️' },
  { id: 'cycling',    name: 'Cycling',    icon: '🚴' },
  { id: 'football',   name: 'Football',   icon: '⚽' },
  { id: 'cricket',    name: 'Cricket',    icon: '🏏' },
  { id: 'basketball', name: 'Basketball', icon: '🏀' },
  { id: 'tennis',     name: 'Tennis',     icon: '🎾' },
  { id: 'swimming',   name: 'Swimming',   icon: '🏊' },
];

/* Upcoming and live match scoreboard — fake but plausible. */
window.SP_MATCHES = [
  {
    id: 1, league: 'ICC T20 World Cup',
    home: 'India',   homeScore: '184/6 (20)',
    away: 'Australia', awayScore: '176/9 (20)',
    status: 'live',   statusText: 'Live · India won by 8 runs',
    startsIso: '2026-08-09T14:00:00Z',
    sport: 'Cricket',
  },
  {
    id: 2, league: 'UEFA Champions League',
    home: 'Real Madrid', homeScore: '2',
    away: 'Manchester City', awayScore: '1',
    status: 'live',   statusText: 'Live · 74′',
    startsIso: '2026-08-09T19:00:00Z',
    sport: 'Football',
  },
  {
    id: 3, league: 'NBA Summer League',
    home: 'LA Lakers', homeScore: '—',
    away: 'Boston Celtics', awayScore: '—',
    status: 'upcoming', statusText: 'Tomorrow · 7:30 AM IST',
    startsIso: '2026-08-10T02:00:00Z',
    sport: 'Basketball',
  },
  {
    id: 4, league: 'Wimbledon',
    home: 'C. Alcaraz', homeScore: '6, 7, 3',
    away: 'J. Sinner',  awayScore: '4, 6, 5',
    status: 'live',   statusText: 'Live · 4th set',
    startsIso: '2026-08-09T13:00:00Z',
    sport: 'Tennis',
  },
];

/* Sport news / editorial blog. */
window.SP_BLOG = [
  {
    id: 1, slug: 'marathon-training-block',
    title: '16-Week Marathon Training Block for First-Timers',
    author: 'Coach Aditi Iyer',
    date: '2026-08-06',
    readMin: 8,
    category: 'Running',
    hero: '../images/blog-fitness.svg',
    excerpt: 'A conservative, injury-proof plan built around three key sessions a week — long run, tempo, and easy — plus how to layer strength on top without overcooking your legs.',
  },
  {
    id: 2, slug: 'ai-in-cricket-coaching',
    title: 'How AI Is Quietly Rewriting Cricket Coaching',
    author: 'Rahul Iyengar',
    date: '2026-08-04',
    readMin: 6,
    category: 'Cricket',
    hero: '../images/blog-neuro.svg',
    excerpt: 'From bowl-tracking cameras that flag a subtle wrist rotation to laptop-based decision-review simulators, technology has moved from the broadcast booth into the nets — and it is measurably improving outcomes.',
  },
  {
    id: 3, slug: 'monsoon-cycling-safety',
    title: 'Cycling in Monsoon: Kit, Tyres, and Techniques That Keep You Upright',
    author: 'Vikram Bose',
    date: '2026-08-02',
    readMin: 5,
    category: 'Cycling',
    hero: '../images/blog-cardio.svg',
    excerpt: 'Painted road-markings, oil films at signals, and metal manhole covers are silent killers when wet. A pro mechanic and a national-level roadie share their monsoon-riding checklist.',
  },
  {
    id: 4, slug: 'yoga-for-runners',
    title: '5 Yoga Poses That Actually Improve Runner\u2019s Mobility',
    author: 'Anandi Nair',
    date: '2026-07-30',
    readMin: 4,
    category: 'Yoga',
    hero: '../images/blog-nutrition.svg',
    excerpt: 'Skip the Instagram bendy stuff. These five poses target the hip flexors, calves, and thoracic spine — the three chokepoints most amateur runners plateau on.',
  },
  {
    id: 5, slug: 'lifting-after-40',
    title: 'Building Muscle After 40: What Sports Science Actually Says',
    author: 'Dr. Kavya Menon',
    date: '2026-07-27',
    readMin: 7,
    category: 'Strength',
    hero: '../images/blog-fitness.svg',
    excerpt: 'Sarcopenia is real, but so is your capacity to build muscle at 45, 55, or 65. A sports-medicine physician walks through protein targets, recovery windows, and the exercises that punch above their weight.',
  },
  {
    id: 6, slug: 'olympic-qualification-tracker',
    title: 'The Olympic Qualification Tracker: Who\u2019s In, Who\u2019s Chasing',
    author: 'Rhea Kapoor',
    date: '2026-07-25',
    readMin: 6,
    category: 'Olympics',
    hero: '../images/blog-cardio.svg',
    excerpt: 'Weekly rundown of the athletes who booked their spot, who\u2019s in the final qualification window, and the surprise stories emerging from continental championships.',
  },
];

window.getSportBlog = function (slugOrId) {
  const list = window.SP_BLOG || [];
  return list.find((b) => b.slug === slugOrId || b.id === Number(slugOrId));
};
