/**
 * Static data for the Travel vertical — India heritage circuit.
 *
 *   TRV_MONUMENTS  : famous historical sites, one per major city, with real
 *                    Wikimedia-Commons photo URLs verified via the Wikipedia
 *                    REST summary API. `wiki` links out for deeper context.
 *   TRV_HOTELS     : sample hotel picks near each monument city, mixing
 *                    heritage, boutique and mid-range options with a
 *                    per-night rate in USD (demo store currency).
 *   TRV_TRANSPORT  : four modes (flight / train / bus / taxi) with a few
 *                    representative routes each — for the tabbed picker.
 *
 * Everything is client-side demo data; no live booking API is called.
 */

window.TRV_MONUMENTS = [
  {
    id: 'taj-mahal',
    name: 'Taj Mahal',
    city: 'Agra',
    state: 'Uttar Pradesh',
    era: '1631\u20131648',
    style: 'Mughal',
    tag: 'UNESCO World Heritage',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Taj_Mahal_%28Edited%29.jpeg/640px-Taj_Mahal_%28Edited%29.jpeg',
    wiki: 'https://en.wikipedia.org/wiki/Taj_Mahal',
    desc: 'Ivory-white marble mausoleum commissioned by Shah Jahan for his wife Mumtaz Mahal; the pinnacle of Mughal architecture.',
  },
  {
    id: 'red-fort',
    name: 'Red Fort',
    city: 'Delhi',
    state: 'Delhi',
    era: '1639\u20131648',
    style: 'Mughal',
    tag: 'UNESCO World Heritage',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Delhi_fort.jpg/640px-Delhi_fort.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Red_Fort',
    desc: 'The main residence of the Mughal emperors for nearly 200 years; the Prime Minister addresses the nation from its ramparts every Independence Day.',
  },
  {
    id: 'qutb-minar',
    name: 'Qutb Minar',
    city: 'Delhi',
    state: 'Delhi',
    era: '1199\u20131220',
    style: 'Indo-Islamic',
    tag: 'UNESCO World Heritage',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Qutb_Minar_2022.jpg/640px-Qutb_Minar_2022.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Qutb_Minar',
    desc: 'A 73-metre victory tower with 399 steps, built after the Ghurid conquest of the region \u2014 the world\u2019s tallest brick minaret.',
  },
  {
    id: 'hawa-mahal',
    name: 'Hawa Mahal',
    city: 'Jaipur',
    state: 'Rajasthan',
    era: '1799',
    style: 'Rajput',
    tag: 'Pink-City icon',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/East_facade_Hawa_Mahal_Jaipur_from_ground_level_%28July_2022%29_-_img_01.jpg/640px-East_facade_Hawa_Mahal_Jaipur_from_ground_level_%28July_2022%29_-_img_01.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Hawa_Mahal',
    desc: 'The five-storey "Palace of Winds" in red and pink sandstone, laced with 953 jharokhas (screened windows) for royal ladies to watch the street below.',
  },
  {
    id: 'amber-fort',
    name: 'Amber Fort',
    city: 'Jaipur',
    state: 'Rajasthan',
    era: '1592',
    style: 'Rajput',
    tag: 'UNESCO World Heritage',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/20191219_Fort_Amber%2C_Amer%2C_Jaipur_0955_9481.jpg/640px-20191219_Fort_Amber%2C_Amer%2C_Jaipur_0955_9481.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Amber_Fort',
    desc: 'The hilltop capital of the Kachwahas, famed for its Sheesh Mahal (mirror palace), Ganesh Pol and terraced Maota Lake gardens.',
  },
  {
    id: 'gateway-of-india',
    name: 'Gateway of India',
    city: 'Mumbai',
    state: 'Maharashtra',
    era: '1911\u20131924',
    style: 'Indo-Saracenic',
    tag: 'Harbour landmark',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Mumbai_03-2016_30_Gateway_of_India.jpg/640px-Mumbai_03-2016_30_Gateway_of_India.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Gateway_of_India',
    desc: 'A 26-metre basalt arch on the Arabian Sea, built to welcome King George V in 1911 and the last symbolic exit point for British troops in 1948.',
  },
  {
    id: 'charminar',
    name: 'Charminar',
    city: 'Hyderabad',
    state: 'Telangana',
    era: '1591',
    style: 'Indo-Islamic (Qutb Shahi)',
    tag: 'City emblem',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Charminar_Hyderabad_1.jpg/640px-Charminar_Hyderabad_1.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Charminar',
    desc: 'Four grand minarets crowning a square in old Hyderabad, encircled by the storied Laad Bazaar for bangles and biryani.',
  },
  {
    id: 'mysore-palace',
    name: 'Mysore Palace',
    city: 'Mysore',
    state: 'Karnataka',
    era: '1897\u20131912',
    style: 'Indo-Saracenic',
    tag: 'Royal residence',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Mysore_Palace_Morning.jpg/640px-Mysore_Palace_Morning.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Mysore_Palace',
    desc: 'The seat of the Wadiyar dynasty \u2014 a fusion of Hoysala, Rajput and Gothic elements that lights up with 97,000 bulbs during Dasara.',
  },
  {
    id: 'konark-sun-temple',
    name: 'Konark Sun Temple',
    city: 'Konark',
    state: 'Odisha',
    era: '13th century',
    style: 'Kalinga',
    tag: 'UNESCO World Heritage',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Konarka_Temple.jpg/640px-Konarka_Temple.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Konark_Sun_Temple',
    desc: 'A colossal chariot-shaped temple to Surya, carved with 24 stone wheels and seven horses \u2014 the summit of Odisha\u2019s temple architecture.',
  },
  {
    id: 'meenakshi-temple',
    name: 'Meenakshi Temple',
    city: 'Madurai',
    state: 'Tamil Nadu',
    era: '17th century',
    style: 'Dravidian',
    tag: 'Twin sanctum',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/An_aerial_view_of_Madurai_city_from_atop_of_Meenakshi_Amman_temple.jpg/640px-An_aerial_view_of_Madurai_city_from_atop_of_Meenakshi_Amman_temple.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Meenakshi_Temple',
    desc: 'A living Nayaka-era temple with 14 gopurams densely populated by 33,000 painted sculptures, dedicated to Meenakshi and Sundareswarar.',
  },
  {
    id: 'golden-temple',
    name: 'Golden Temple',
    city: 'Amritsar',
    state: 'Punjab',
    era: '1604 (present form 1830)',
    style: 'Sikh gurdwara',
    tag: 'Sri Harmandir Sahib',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/The_Golden_Temple_of_Amrithsar_7.jpg/640px-The_Golden_Temple_of_Amrithsar_7.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Golden_Temple',
    desc: 'The holiest gurdwara of Sikhism, its upper floors overlaid in gold and set in the middle of the sacred Sarovar; the langar feeds 100,000 daily.',
  },
  {
    id: 'victoria-memorial',
    name: 'Victoria Memorial',
    city: 'Kolkata',
    state: 'West Bengal',
    era: '1906\u20131921',
    style: 'Indo-Saracenic',
    tag: 'Museum & gardens',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/Victoria_Memorial_situated_in_Kolkata.jpg/640px-Victoria_Memorial_situated_in_Kolkata.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Victoria_Memorial,_Kolkata',
    desc: 'A vast Makrana-marble memorial to Queen Victoria set in 26 hectares of gardens, now a museum with 3,900 artworks.',
  },
  {
    id: 'sanchi-stupa',
    name: 'Sanchi Stupa',
    city: 'Sanchi',
    state: 'Madhya Pradesh',
    era: '3rd century BCE',
    style: 'Buddhist',
    tag: 'UNESCO World Heritage',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/12/East_Gateway_-_Stupa_1_-_Sanchi_Hill_2013-02-21_4398.JPG/640px-East_Gateway_-_Stupa_1_-_Sanchi_Hill_2013-02-21_4398.JPG',
    wiki: 'https://en.wikipedia.org/wiki/Sanchi_Stupa',
    desc: 'Commissioned by emperor Ashoka, the Great Stupa\u2019s four toranas carry some of the earliest and most exquisite narrative reliefs from the Buddha\u2019s life.',
  },
  {
    id: 'ajanta-caves',
    name: 'Ajanta Caves',
    city: 'Aurangabad',
    state: 'Maharashtra',
    era: '2nd c. BCE \u2013 480 CE',
    style: 'Rock-cut Buddhist',
    tag: 'UNESCO World Heritage',
    photo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Ajanta_%2863%29.jpg/640px-Ajanta_%2863%29.jpg',
    wiki: 'https://en.wikipedia.org/wiki/Ajanta_Caves',
    desc: '30 rock-cut viharas and chaityas hewn into a horseshoe cliff, holding some of the finest surviving murals of the ancient world.',
  },
];

/** Distinct cities that have monuments — used for the "Explore by city" pills. */
window.TRV_CITIES = (function() {
  const seen = {};
  const list = [];
  window.TRV_MONUMENTS.forEach((m) => {
    if (!seen[m.city]) { seen[m.city] = true; list.push(m.city); }
  });
  return list;
})();

/** Sample hotels per city — indicative pricing in USD (demo store currency). */
window.TRV_HOTELS = [
  { id: 'h-agra-01',   name: 'The Oberoi Amarvilas',       city: 'Agra',       stars: 5, price: 460, tag: 'Taj view from every room',
    photo: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=640&q=70' },
  { id: 'h-agra-02',   name: 'ITC Mughal, a Luxury Collection', city: 'Agra',  stars: 5, price: 210, tag: 'Kaya Kalp Royal Spa' },
  { id: 'h-delhi-01',  name: 'The Imperial New Delhi',     city: 'Delhi',      stars: 5, price: 320, tag: 'Colonial heritage' },
  { id: 'h-delhi-02',  name: 'Bloomrooms @ Janpath',       city: 'Delhi',      stars: 3, price: 62,  tag: 'Central & budget-smart' },
  { id: 'h-jaipur-01', name: 'Rambagh Palace',             city: 'Jaipur',     stars: 5, price: 540, tag: 'Former royal residence' },
  { id: 'h-jaipur-02', name: 'Umaid Mahal',                city: 'Jaipur',     stars: 3, price: 78,  tag: 'Heritage courtyard' },
  { id: 'h-mumbai-01', name: 'The Taj Mahal Palace',       city: 'Mumbai',     stars: 5, price: 380, tag: 'Facing Gateway of India' },
  { id: 'h-mumbai-02', name: 'Abode Bombay',               city: 'Mumbai',     stars: 4, price: 118, tag: 'Colaba boutique' },
  { id: 'h-hyd-01',    name: 'Taj Falaknuma Palace',       city: 'Hyderabad',  stars: 5, price: 480, tag: 'Nizam\u2019s hilltop palace' },
  { id: 'h-hyd-02',    name: 'Novotel Hyderabad Airport',  city: 'Hyderabad',  stars: 4, price: 108, tag: 'Terminal shuttle' },
  { id: 'h-mysore-01', name: 'Royal Orchid Metropole',     city: 'Mysore',     stars: 4, price: 92,  tag: 'Restored 1920s heritage' },
  { id: 'h-mysore-02', name: 'Radisson Blu Plaza',         city: 'Mysore',     stars: 4, price: 105, tag: 'Rooftop dining' },
  { id: 'h-amritsar-01', name: 'Taj Swarna',               city: 'Amritsar',   stars: 5, price: 145, tag: '3 km from Golden Temple' },
  { id: 'h-amritsar-02', name: 'Hyatt Regency Amritsar',   city: 'Amritsar',   stars: 5, price: 130, tag: 'Punjabi thali night' },
  { id: 'h-kol-01',    name: 'The Oberoi Grand Kolkata',   city: 'Kolkata',    stars: 5, price: 250, tag: '1887 landmark' },
  { id: 'h-kol-02',    name: 'The LaLiT Great Eastern',    city: 'Kolkata',    stars: 5, price: 175, tag: '\u201cJewel of the East\u201d' },
  { id: 'h-madurai-01',name: 'Heritage Madurai',           city: 'Madurai',    stars: 4, price: 88,  tag: 'Laurie Baker cottages' },
  { id: 'h-konark-01', name: 'Mayfair Waves',              city: 'Konark',     stars: 4, price: 110, tag: 'Puri sea front' },
  { id: 'h-aur-01',    name: 'Vivanta Aurangabad',         city: 'Aurangabad', stars: 4, price: 90,  tag: 'Base for Ajanta & Ellora' },
  { id: 'h-sanchi-01', name: 'Jehan Numa Palace, Bhopal',  city: 'Sanchi',     stars: 5, price: 155, tag: 'Bhopal heritage stay' },
];

/** Sample transport routes across the four modes. Times are indicative. */
window.TRV_TRANSPORT = {
  flight: [
    { id: 'f1', operator: 'IndiGo 6E-2117',        from: 'Delhi (DEL)',    to: 'Mumbai (BOM)',   depart: '06:15', arrive: '08:25', duration: '2h 10m', price: 68 },
    { id: 'f2', operator: 'Vistara UK-935',        from: 'Mumbai (BOM)',   to: 'Bengaluru (BLR)',depart: '09:40', arrive: '11:15', duration: '1h 35m', price: 82 },
    { id: 'f3', operator: 'Air India AI-540',      from: 'Delhi (DEL)',    to: 'Kolkata (CCU)',  depart: '14:20', arrive: '16:35', duration: '2h 15m', price: 74 },
    { id: 'f4', operator: 'IndiGo 6E-2437',        from: 'Chennai (MAA)',  to: 'Hyderabad (HYD)',depart: '18:05', arrive: '19:20', duration: '1h 15m', price: 55 },
    { id: 'f5', operator: 'Akasa QP-1104',         from: 'Bengaluru (BLR)',to: 'Jaipur (JAI)',   depart: '07:30', arrive: '10:05', duration: '2h 35m', price: 96 },
  ],
  train: [
    { id: 't1', operator: 'Rajdhani Exp. 12951',   from: 'Mumbai Central', to: 'New Delhi',      depart: '17:00', arrive: '08:35', duration: '15h 35m', price: 38, coach: '2A' },
    { id: 't2', operator: 'Shatabdi Exp. 12002',   from: 'New Delhi',      to: 'Bhopal Jn.',     depart: '06:00', arrive: '13:50', duration: '7h 50m',  price: 22, coach: 'CC' },
    { id: 't3', operator: 'Vande Bharat 22436',    from: 'New Delhi',      to: 'Varanasi Jn.',   depart: '06:00', arrive: '14:00', duration: '8h 00m',  price: 28, coach: 'EC' },
    { id: 't4', operator: 'Duronto Exp. 12213',    from: 'Delhi Sarai',    to: 'Yesvantpur',     depart: '20:45', arrive: '05:50', duration: '33h 05m', price: 45, coach: '3A' },
    { id: 't5', operator: 'Palace on Wheels',      from: 'Delhi',          to: 'Delhi (circuit)',depart: 'Wed 18:30', arrive: '+7 nights', duration: '7 nights', price: 6800, coach: 'Cabin' },
  ],
  bus: [
    { id: 'b1', operator: 'RedBus / VRL Volvo',    from: 'Bengaluru',      to: 'Mysore',         depart: '07:30', arrive: '10:45', duration: '3h 15m',  price: 12, type: 'AC Sleeper' },
    { id: 'b2', operator: 'RSRTC Volvo',           from: 'Delhi',          to: 'Jaipur',         depart: '06:00', arrive: '11:30', duration: '5h 30m',  price: 15, type: 'AC Seater' },
    { id: 'b3', operator: 'MSRTC Shivneri',        from: 'Mumbai',         to: 'Pune',           depart: '08:15', arrive: '11:45', duration: '3h 30m',  price: 8,  type: 'AC Seater' },
    { id: 'b4', operator: 'GreenLine',             from: 'Hyderabad',      to: 'Vijayawada',     depart: '22:00', arrive: '04:30', duration: '6h 30m',  price: 18, type: 'AC Sleeper' },
    { id: 'b5', operator: 'KSRTC Airavat',         from: 'Bengaluru',      to: 'Hampi',          depart: '21:30', arrive: '07:00', duration: '9h 30m',  price: 22, type: 'AC Sleeper' },
  ],
  taxi: [
    { id: 'x1', operator: 'Ola Prime SUV',         from: 'Agra city',      to: 'Fatehpur Sikri', depart: 'On demand', arrive: '\u2014', duration: '1h 15m', price: 24, type: 'One-way' },
    { id: 'x2', operator: 'Uber Premier',          from: 'Jaipur',         to: 'Amber Fort',     depart: 'On demand', arrive: '\u2014', duration: '35 min', price: 8,  type: 'One-way' },
    { id: 'x3', operator: 'MakeMyTrip Innova',     from: 'Kochi',          to: 'Munnar',         depart: 'Pre-book',  arrive: '\u2014', duration: '4h 00m', price: 65, type: 'One-way' },
    { id: 'x4', operator: 'Savaari Sedan',         from: 'New Delhi',      to: 'Agra',           depart: 'Pre-book',  arrive: '\u2014', duration: '3h 45m', price: 58, type: 'One-way' },
    { id: 'x5', operator: 'Zoomcar Self-drive',    from: 'Bengaluru',      to: 'Mysore (day)',   depart: 'On demand', arrive: '\u2014', duration: 'Day rental', price: 42, type: 'Full-day' },
  ],
};
