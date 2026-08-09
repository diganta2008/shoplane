/* healthcare-data.js
 * ----------------------------------------------------------------
 * Reference data for the ShopLane Healthcare vertical. Kept in a
 * separate file so automation tests can import it directly:
 *
 *   const cardio = window.HC_DOCTORS.filter(d => d.specialty === 'Cardiology');
 *
 * All content is fictional and safe for demos.
 * ---------------------------------------------------------------- */

window.HC_SPECIALTIES = [
  { id: 'general',       name: 'General Physician',  icon: 'stethoscope' },
  { id: 'cardiology',    name: 'Cardiology',         icon: 'heart' },
  { id: 'dermatology',   name: 'Dermatology',        icon: 'sparkles' },
  { id: 'pediatrics',    name: 'Pediatrics',         icon: 'baby' },
  { id: 'orthopedics',   name: 'Orthopedics',        icon: 'bone' },
  { id: 'neurology',     name: 'Neurology',          icon: 'brain' },
  { id: 'gynecology',    name: 'Gynecology',         icon: 'flower' },
  { id: 'psychiatry',    name: 'Psychiatry',         icon: 'moon' },
];

window.HC_DOCTORS = [
  {
    id: 1, name: 'Dr. Aditi Sharma',
    specialty: 'Cardiology',
    qualifications: 'MBBS, MD, DM (Cardiology)',
    experienceYears: 16,
    languages: ['English', 'Hindi', 'Bengali'],
    hospital: 'Fortis Heart Institute, Bengaluru',
    rating: 4.9, reviewCount: 512,
    fee: 1200,
    nextSlot: 'Today, 4:30 PM',
    telehealth: true,
    image: '../images/doc-avatar-teal.svg',
    bio: 'Interventional cardiologist with 4,200+ angioplasty cases. Special interest in preventive cardiology for South Asian patients.',
  },
  {
    id: 2, name: 'Dr. Rohan Kapoor',
    specialty: 'Orthopedics',
    qualifications: 'MBBS, MS (Ortho), Fellowship (Sports Medicine, USA)',
    experienceYears: 12,
    languages: ['English', 'Hindi', 'Punjabi'],
    hospital: 'Apollo Sports Injury Clinic, Delhi',
    rating: 4.8, reviewCount: 302,
    fee: 900,
    nextSlot: 'Tomorrow, 10:00 AM',
    telehealth: true,
    image: '../images/doc-avatar-blue.svg',
    bio: 'Team physician for two national-league football clubs. Focus on knee-ligament reconstruction and non-surgical rehab.',
  },
  {
    id: 3, name: 'Dr. Meera Iyer',
    specialty: 'Dermatology',
    qualifications: 'MBBS, MD (Dermatology, Venereology & Leprosy)',
    experienceYears: 10,
    languages: ['English', 'Tamil', 'Malayalam'],
    hospital: 'Skin & Beyond Clinic, Chennai',
    rating: 4.7, reviewCount: 421,
    fee: 800,
    nextSlot: 'Today, 6:00 PM',
    telehealth: true,
    image: '../images/doc-avatar-rose.svg',
    bio: 'Aesthetic dermatology, acne management and Q-switch laser for pigmentation. Peer reviewer at the Indian Journal of Dermatology.',
  },
  {
    id: 4, name: 'Dr. Arjun Menon',
    specialty: 'Pediatrics',
    qualifications: 'MBBS, MD (Pediatrics), Fellowship (Neonatology)',
    experienceYears: 14,
    languages: ['English', 'Malayalam', 'Kannada'],
    hospital: 'Rainbow Children Hospital, Kochi',
    rating: 4.9, reviewCount: 618,
    fee: 700,
    nextSlot: 'Today, 5:15 PM',
    telehealth: false,
    image: '../images/doc-avatar-green.svg',
    bio: 'Newborn intensive care and adolescent nutrition. Runs a monthly parent-education clinic on paediatric asthma.',
  },
  {
    id: 5, name: 'Dr. Priya Nair',
    specialty: 'Gynecology',
    qualifications: 'MBBS, MS (Obs & Gyn), Fellowship (Laparoscopy)',
    experienceYears: 18,
    languages: ['English', 'Hindi', 'Marathi'],
    hospital: 'Cloudnine Womens Hospital, Mumbai',
    rating: 4.8, reviewCount: 803,
    fee: 1000,
    nextSlot: 'Tomorrow, 11:30 AM',
    telehealth: true,
    image: '../images/doc-avatar-rose.svg',
    bio: 'High-risk pregnancy specialist, 2,000+ laparoscopic procedures. Advocate for evidence-based prenatal care.',
  },
  {
    id: 6, name: 'Dr. Kabir Ahmed',
    specialty: 'Neurology',
    qualifications: 'MBBS, MD, DM (Neurology)',
    experienceYears: 15,
    languages: ['English', 'Hindi', 'Urdu'],
    hospital: 'Max Super Speciality, Delhi',
    rating: 4.7, reviewCount: 289,
    fee: 1400,
    nextSlot: 'Wed, 3:00 PM',
    telehealth: true,
    image: '../images/doc-avatar-blue.svg',
    bio: 'Stroke neurology, epilepsy and headache disorders. Ran the tele-stroke pilot for a North Indian government hospital network.',
  },
  {
    id: 7, name: 'Dr. Sanjay Rao',
    specialty: 'General Physician',
    qualifications: 'MBBS, MD (General Medicine)',
    experienceYears: 20,
    languages: ['English', 'Hindi', 'Kannada', 'Telugu'],
    hospital: 'Manipal Family Medicine, Bengaluru',
    rating: 4.6, reviewCount: 924,
    fee: 500,
    nextSlot: 'Today, 7:00 PM',
    telehealth: true,
    image: '../images/doc-avatar-teal.svg',
    bio: 'Family medicine, lifestyle disease management and preventive care. Panel doctor for a Fortune 500 India employee wellness program.',
  },
  {
    id: 8, name: 'Dr. Neha Verma',
    specialty: 'Psychiatry',
    qualifications: 'MBBS, MD (Psychiatry)',
    experienceYears: 9,
    languages: ['English', 'Hindi'],
    hospital: 'Mindspace Clinic, Pune',
    rating: 4.9, reviewCount: 233,
    fee: 1500,
    nextSlot: 'Thu, 2:00 PM',
    telehealth: true,
    image: '../images/doc-avatar-green.svg',
    bio: 'Anxiety and mood disorders, adult ADHD assessments, and workplace mental-wellness programs.',
  },
];

window.HC_BLOG = [
  {
    id: 1, slug: 'six-desk-stretches',
    title: '6 Desk Stretches Every Knowledge Worker Needs',
    author: 'Dr. Rohan Kapoor',
    date: '2026-08-04',
    readMin: 5,
    category: 'Fitness',
    hero: '../images/blog-fitness.svg',
    excerpt: 'A physiotherapist-approved five-minute routine you can do without leaving your chair — targeting the neck, shoulders and lower back where 80% of desk workers report pain.',
  },
  {
    id: 2, slug: 'monsoon-immunity',
    title: 'Monsoon Immunity: What Actually Works (and What Does Not)',
    author: 'Dr. Sanjay Rao',
    date: '2026-08-02',
    readMin: 7,
    category: 'Nutrition',
    hero: '../images/blog-nutrition.svg',
    excerpt: 'Vitamin C is not a magic bullet. A general physician separates evidence-backed monsoon-season habits from Instagram folk-wisdom.',
  },
  {
    id: 3, slug: 'when-headache-is-not-just-a-headache',
    title: 'When a Headache is NOT Just a Headache',
    author: 'Dr. Kabir Ahmed',
    date: '2026-07-29',
    readMin: 6,
    category: 'Neurology',
    hero: '../images/blog-neuro.svg',
    excerpt: 'Red-flag symptoms that mean your headache needs an ER visit — not another paracetamol. Written in plain language, no jargon.',
  },
  {
    id: 4, slug: 'sleep-and-heart',
    title: 'How Six Hours of Sleep is Quietly Aging Your Heart',
    author: 'Dr. Aditi Sharma',
    date: '2026-07-24',
    readMin: 8,
    category: 'Cardiology',
    hero: '../images/blog-cardio.svg',
    excerpt: 'New research links chronic short sleep to a 20% higher risk of cardiovascular events. Here is what a cardiologist recommends.',
  },
  {
    id: 5, slug: 'first-time-mom-checklist',
    title: 'The First-time Mom Prenatal Checklist',
    author: 'Dr. Priya Nair',
    date: '2026-07-19',
    readMin: 10,
    category: 'Pregnancy',
    hero: '../images/blog-nutrition.svg',
    excerpt: 'Every scan, blood test and immunisation you should have on your radar, laid out trimester by trimester. Print-friendly.',
  },
  {
    id: 6, slug: 'digital-mental-health',
    title: 'The Case for Digital Mental-Health, and Its Limits',
    author: 'Dr. Neha Verma',
    date: '2026-07-14',
    readMin: 6,
    category: 'Mental Health',
    hero: '../images/blog-neuro.svg',
    excerpt: 'App-based therapy is convenient and often affordable — but there is a specific set of situations where it should never replace an in-person visit.',
  },
];

/* Convenience helpers -------------------------------------------- */
window.getDoctor = function (id) {
  return (window.HC_DOCTORS || []).find((d) => d.id === Number(id));
};
window.getBlogPost = function (slugOrId) {
  const list = window.HC_BLOG || [];
  return list.find((b) => b.slug === slugOrId || b.id === Number(slugOrId));
};
