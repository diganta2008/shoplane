/* yoga-data.js
 * ----------------------------------------------------------------
 * Reference data for the ShopLane Yoga vertical. Poses, class
 * schedule, articles and styles chips. All content is fictional.
 * ---------------------------------------------------------------- */

window.YG_STYLES = [
  { id: 'hatha',        name: 'Hatha',        icon: '🧘' },
  { id: 'vinyasa',      name: 'Vinyasa',      icon: '💨' },
  { id: 'ashtanga',     name: 'Ashtanga',     icon: '🔥' },
  { id: 'yin',          name: 'Yin',          icon: '🌙' },
  { id: 'restorative',  name: 'Restorative',  icon: '☁️' },
  { id: 'power',        name: 'Power',        icon: '💪' },
  { id: 'prenatal',     name: 'Prenatal',     icon: '🤰' },
  { id: 'kids',         name: 'Kids',         icon: '🐣' },
];

window.YG_POSES = [
  {
    id: 1, name: 'Downward-Facing Dog', sanskrit: 'Adho Mukha Svanasana',
    difficulty: 'Beginner', hold: '5–10 breaths', emoji: '🐕',
    benefits: 'Full-body stretch. Strengthens shoulders, lengthens hamstrings and calves, resets the nervous system between flows.',
  },
  {
    id: 2, name: 'Warrior II', sanskrit: 'Virabhadrasana II',
    difficulty: 'Beginner', hold: '5 breaths per side', emoji: '⚔️',
    benefits: 'Builds leg endurance, opens the hips and chest, teaches steady focus (drishti) through the front hand.',
  },
  {
    id: 3, name: 'Tree Pose', sanskrit: 'Vrikshasana',
    difficulty: 'Beginner', hold: '30–60 seconds per side', emoji: '🌳',
    benefits: 'Improves single-leg balance, strengthens the standing ankle, opens the inner thigh and hip of the lifted leg.',
  },
  {
    id: 4, name: "Child's Pose", sanskrit: 'Balasana',
    difficulty: 'Beginner', hold: 'As long as needed', emoji: '🌱',
    benefits: 'Restorative reset — releases the lower back, calms the mind, and reconnects breath before or after intense poses.',
  },
  {
    id: 5, name: 'Cobra Pose', sanskrit: 'Bhujangasana',
    difficulty: 'Intermediate', hold: '5 breaths, 2–3 rounds', emoji: '🐍',
    benefits: 'Gentle back-bend that decompresses the spine, opens the chest and counters kyphosis from desk work.',
  },
  {
    id: 6, name: 'Crow Pose', sanskrit: 'Bakasana',
    difficulty: 'Advanced', hold: 'Build to 30 seconds', emoji: '🦅',
    benefits: 'Arm-balance staple — builds wrist and shoulder strength, teaches core engagement, and rewires fear of face-planting.',
  },
  {
    id: 7, name: 'Corpse Pose', sanskrit: 'Savasana',
    difficulty: 'Beginner', hold: '5–15 minutes', emoji: '💤',
    benefits: 'The most important pose. Consolidates the neurological work of practice; skip it and you undo half the class.',
  },
  {
    id: 8, name: 'Seated Forward Fold', sanskrit: 'Paschimottanasana',
    difficulty: 'Intermediate', hold: '1–3 minutes', emoji: '🙇',
    benefits: 'Deep hamstring and lower-back release. Also stimulates the abdominal organs — great for digestion.',
  },
];

window.YG_CLASSES = [
  {
    id: 1, title: 'Sunrise Vinyasa Flow', style: 'Vinyasa',
    teacher: 'Anandi Nair',
    when: 'Tomorrow · 6:30 AM IST',
    durationMin: 45,
    level: 'All levels',
    seats: 12,
    mode: 'Live · Zoom',
  },
  {
    id: 2, title: 'Deep-Tissue Yin', style: 'Yin',
    teacher: 'Kavya Menon',
    when: 'Tomorrow · 8:00 PM IST',
    durationMin: 60,
    level: 'All levels',
    seats: 8,
    mode: 'Live · Zoom',
  },
  {
    id: 3, title: 'Ashtanga Primary Series (Led)', style: 'Ashtanga',
    teacher: 'Rohan Kapoor',
    when: 'Mon · 6:00 AM IST',
    durationMin: 90,
    level: 'Intermediate',
    seats: 6,
    mode: 'Studio · Bengaluru',
  },
  {
    id: 4, title: 'Prenatal Gentle Flow', style: 'Prenatal',
    teacher: 'Meera Iyer',
    when: 'Tue · 10:30 AM IST',
    durationMin: 45,
    level: 'All trimesters',
    seats: 10,
    mode: 'Live · Zoom',
  },
  {
    id: 5, title: 'Restorative Sunday Reset', style: 'Restorative',
    teacher: 'Sanjay Rao',
    when: 'Sun · 5:00 PM IST',
    durationMin: 75,
    level: 'All levels',
    seats: 14,
    mode: 'Studio · Gurugram',
  },
  {
    id: 6, title: 'Power Hour: Core &amp; Balance', style: 'Power',
    teacher: 'Aditi Iyer',
    when: 'Wed · 7:00 PM IST',
    durationMin: 60,
    level: 'Intermediate+',
    seats: 8,
    mode: 'Live · Zoom',
  },
];

window.YG_ARTICLES = [
  {
    id: 1, slug: 'breath-first-vinyasa',
    title: 'Breath First: Why Ujjayi Belongs at the Centre of Your Vinyasa',
    author: 'Anandi Nair',
    date: '2026-08-07', readMin: 5,
    category: 'Breathwork',
    hero: '../images/blog-nutrition.svg',
    excerpt: 'The ocean-breath (ujjayi) is not a decoration — it is the metronome that keeps your nervous system in the parasympathetic lane while you move.',
  },
  {
    id: 2, slug: 'yoga-for-lower-back',
    title: 'A 15-Minute Sequence to Actually Fix Your Lower Back',
    author: 'Dr. Rohan Kapoor',
    date: '2026-08-05', readMin: 8,
    category: 'Therapy',
    hero: '../images/blog-cardio.svg',
    excerpt: 'A physiotherapist-designed short flow that targets the three chokepoints — QL, hip flexors, and thoracic spine — behind most non-specific lower-back pain.',
  },
  {
    id: 3, slug: 'morning-vs-evening-practice',
    title: 'Morning vs. Evening Yoga: What Sports Science Says',
    author: 'Kavya Menon',
    date: '2026-08-02', readMin: 6,
    category: 'Practice',
    hero: '../images/blog-fitness.svg',
    excerpt: 'Cortisol curves, flexibility peaks, and body-temperature research — the surprisingly clear-cut case for two different practice styles at two different times of day.',
  },
  {
    id: 4, slug: 'why-savasana-matters',
    title: 'Why Skipping Savasana Undoes Half Your Practice',
    author: 'Meera Iyer',
    date: '2026-07-29', readMin: 4,
    category: 'Recovery',
    hero: '../images/blog-neuro.svg',
    excerpt: 'A neurologist explains what actually happens in the 8-minute wind-down at the end of class, and why walking out early costs you most of the benefit.',
  },
];
