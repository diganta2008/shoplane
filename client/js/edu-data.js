/* ==========================================================================
   Education vertical — static data used by pages/education.html
   Kept in a separate file (loaded before app.js) so the landing page stays
   declarative and easy for automation to read.
   ========================================================================== */

/** Filter chips that drive the "Institutes" grid. Keep ids URL-safe. */
window.EDU_STREAMS = [
  { id: 'all',         label: 'All streams',   icon: '🎯' },
  { id: 'engineering', label: 'Engineering',   icon: '⚙️' },
  { id: 'medical',     label: 'Medical',       icon: '⚕️' },
  { id: 'upsc',        label: 'UPSC / Civil',  icon: '🏛️' },
  { id: 'coding',      label: 'Coding & AI',   icon: '💻' },
  { id: 'k12',         label: 'School (K–12)', icon: '🏫' },
  { id: 'languages',   label: 'Languages',     icon: '🗣️' },
  { id: 'mba',         label: 'MBA / CAT',     icon: '📈' },
];

/**
 * Coaching institutes / classes. Not products — treated as service listings.
 * `streams` matches EDU_STREAMS ids so the filter chips can hide/show cards
 * without touching the DOM.
 */
window.EDU_INSTITUTES = [
  {
    id: 'apex-jee',
    name: 'Apex JEE Academy',
    tagline: 'IIT-JEE Main + Advanced · 2-year programme',
    streams: ['engineering'],
    mode: 'Hybrid',
    rating: 4.8, reviewCount: 2140,
    fee: '₹1,20,000 / year',
    location: 'Kota · Delhi · Online',
    badge: 'Top rated',
    tags: ['JEE Main', 'JEE Advanced', 'Foundation'],
  },
  {
    id: 'medico-neet',
    name: 'Medico NEET Prep',
    tagline: 'NEET-UG intensive with AIIMS toppers as mentors',
    streams: ['medical'],
    mode: 'Offline',
    rating: 4.7, reviewCount: 1876,
    fee: '₹1,08,000 / year',
    location: 'Hyderabad · Bengaluru',
    badge: 'AIIMS mentors',
    tags: ['NEET-UG', 'Biology', 'Physics', 'Chemistry'],
  },
  {
    id: 'lex-upsc',
    name: 'Lex Civils Institute',
    tagline: 'UPSC CSE — Prelims + Mains + Interview',
    streams: ['upsc'],
    mode: 'Hybrid',
    rating: 4.9, reviewCount: 3210,
    fee: '₹1,80,000 / 18 months',
    location: 'Old Rajinder Nagar, Delhi',
    badge: '25+ selections',
    tags: ['GS 1–4', 'Ethics', 'Optional'],
  },
  {
    id: 'codehive',
    name: 'CodeHive Bootcamp',
    tagline: 'Full-stack + DSA + system design in 6 months',
    streams: ['coding'],
    mode: 'Online',
    rating: 4.6, reviewCount: 942,
    fee: '₹85,000',
    location: 'Fully online · IST cohort',
    badge: 'Placement help',
    tags: ['MERN', 'DSA', 'System design'],
  },
  {
    id: 'aiforge',
    name: 'AI Forge — ML & GenAI',
    tagline: 'Applied ML, LLM apps, and MLOps',
    streams: ['coding'],
    mode: 'Online',
    rating: 4.7, reviewCount: 517,
    fee: '₹65,000',
    location: 'Live cohorts · Weekend',
    badge: 'New',
    tags: ['Python', 'LangChain', 'MLOps'],
  },
  {
    id: 'scholars-k12',
    name: 'Scholars K–12',
    tagline: 'CBSE / ICSE tuitions · Class 6 to 12',
    streams: ['k12'],
    mode: 'Offline',
    rating: 4.5, reviewCount: 1420,
    fee: '₹18,000 / year',
    location: 'PAN India · 40+ centres',
    badge: null,
    tags: ['CBSE', 'ICSE', 'State boards'],
  },
  {
    id: 'polyglot',
    name: 'Polyglot Language Studio',
    tagline: 'Spanish · French · German · Japanese',
    streams: ['languages'],
    mode: 'Hybrid',
    rating: 4.6, reviewCount: 683,
    fee: '₹22,000 / level',
    location: 'Bengaluru · Online',
    badge: null,
    tags: ['A1 – C1', 'DELF', 'JLPT'],
  },
  {
    id: 'ivy-cat',
    name: 'Ivy MBA & CAT',
    tagline: 'CAT · XAT · IIFT · GMAT prep',
    streams: ['mba'],
    mode: 'Hybrid',
    rating: 4.7, reviewCount: 1108,
    fee: '₹58,000',
    location: 'Mumbai · Chennai · Online',
    badge: '99+ %ile track record',
    tags: ['CAT', 'GMAT', 'Interview prep'],
  },
];

/**
 * Study material bundles. These are lightweight "kit" listings — no cart
 * integration; render as info cards with a "Download syllabus" affordance.
 */
window.EDU_MATERIALS = [
  {
    id: 'mat-jee-formulae',
    title: 'JEE Formula Master Sheet',
    subject: 'Physics + Chemistry + Math',
    format: 'PDF · 84 pages',
    price: 'Free',
    icon: '📐',
  },
  {
    id: 'mat-neet-bio',
    title: 'NEET Biology 10-Year PYQ',
    subject: 'Biology',
    format: 'PDF · 220 pages',
    price: '₹299',
    icon: '🧬',
  },
  {
    id: 'mat-upsc-notes',
    title: 'UPSC GS Handwritten Notes',
    subject: 'GS 1–4 + Ethics',
    format: 'Printed · 6 books',
    price: '₹1,499',
    icon: '📚',
  },
  {
    id: 'mat-cbse10',
    title: 'CBSE Class 10 All-in-One',
    subject: 'All subjects',
    format: 'Printed · 1200 pages',
    price: '₹899',
    icon: '📖',
  },
  {
    id: 'mat-dsa',
    title: 'DSA 100 Days Roadmap',
    subject: 'Data Structures',
    format: 'Notion + PDF',
    price: 'Free',
    icon: '🧠',
  },
  {
    id: 'mat-cat-mock',
    title: 'CAT Mock Test Series (24)',
    subject: 'Aptitude',
    format: 'Online · 24 tests',
    price: '₹1,999',
    icon: '📝',
  },
];

/**
 * Education news / blog articles. Same shape as SP_BLOG so we could later
 * unify the "news card" component if desired.
 */
window.EDU_BLOG = [
  {
    id: 'edu-1',
    title: 'NEP 2020: Five years in — what actually changed for schools',
    excerpt: 'A ground-level look at how the National Education Policy is playing out across boards, and what parents should know about its 5+3+3+4 restructure.',
    tag: 'Policy',
    date: 'Aug 08, 2026',
    author: 'Ritu Shah',
    readMins: 7,
  },
  {
    id: 'edu-2',
    title: 'JEE 2027: three big changes to the syllabus you can\'t ignore',
    excerpt: 'NTA has confirmed revisions to the physical chemistry and rotational mechanics sections. Here is the delta and a study plan built around it.',
    tag: 'JEE',
    date: 'Aug 06, 2026',
    author: 'Anand Verma',
    readMins: 6,
  },
  {
    id: 'edu-3',
    title: 'The 2026 UPSC topper interviews — patterns that repeat',
    excerpt: 'We read 40 topper interviews so you don\'t have to. A distilled playbook: notes strategy, answer-writing cadence, and how they handled Interview D-day.',
    tag: 'UPSC',
    date: 'Aug 04, 2026',
    author: 'Prerna Iyer',
    readMins: 9,
  },
  {
    id: 'edu-4',
    title: 'Are AI tutors replacing coaching centres?',
    excerpt: 'GenAI-based one-on-one tutors are quietly eating into K-12 coaching revenue. We benchmark five products and rate them on pedagogy, not just novelty.',
    tag: 'EdTech',
    date: 'Aug 02, 2026',
    author: 'Vikram Rao',
    readMins: 8,
  },
  {
    id: 'edu-5',
    title: 'Study abroad Fall 2027: visa timelines you should already be on',
    excerpt: 'US, UK, Canada, Germany — a country-by-country calendar of deadlines for applicants targeting Fall intake, with recommended buffers for each.',
    tag: 'Study abroad',
    date: 'Jul 30, 2026',
    author: 'Neha Kapoor',
    readMins: 10,
  },
  {
    id: 'edu-6',
    title: 'Coding bootcamp ROI: honest numbers from 500 graduates',
    excerpt: 'Anonymous salary + placement data across the top ten Indian bootcamps. Where the average grad lands, and which programmes actually beat self-study.',
    tag: 'Bootcamps',
    date: 'Jul 28, 2026',
    author: 'Karan Malhotra',
    readMins: 11,
  },
];
