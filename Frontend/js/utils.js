// ========== DESTINATION GUIDES DATABASE ==========
const DESTINATION_GUIDES = {
    Goa: { state: 'Goa', image: 'images/goa.jpg', vibe: 'Sunset beaches, Portuguese heritage, and vibrant nightlife.', season: 'November to February', days: '3 days', budget: 'Rs. 15,000 - Rs. 25,000', mustDo: ['Calangute Beach sunset', 'Old Goa heritage walk', 'Local seafood dinner'], nearby: ['Dudhsagar Falls', 'Fort Aguada', 'Chapora Fort'], tips: ['Book scooters with valid license.', 'South Goa is calmer than North.', 'Visit during cooler hours.'] },
    Gokarna: { state: 'Karnataka', image: 'images/gokarna.jpg', vibe: 'Peaceful coastal town with temple streets and scenic beach trails.', season: 'October to March', days: '2 days', budget: 'Rs. 10,000 - Rs. 18,000', mustDo: ['Om Beach viewpoint', 'Kudle Beach walk', 'Half Moon Beach hike'], nearby: ['Mirjan Fort', 'Yana Caves', 'Mahabaleshwar'], tips: ['Carry water on hikes.', 'Sea conditions change in monsoon.'] },
    Varkala: { state: 'Kerala', image: 'images/varkala.jpg', vibe: 'Dramatic cliffs above the Arabian Sea with wellness retreats and cafes.', season: 'October to March', days: '2-3 days', budget: 'Rs. 15,000 - Rs. 18,000', mustDo: ['Cliff promenade walk', 'Papanasam Beach sunset', 'Spa or yoga session'], nearby: ['Kappil Beach', 'Jatayu Center', 'Sivagiri'], tips: ['Use marked cliff stairs only.', 'Reserve stays early in peak season.'] },
    'Marina Beach': { state: 'Tamil Nadu', image: 'images/marina.jpg', vibe: "Chennai's long urban beach paired with temples and South Indian food.", season: 'November to February', days: '1-2 days', budget: 'Rs. 10,000 - Rs. 16,000', mustDo: ['Early morning promenade', 'Mylapore temple streets', 'South Indian breakfast'], nearby: ['San Thome Basilica', 'Government Museum', 'Besant Nagar'], tips: ['Visit beach early to avoid heat.', 'Swimming unsafe in rough areas.'] },
    Kovalam: { state: 'Kerala', image: 'images/kovalam.jpg', vibe: 'Curving beaches, lighthouse, and ayurvedic wellness on tropical shores.', season: 'September to March', days: '2 days', budget: 'Rs. 12,000 - Rs. 20,000', mustDo: ['Lighthouse climb', 'Hawa Beach sunset', 'Ayurveda session'], nearby: ['Poovar Backwaters', 'Vizhinjam', 'Thiruvananthapuram'], tips: ['Follow lifeguard flags.', 'Wear light cotton and sunscreen.'] },
    Rameswaram: { state: 'Tamil Nadu', image: 'images/rameswaram.jpg', vibe: 'Sacred pilgrimage city surrounded by vivid sea and island landscapes.', season: 'October to April', days: '2 days', budget: 'Rs. 9,000 - Rs. 15,000', mustDo: ['Ramanathaswamy Temple', 'Pamban Bridge views', 'Dhanushkodi trip'], nearby: ['APJ Abdul Kalam Memorial', 'Ariyaman Beach', 'Devipattinam'], tips: ['Dress respectfully at temples.', 'Start Dhanushkodi early for heat.'] },
    Manali: { state: 'Himachal Pradesh', image: 'images/manali.jpg', vibe: 'River valleys, pine forests, and high-altitude adventure in the Himalayas.', season: 'March to June and December to February', days: '3 days', budget: 'Rs. 18,000 - Rs. 30,000', mustDo: ['Old Manali walk', 'Solang Valley activities', 'Snow point visit'], nearby: ['Naggar Castle', 'Kasol', 'Rohtang Pass'], tips: ['Allow buffer for weather delays.', 'Layer clothing for temperature swings.'] },
    Shimla: { state: 'Himachal Pradesh', image: 'images/shimla.jpg', vibe: 'Colonial hill station with promenades, pine slopes, and valley views.', season: 'March to June and December to January', days: '2 days', budget: 'Rs. 15,000 - Rs. 25,000', mustDo: ['Mall Road walk', 'Jakhoo Temple', 'Kalka-Shimla railway'], nearby: ['Kufri', 'Mashobra', 'Tandi'], tips: ['Walk central Shimla on foot.', 'Check road advisories in winter.'] },
    Mussoorie: { state: 'Uttarakhand', image: 'images/mussoorie.jpg', vibe: 'Classic hill escape with valley vistas and walking-friendly promenades.', season: 'March to June and September to November', days: '2 days', budget: 'Rs. 14,000 - Rs. 22,000', mustDo: ['Camel Back Road', 'Gun Hill views', 'Landour cafe stroll'], nearby: ['Kempty Falls', 'Dhanaulti', 'George Everest Peak'], tips: ['Walk during off-peak hours.', 'Carry rain protection in monsoon.'] },
    Darjeeling: { state: 'West Bengal', image: 'images/darjeeling.jpg', vibe: 'Tea estates, Himalayan sunrises, and heritage toy train charm.', season: 'March to May and October to December', days: '3 days', budget: 'Rs. 16,000 - Rs. 25,000', mustDo: ['Tiger Hill sunrise', 'Tea garden tasting', 'Toy Train ride'], nearby: ['Mirik Lake', 'Kurseong', 'Kalimpong'], tips: ['Book sunrise transport in advance.', 'Mountain views may be obscured; keep spare day.'] },
    Nainital: { state: 'Uttarakhand', image: 'images/nainital.jpg', vibe: 'Mountain lake town ideal for boating, walks, and scenic viewpoints.', season: 'March to June and October to January', days: '2 days', budget: 'Rs. 13,000 - Rs. 22,000', mustDo: ['Naini Lake boating', 'Mall Road evening', 'Snow View Point'], nearby: ['Bhimtal', 'Sattal', 'Mukteshwar'], tips: ['Use official boat services.', 'Avoid peak Mall Road hours.'] },
    Mcleodganj: { state: 'Himachal Pradesh', image: 'images/mcleodganj.jpg', vibe: 'Tibetan culture, mountain cafes, and rewarding trekking trails.', season: 'March to June and September to November', days: '2-3 days', budget: 'Rs. 12,000 - Rs. 20,000', mustDo: ['Tsuglagkhang complex', 'Bhagsu waterfall', 'Triund trek'], nearby: ['Dharamkot', 'Kangra Fort', 'Palampur'], tips: ['Wear proper trekking shoes.', 'Keep monasteries quiet and respectful.'] },
    Coorg: { state: 'Karnataka', image: 'images/coorg.jpg', vibe: 'Coffee plantations, misty green hills, and gentle waterfall escapes.', season: 'October to March', days: '2-3 days', budget: 'Rs. 12,000 - Rs. 20,000', mustDo: ['Coffee estate walk', 'Abbey Falls', 'Raja Seat sunset'], nearby: ['Mandalpatti', 'Dubare', 'Talacauvery'], tips: ['Carry light rain gear.', 'Reserve estate stays on weekends early.'] },
    Gangtok: { state: 'Sikkim', image: 'images/gangtok.jpg', vibe: 'Clean Himalayan city with monasteries and grand mountain viewpoints.', season: 'March to June and October to December', days: '3 days', budget: 'Rs. 14,000 - Rs. 24,000', mustDo: ['MG Marg evening', 'Rumtek monastery', 'Tsomgo Lake trip'], nearby: ['Nathula Pass', 'Namchi', 'Pelling'], tips: ['Permits needed for high-altitude areas.', 'Acclimatize before long mountain trips.'] },
    Srinagar: { state: 'Jammu and Kashmir', image: 'images/srinagar.jpg', vibe: 'Lakes, Mughal gardens, and houseboat mornings framed by mountains.', season: 'April to October', days: '3 days', budget: 'Rs. 16,000 - Rs. 28,000', mustDo: ['Dal Lake shikara', 'Mughal Gardens', 'Old City shopping'], nearby: ['Gulmarg', 'Pahalgam', 'Sonamarg'], tips: ['Confirm route conditions before day trips.', 'Agree shikara rates before departure.'] },
    Jaipur: { state: 'Rajasthan', image: 'images/jaipur.jpg', vibe: 'Pink-hued forts, palaces, crafts, and memorable Rajasthani cuisine.', season: 'October to March', days: '2-3 days', budget: 'Rs. 12,000 - Rs. 20,000', mustDo: ['Amber Fort morning', 'City Palace visit', 'Bapu Bazaar shopping'], nearby: ['Nahargarh Fort', 'Jaigarh Fort', 'Stepwell Abhaneri'], tips: ['Start forts early for fewer crowds.', 'Use authorized guides at monuments.'] },
    Delhi: { state: 'Delhi', image: 'images/delhi.jpg', vibe: 'Layered history, famous food lanes, and modern cultural districts.', season: 'October to March', days: '2-3 days', budget: 'Rs. 10,000 - Rs. 18,000', mustDo: ['Old Delhi heritage walk', 'Humayun Tomb', 'India Gate evening'], nearby: ['Agra', 'Neemrana', 'Gurugram'], tips: ['Use Metro for predictable travel.', 'Keep valuables secure in markets.'] },
    Mumbai: { state: 'Maharashtra', image: 'images/mumbai.jpg', vibe: 'Seafront sunsets, heritage architecture, and nonstop city energy.', season: 'November to February', days: '2-3 days', budget: 'Rs. 12,000 - Rs. 22,000', mustDo: ['Gateway heritage walk', 'Marine Drive sunset', 'Street food trail'], nearby: ['Elephanta Caves', 'Lonavala', 'Alibaug'], tips: ['Allow extra travel time in traffic.', 'Avoid seafront during heavy monsoon.'] },
    Bangalore: { state: 'Karnataka', image: 'images/bangalore.jpg', vibe: 'Leafy urban breaks, cafes, parks, and easy weekend getaway base.', season: 'October to February', days: '2 days', budget: 'Rs. 10,000 - Rs. 18,000', mustDo: ['Cubbon Park', 'Bangalore Palace', 'Church Street'], nearby: ['Nandi Hills', 'Mysuru', 'Coorg'], tips: ['Use Metro during peak hours.', 'Carry light layer for cool evenings.'] },
    Kolkata: { state: 'West Bengal', image: 'images/kolkata.jpg', vibe: 'Literature, colonial landmarks, river views, and celebrated cuisine.', season: 'October to February', days: '2-3 days', budget: 'Rs. 11,000 - Rs. 19,000', mustDo: ['Victoria Memorial', 'College Street café', 'Hooghly riverside'], nearby: ['Sundarbans', 'Shantiniketan', 'Bishnupur'], tips: ['Explore heritage areas in morning.', 'Carry cash for street food vendors.'] },
    Chennai: { state: 'Tamil Nadu', image: 'images/chennai.jpg', vibe: 'Coastal culture, grand temples, and deep South Indian flavors.', season: 'November to February', days: '2 days', budget: 'Rs. 10,000 - Rs. 18,000', mustDo: ['Marina sunrise', 'Kapaleeshwarar Temple', 'Filter coffee trail'], nearby: ['Mahabalipuram', 'Kanchipuram', 'Pondicherry'], tips: ['Plan outdoor time in morning/evening.', 'Dress modestly at religious sites.'] },
    Agra: { state: 'Uttar Pradesh', image: 'images/agra.jpg', vibe: 'Mughal masterworks centered around an unforgettable monument.', season: 'October to March', days: '1-2 days', budget: 'Rs. 9,000 - Rs. 16,000', mustDo: ['Taj Mahal sunrise', 'Agra Fort', 'Mehtab Bagh sunset'], nearby: ['Fatehpur Sikri', 'Mathura', 'Bharatpur'], tips: ['Taj Mahal closed on Fridays.', 'Buy tickets from official channels.'] },
    Indore: { state: 'Madhya Pradesh', image: 'images/indore.jpg', vibe: 'Food-focused city breaks with heritage and lively bazaars.', season: 'October to March', days: '2 days', budget: 'Rs. 9,000 - Rs. 16,000', mustDo: ['Rajwada Palace', 'Sarafa night market', 'Chappan Dukan'], nearby: ['Mandu', 'Maheshwar', 'Ujjain'], tips: ['Arrive hungry for night markets.', 'Keep day trips separate from food days.'] },
    Ahmedabad: { state: 'Gujarat', image: 'images/ahmedabad.jpg', vibe: 'World heritage lanes, textile crafts, and riverside evenings.', season: 'November to February', days: '2 days', budget: 'Rs. 10,000 - Rs. 17,000', mustDo: ['Old City walk', 'Sabarmati Ashram', 'Riverfront sunset'], nearby: ['Adalaj Stepwell', 'Modhera Temple', 'Patan'], tips: ['Pre-book heritage walks.', 'Summer afternoons are extremely hot.'] },
    Hampi: { state: 'Karnataka', image: 'images/hampi.jpg', vibe: 'Ancient stone monuments amid boulder landscapes and river views.', season: 'October to February', days: '2 days', budget: 'Rs. 10,000 - Rs. 18,000', mustDo: ['Virupaksha Temple', 'Vittala complex', 'Matanga Hill sunset'], nearby: ['Anegundi', 'Tungabhadra', 'Badami'], tips: ['Begin monument walks early.', 'Carry water between exposed sites.'] },
    Mysuru: { state: 'Karnataka', image: 'images/mysuru.jpg', vibe: 'Graceful palace city with markets, gardens, and heritage charm.', season: 'October to February', days: '2 days', budget: 'Rs. 9,000 - Rs. 16,000', mustDo: ['Mysore Palace', 'Devaraja Market', 'Chamundi Hill'], nearby: ['Brindavan Gardens', 'Srirangapatna', 'Coorg'], tips: ['Check palace illumination schedule.', 'Try local breakfasts early in day.'] },
    Kasol: { state: 'Himachal Pradesh', image: 'images/kasol.jpg', vibe: 'Parvati Valley trails, riverside cafes, and relaxed mountain time.', season: 'March to June and September to November', days: '2-3 days', budget: 'Rs. 10,000 - Rs. 18,000', mustDo: ['Parvati riverside walk', 'Chalal trail', 'Café evening'], nearby: ['Manikaran', 'Tosh', 'Kheerganga'], tips: ['Avoid unmarked riverside edges.', 'Carry cash and warm layers.'] },
    Rishikesh: { state: 'Uttarakhand', image: 'images/rishikesh.jpg', vibe: 'Ganga spirituality paired with adventure rafting and yoga.', season: 'September to April', days: '2-3 days', budget: 'Rs. 12,000 - Rs. 20,000', mustDo: ['River rafting', 'Ganga aarti', 'Yoga session'], nearby: ['Haridwar', 'Neer Garh falls', 'Devprayag'], tips: ['Follow rafting safety strictly.', 'Respect ceremony photography rules.'] },
    Ladakh: { state: 'Ladakh', image: 'images/ladakh.jpg', vibe: 'High-altitude roads, monasteries, and stark cinematic landscapes.', season: 'May to September', days: '5 days', budget: 'Rs. 30,000 - Rs. 45,000', mustDo: ['Leh market', 'Pangong Lake', 'Nubra Valley'], nearby: ['Khardung La', 'Magnetic Hill', 'Thiksey'], tips: ['Allow first day for acclimatization.', 'Secure permits and check roads.'] },
    Andaman: { state: 'Andaman and Nicobar Islands', image: 'images/andaman.jpg', vibe: 'Clear island waters, pristine beaches, and marine activities.', season: 'October to May', days: '4 days', budget: 'Rs. 25,000 - Rs. 40,000', mustDo: ['Radhanagar Beach', 'Scuba or snorkel', 'Cellular Jail'], nearby: ['Neil Island', 'Ross Island', 'Baratang'], tips: ['Book ferries early.', 'Follow coral-safe practices.'] },
    Panchmarhi: { state: 'Madhya Pradesh', image: 'images/panchmarhi.jpg', vibe: 'Wooded hill station with waterfalls, caves, and forest viewpoints.', season: 'October to March', days: '2-3 days', budget: 'Rs. 11,000 - Rs. 19,000', mustDo: ['Bee Falls', 'Dhoopgarh sunset', 'Pandav Caves'], nearby: ['Satpura National Park', 'Jatashankar', 'Handi Khoh'], tips: ['Use permitted vehicles.', 'Wear grippy shoes on wet surfaces.'] },
    Cherrapunji: { state: 'Meghalaya', image: 'images/cherrapunji.jpg', vibe: 'Rain-fed waterfalls, forest paths, and remarkable living root bridges.', season: 'October to May', days: '3 days', budget: 'Rs. 14,000 - Rs. 24,000', mustDo: ['Root bridge trek', 'Nohkalikai Falls', 'Mawsmai Cave'], nearby: ['Mawlynnong', 'Dawki', 'Shillong'], tips: ['Use grippy footwear on wet paths.', 'Check rainfall before hikes.'] },
    Varanasi: { state: 'Uttar Pradesh', image: 'images/varanasi.jpg', vibe: 'Sacred river rituals, historic lanes, and powerful spiritual moments.', season: 'October to March', days: '2 days', budget: 'Rs. 8,000 - Rs. 15,000', mustDo: ['Sunrise boat ride', 'Old city lanes', 'Dashashwamedh aarti'], nearby: ['Sarnath', 'Chunar Fort', 'Vindhyachal'], tips: ['Maintain respectful distance.', 'Old lanes are best explored on foot.'] },
    Haridwar: { state: 'Uttarakhand', image: 'images/haridwar.jpg', vibe: 'Pilgrimage city shaped by the Ganga and sacred evening aarti.', season: 'October to April', days: '1-2 days', budget: 'Rs. 7,000 - Rs. 14,000', mustDo: ['Har Ki Pauri aarti', 'Ghat visit', 'Mansa Devi Temple'], nearby: ['Rishikesh', 'Rajaji NP', 'Dehradun'], tips: ['Watch belongings at crowded ghats.', 'Follow local instructions during bathing.'] },
    'Bodh Gaya': { state: 'Bihar', image: 'images/bodhgaya.jpg', vibe: 'Quiet international meditation hub with historic temples.', season: 'October to March', days: '2 days', budget: 'Rs. 9,000 - Rs. 16,000', mustDo: ['Mahabodhi Temple', 'Buddha statue', 'Monasteries'], nearby: ['Rajgir', 'Nalanda', 'Gaya'], tips: ['Maintain silence in meditation zones.', 'Early mornings are calmest.'] },
    Amritsar: { state: 'Punjab', image: 'images/amritsar.jpg', vibe: 'Golden Temple serenity, generous hospitality, and moving history.', season: 'October to March', days: '2 days', budget: 'Rs. 10,000 - Rs. 18,000', mustDo: ['Golden Temple', 'Langar meal', 'Jallianwala Bagh'], nearby: ['Pul Kanjari', 'Gobindgarh Fort', 'Harike'], tips: ['Cover head and remove footwear.', 'Arrive early at Wagah Border.'] },
    Mathura: { state: 'Uttar Pradesh', image: 'images/mathura.jpg', vibe: 'Sacred Braj heritage with colorful temples and river ghats.', season: 'October to March', days: '2 days', budget: 'Rs. 8,000 - Rs. 14,000', mustDo: ['Krishna Temple', 'Vishram Ghat aarti', 'Vrindavan circuit'], nearby: ['Vrindavan', 'Govardhan', 'Agra'], tips: ['Festivals are vibrant but crowded.', 'Dress modestly and plan footwear.'] },
    Pushkar: { state: 'Rajasthan', image: 'images/pushkar.jpg', vibe: 'Holy lake town with cafes, bazaars, and desert sunsets.', season: 'October to March', days: '2 days', budget: 'Rs. 8,000 - Rs. 15,000', mustDo: ['Lake ghats', 'Brahma Temple', 'Savitri sunset'], nearby: ['Ajmer', 'Kishangarh', 'Jaipur'], tips: ['Follow lake etiquette.', 'Carry modest layers for desert evenings.'] },
    'Jim Corbett': { state: 'Uttarakhand', image: 'images/jimcorbett.jpg', vibe: 'Forest safari escape with wildlife and river landscapes.', season: 'November to June', days: '2-3 days', budget: 'Rs. 15,000 - Rs. 25,000', mustDo: ['Jeep safari', 'Corbett museum', 'Kosi river'], nearby: ['Nainital', 'Ramnagar', 'Garjiya'], tips: ['Reserve safaris in advance.', 'Never leave vehicle in safari zones.'] },
    Kaziranga: { state: 'Assam', image: 'images/kaziranga.jpg', vibe: 'Grassland wilderness and one-horned rhino sightings.', season: 'November to April', days: '2-3 days', budget: 'Rs. 18,000 - Rs. 30,000', mustDo: ['Early jeep safari', 'Central range', 'Assamese evening'], nearby: ['Majuli', 'Jorhat', 'Orchid Park'], tips: ['Park usually closed in monsoon.', 'Use authorized safaris only.'] }
};

const DESTINATION_FAMOUS_FOR = {
    Goa: 'Golden beaches, Portuguese quarters, and beach shack nightlife.', Gokarna: 'Peaceful crescent beaches and sacred Mahabaleshwar Temple.', Varkala: 'Dramatic red cliffs and laid-back beach cafes.', 'Marina Beach': "One of India's longest urban beaches.", Kovalam: 'Lighthouse Beach and curved bays.', Rameswaram: 'Ramanathaswamy Temple and Pamban Bridge.', Manali: 'Snow views and high Himalayan adventure.', Shimla: 'Colonial heritage and toy train journey.', Mussoorie: 'Valley panoramas and hill-station charm.', Darjeeling: 'Tea gardens and toy train.', Nainital: 'Emerald lake and Kumaon views.', Mcleodganj: 'Tibetan culture and Triund trek.', Coorg: 'Coffee plantations and waterfalls.', Gangtok: 'Monasteries and mountain viewpoints.', Srinagar: 'Dal Lake houseboats and Mughal gardens.', Jaipur: 'Pink architecture and royal palaces.', Delhi: 'Mughal landmarks and street food.', Mumbai: 'Marine Drive and Bollywood.', Bangalore: 'Leafy gardens and café culture.', Kolkata: 'Art, literature, and Bengali cuisine.', Chennai: 'Temples and classical culture.', Agra: 'The Taj Mahal.', Indore: 'Night food market and street food.', Ahmedabad: 'UNESCO heritage lanes.', Hampi: 'Vijayanagara ruins and boulders.', Mysuru: 'Mysore Palace and Dasara.', Kasol: 'Parvati Valley and café culture.', Rishikesh: 'Yoga, rafting, and Ganga aarti.', Ladakh: 'High-altitude desert landscapes.', Andaman: 'Turquoise islands and scuba.', Panchmarhi: 'Waterfalls and forest trails.', Cherrapunji: 'Living root bridges and falls.', Varanasi: 'Ghats and spiritual ceremonies.', Haridwar: 'Har Ki Pauri and Ganga aarti.', 'Bodh Gaya': 'Mahabodhi Temple and Buddhism.', Amritsar: 'Golden Temple and Sikh history.', Mathura: 'Krishna heritage and temples.', Pushkar: 'Holy lake and Brahma Temple.', 'Jim Corbett': 'Tiger reserve and safari.', Kaziranga: 'One-horned rhinos and grasslands.'
};

// Destination overrides — kept empty intentionally. Budget is calculated purely from tier + travelers + days.
const DESTINATION_OVERRIDES = {};

const DESTINATION_ITINERARY_STOPS = {
    Goa: {
        morning: ['Basilica of Bom Jesus', 'Fort Aguada', 'Dudhsagar Falls'],
        afternoon: ['Calangute Beach', 'Candolim Beach', 'Fontainhas'],
        evening: ['Baga Beach', 'Chapora Fort', 'Anjuna Beach']
    },
    Gokarna: {
        morning: ['Mahabaleshwar Temple', 'Om Beach Viewpoint', 'Half Moon Beach'],
        afternoon: ['Kudle Beach', 'Gokarna Main Beach', 'Paradise Beach'],
        evening: ['Sunset Point Gokarna', 'Beachside cafes in Kudle', 'Temple street market']
    },
    Varkala: {
        morning: ['Varkala Cliff', 'Janardanaswamy Temple', 'Papanasam Beach'],
        afternoon: ['Kappil Beach', 'Jatayu Earth Center', 'Black Sand Beach'],
        evening: ['Varkala Cliff cafes', 'Edava Beach', 'North Cliff promenade']
    },
    'Marina Beach': {
        morning: ['Marina Beach', 'Kapaleeshwarar Temple', 'San Thome Basilica'],
        afternoon: ['Government Museum Chennai', 'Mylapore', 'Besant Nagar Beach'],
        evening: ['Marina Beach promenade', 'Elliot’s Beach', 'Mylapore food streets']
    },
    Kovalam: {
        morning: ['Lighthouse Beach', 'Vizhinjam Rock Cut Cave Temple', 'Hawa Beach'],
        afternoon: ['Samudra Beach', 'Poovar Backwaters', 'Vizhinjam Harbour'],
        evening: ['Kovalam beach promenade', 'Lighthouse sunset point', 'Ayurveda spa strip']
    },
    Rameswaram: {
        morning: ['Ramanathaswamy Temple', 'Pamban Bridge', 'APJ Abdul Kalam Memorial'],
        afternoon: ['Dhanushkodi Beach', 'Ariyaman Beach', 'Agni Theertham'],
        evening: ['Pamban sunset point', 'Temple streets of Rameswaram', 'Seashore walk near Agni Theertham']
    },
    Manali: {
        morning: ['Hadimba Devi Temple', 'Solang Valley', 'Vashisht Temple'],
        afternoon: ['Old Manali', 'Naggar Castle', 'Van Vihar'],
        evening: ['Mall Road Manali', 'Old Manali cafes', 'Club House riverside']
    },
    Shimla: {
        morning: ['Jakhoo Temple', 'Christ Church Shimla', 'The Ridge'],
        afternoon: ['Kufri', 'Viceregal Lodge', 'Mall Road Shimla'],
        evening: ['Scandal Point', 'Mall Road evening walk', 'Lakkar Bazaar']
    },
    Mussoorie: {
        morning: ['Gun Hill', 'Camel Back Road', 'George Everest Peak'],
        afternoon: ['Kempty Falls', 'Company Garden', 'Landour'],
        evening: ['Mall Road Mussoorie', 'Landour Bakehouse area', 'Library Chowk']
    },
    Darjeeling: {
        morning: ['Tiger Hill', 'Batasia Loop', 'Ghoom Monastery'],
        afternoon: ['Happy Valley Tea Estate', 'Padmaja Naidu Himalayan Zoo', 'Darjeeling Ropeway'],
        evening: ['Mall Road Darjeeling', 'Chowrasta', 'Observatory Hill']
    },
    Nainital: {
        morning: ['Naini Lake', 'Naina Devi Temple', 'Snow View Point'],
        afternoon: ['Tiffin Top', 'Eco Cave Gardens', 'Mallital'],
        evening: ['Mall Road Nainital', 'Naina Lake promenade', 'Boat House Club area']
    },
    Mcleodganj: {
        morning: ['Tsuglagkhang Complex', 'Bhagsu Falls', 'St. John in the Wilderness'],
        afternoon: ['Dharamkot', 'Dalai Lama Temple', 'Namgyal Monastery'],
        evening: ['Mcleodganj Market', 'Temple Road cafes', 'Sunset Point Naddi']
    },
    Coorg: {
        morning: ['Abbey Falls', 'Raja’s Seat', 'Talacauvery'],
        afternoon: ['Dubare Elephant Camp', 'Coffee plantation trail', 'Madikeri Fort'],
        evening: ['Madikeri town market', 'Raja’s Seat sunset', 'Plantation stay trails']
    },
    Gangtok: {
        morning: ['Rumtek Monastery', 'Tsomgo Lake', 'Ganesh Tok'],
        afternoon: ['MG Marg', 'Hanuman Tok', 'Namgyal Institute of Tibetology'],
        evening: ['MG Marg promenade', 'Ridge Park', 'Lal Bazaar']
    },
    Srinagar: {
        morning: ['Dal Lake', 'Shalimar Bagh', 'Nishat Bagh'],
        afternoon: ['Pari Mahal', 'Shankaracharya Temple', 'Hazratbal Shrine'],
        evening: ['Boulevard Road', 'Shikara ghat area', 'Old Srinagar market']
    },
    Jaipur: {
        morning: ['Amber Fort', 'Hawa Mahal', 'Jantar Mantar'],
        afternoon: ['City Palace', 'Albert Hall Museum', 'Jaigarh Fort'],
        evening: ['Nahargarh Fort', 'Bapu Bazaar', 'Jal Mahal viewpoint']
    },
    Delhi: {
        morning: [
            'Red Fort',
            'Jama Masjid',
            'Humayun Tomb',
            'Qutub Minar',
            'Raj Ghat'
        ],
        afternoon: [
            'Chandni Chowk',
            'National Museum',
            'Lodhi Garden',
            'Connaught Place',
            'Dilli Haat'
        ],
        evening: [
            'India Gate',
            'Bangla Sahib',
            'Hauz Khas Village',
            'Akshardham Temple',
            'Khan Market'
        ]
    },
    Mumbai: {
        morning: ['Gateway of India', 'Chhatrapati Shivaji Maharaj Terminus', 'Elephanta Caves'],
        afternoon: ['Kala Ghoda', 'Colaba Causeway', 'Haji Ali Dargah'],
        evening: ['Marine Drive', 'Bandra Bandstand', 'Juhu Beach']
    },
    Bangalore: {
        morning: ['Lalbagh Botanical Garden', 'Cubbon Park', 'Bangalore Palace'],
        afternoon: ['Visvesvaraya Museum', 'Tipu Sultan’s Summer Palace', 'Commercial Street'],
        evening: ['Church Street', 'MG Road', 'UB City']
    },
    Kolkata: {
        morning: ['Victoria Memorial', 'Dakshineswar Kali Temple', 'Indian Museum'],
        afternoon: ['College Street', 'Kumartuli', 'Howrah Bridge riverside'],
        evening: ['Prinsep Ghat', 'Park Street', 'Hooghly riverfront']
    },
    Chennai: {
        morning: ['Marina Beach', 'Kapaleeshwarar Temple', 'Fort St. George'],
        afternoon: ['Government Museum Chennai', 'San Thome Basilica', 'Mylapore'],
        evening: ['Besant Nagar Beach', 'Marina promenade', 'T Nagar']
    },
    Agra: {
        morning: ['Taj Mahal', 'Agra Fort', 'Mehtab Bagh'],
        afternoon: ['Itmad-ud-Daulah', 'Sadar Bazaar', 'Fatehpur Sikri'],
        evening: ['Mehtab Bagh sunset', 'Taj Nature Walk', 'Kinari Bazaar']
    },
    Indore: {
        morning: ['Rajwada Palace', 'Lal Bagh Palace', 'Kanch Mandir'],
        afternoon: ['Chappan Dukan', 'Central Museum Indore', 'Annapurna Temple'],
        evening: ['Sarafa Bazaar', 'Rajwada market streets', 'Meghdoot Garden']
    },
    Ahmedabad: {
        morning: ['Sabarmati Ashram', 'Adalaj Stepwell', 'Jama Masjid Ahmedabad'],
        afternoon: ['Sidi Saiyyed Mosque', 'Kankaria Lake', 'Calico Museum area'],
        evening: ['Sabarmati Riverfront', 'Law Garden Night Market', 'Manek Chowk']
    },
    Hampi: {
        morning: ['Virupaksha Temple', 'Vittala Temple', 'Hemakuta Hill'],
        afternoon: ['Lotus Mahal', 'Elephant Stables', 'Achyutaraya Temple'],
        evening: ['Matanga Hill', 'Tungabhadra riverside', 'Hampi Bazaar']
    },
    Mysuru: {
        morning: ['Mysore Palace', 'Chamundi Hill', 'St. Philomena’s Church'],
        afternoon: ['Jaganmohan Palace', 'Devaraja Market', 'Mysore Zoo'],
        evening: ['Brindavan Gardens', 'Palace illumination area', 'Mall of Mysore district']
    },
    Kasol: {
        morning: ['Parvati River trail', 'Chalal village', 'Manikaran Sahib'],
        afternoon: ['Kasol Market', 'Tosh village', 'Nature Park Kasol'],
        evening: ['Kasol cafes', 'Riverside sunset point', 'Old Kasol lanes']
    },
    Rishikesh: {
        morning: ['Lakshman Jhula', 'Ram Jhula', 'Parmarth Niketan'],
        afternoon: ['Neer Garh Waterfall', 'Beatles Ashram', 'Triveni Ghat'],
        evening: ['Ganga Aarti at Parmarth Niketan', 'Tapovan cafes', 'Riverfront promenade']
    },
    Ladakh: {
        morning: ['Shanti Stupa', 'Thiksey Monastery', 'Leh Palace'],
        afternoon: ['Magnetic Hill', 'Hall of Fame Museum', 'Sangam Point'],
        evening: ['Leh Market', 'Shanti Stupa sunset', 'Old Leh streets']
    },
    Andaman: {
        morning: ['Radhanagar Beach', 'Cellular Jail', 'Ross Island'],
        afternoon: ['Corbyn’s Cove', 'North Bay Island', 'Neil Island jetty area'],
        evening: ['Cellular Jail light and sound show', 'Beachside promenade in Port Blair', 'Sunset at Chidiya Tapu']
    },
    Panchmarhi: {
        morning: ['Bee Falls', 'Pandav Caves', 'Jatashankar Cave'],
        afternoon: ['Handi Khoh', 'Priyadarshini Point', 'Reechgarh'],
        evening: ['Dhoopgarh sunset', 'Panchmarhi market', 'Lakeside walk']
    },
    Cherrapunji: {
        morning: ['Nohkalikai Falls', 'Mawsmai Cave', 'Seven Sisters Falls'],
        afternoon: ['Eco Park Cherrapunji', 'Wakaba Falls', 'Arwah Cave'],
        evening: ['Sunset viewpoint Sohra', 'Local market area', 'Cliffside walk']
    },
    Varanasi: {
        morning: ['Dashashwamedh Ghat', 'Kashi Vishwanath Temple', 'Assi Ghat'],
        afternoon: ['Sarnath', 'Manikarnika Ghat viewpoint', 'Banaras Hindu University'],
        evening: ['Ganga Aarti', 'Godowlia market', 'Riverside promenade']
    },
    Haridwar: {
        morning: ['Har Ki Pauri', 'Mansa Devi Temple', 'Chandi Devi Temple'],
        afternoon: ['Bharat Mata Mandir', 'Shantikunj', 'Rajaji National Park gate area'],
        evening: ['Har Ki Pauri aarti', 'Local bazaar near ghats', 'Ganga canal walk']
    },
    'Bodh Gaya': {
        morning: ['Mahabodhi Temple', 'Great Buddha Statue', 'Bodhi Tree'],
        afternoon: ['Thai Monastery', 'Japanese Temple', 'Archaeological Museum Bodh Gaya'],
        evening: ['Monastery circuit walk', 'Meditation park area', 'Temple street cafes']
    },
    Amritsar: {
        morning: ['Golden Temple', 'Jallianwala Bagh', 'Durgiana Temple'],
        afternoon: ['Partition Museum', 'Gobindgarh Fort', 'Hall Bazaar'],
        evening: ['Wagah Border', 'Golden Temple night view', 'Lawrence Road food street']
    },
    Mathura: {
        morning: ['Shri Krishna Janmabhoomi', 'Dwarkadhish Temple', 'Vishram Ghat'],
        afternoon: ['Vrindavan Banke Bihari Temple', 'Prem Mandir', 'Govardhan route viewpoint'],
        evening: ['Yamuna aarti', 'Vrindavan market', 'Temple lights at Prem Mandir']
    },
    Pushkar: {
        morning: ['Pushkar Lake', 'Brahma Temple', 'Varaha Temple'],
        afternoon: ['Savitri Temple', 'Pushkar Bazaar', 'Rangji Temple'],
        evening: ['Pushkar ghats', 'Sunset at Savitri viewpoint', 'Cafe strip in Pushkar']
    },
    'Jim Corbett': {
        morning: ['Corbett Jeep Safari Zone', 'Garjiya Devi Temple', 'Kosi River bank'],
        afternoon: ['Corbett Museum', 'Sitabani forest edge', 'Ramnagar market'],
        evening: ['Riverside resort trail', 'Sunset near Kosi', 'Bonfire area at stay']
    },
    Kaziranga: {
        morning: ['Kaziranga Central Range Safari', 'Kaziranga Orchid Park', 'Western Range viewpoint'],
        afternoon: ['Elephant safari zone', 'Tea garden trail nearby', 'Local Assamese craft center'],
        evening: ['Cultural performance venue', 'Resort nature walk', 'Sunset point near grasslands']
    }
};

// ========== FALLBACK ITINERARY BUILDER ==========
function buildFallbackItinerary(requestBody) {
    const city = requestBody.city || requestBody.placeName || requestBody.destinationCity || requestBody.region || 'Destination';
    const guide = getDestinationGuide(city);
    const cityStops = DESTINATION_ITINERARY_STOPS[city] || null;
    const tripLength = Number(requestBody.days) || 3;
    const totalDays = Math.max(1, Math.min(14, tripLength));
    const travellerType = String(requestBody.travellerType || '').toLowerCase();
    const budgetLevel = requestBody.budgetLevel || 'midrange';

    const mustDos = Array.isArray(guide.mustDo) && guide.mustDo.length ? guide.mustDo.slice() : [
        'Main sightseeing area',
        'Local food stop',
        'Scenic evening walk'
    ];
    const nearby = Array.isArray(guide.nearby) && guide.nearby.length ? guide.nearby.slice() : [
        'Nearby viewpoint',
        'Nearby market',
        'Nearby cultural stop'
    ];

    // Preference-based scoring
    const prefSource = [
        requestBody.travelStyle,
        requestBody.preferences,
        requestBody.interests,
        requestBody.category,
        requestBody.mood
    ];
    const prefText = prefSource
        .flatMap(value => Array.isArray(value) ? value : String(value || '').split(','))
        .map(value => String(value || '').trim())
        .filter(Boolean)
        .join(' ');
    const prefs = prefText.toLowerCase().split(/[,;\s]+/).filter(Boolean);

    function activityScore(text) {
        if (!text) return 0;
        const t = String(text).toLowerCase();
        let score = 0;
        prefs.forEach(p => {
            if (t.includes(p)) score += 2;
            const shorter = p.replace(/[^a-z0-9]/g, '');
            if (shorter && t.includes(shorter) && shorter.length > 3) score += 1;
        });
        if (/temple|heritage|museum|fort|palace|historic/.test(t)) score += 1;
        if (/beach|sea|coast|island|scuba|snorkel/.test(t)) score += 1;
        if (/trek|hike|valley|peak|mountain/.test(t)) score += 1;
        if (/food|restaurant|dinner|cafe|street food/.test(t)) score += 1;
        if (/wildlife|safari|park|reserve/.test(t)) score += 1;
        return score;
    }

    if (prefs.length) {
        mustDos.sort((a, b) => activityScore(b) - activityScore(a));
        nearby.sort((a, b) => activityScore(b) - activityScore(a));
    }

    // Travellertype-based pacing
    const paceText = String(requestBody.pace || requestBody.travelPace || '').toLowerCase();
    const relaxed = /relax|slow|leisure|relaxed/.test(paceText) || prefs.includes('relaxed') || prefs.includes('leisure')
        || travellerType === 'family';
    const stopsPerDay = relaxed ? 2 : 3;

    const mainCityDays = totalDays;
    const nearbyCityDays = 0;

    const itinerary = [];
    const famousSnippet = String(getFamousFor(city) || '')
        .split(/[.;]/)[0]
        .trim();
    const addUnique = (list, value) => {
        const normalized = String(value || '').trim();
        if (!normalized) return;
        if (!list.some(item => item.toLowerCase() === normalized.toLowerCase())) {
            list.push(normalized);
        }
    };

    const morningPool = [];
    const afternoonPool = [];
    const eveningPool = [];

    if (cityStops) {
        cityStops.morning.forEach(item => addUnique(morningPool, item));
        cityStops.afternoon.forEach(item => addUnique(afternoonPool, item));
        cityStops.evening.forEach(item => addUnique(eveningPool, item));
    } else {
        mustDos.forEach((activity, index) => {
            if (index % 3 === 0) addUnique(morningPool, activity);
            else if (index % 3 === 1) addUnique(afternoonPool, activity);
            else addUnique(eveningPool, activity);
        });

        [
            `${city} heritage walk`,
            `${city} landmark circuit`,
            famousSnippet ? `${city} highlights around ${famousSnippet.toLowerCase()}` : `${city} old quarter walk`
        ].forEach(item => addUnique(morningPool, item));

        [
            `${city} museum and culture stop`,
            `${city} local market walk`,
            `${city} food trail`
        ].forEach(item => addUnique(afternoonPool, item));

        [
            `${city} sunset viewpoint`,
            `${city} cafe and street food evening`,
            `${city} neighborhood walk`
        ].forEach(item => addUnique(eveningPool, item));
    }

    if (!morningPool.length) addUnique(morningPool, `${city} highlights walk`);
    if (!afternoonPool.length) addUnique(afternoonPool, `${city} local discovery trail`);
    if (!eveningPool.length) addUnique(eveningPool, `${city} evening stroll`);

    const pickFromPool = (pool, index, usedToday) => {
        for (let offset = 0; offset < pool.length; offset++) {
            const candidate = pool[(index + offset) % pool.length];
            if (!usedToday.has(candidate.toLowerCase())) {
                usedToday.add(candidate.toLowerCase());
                return candidate;
            }
        }

        const fallback = pool[index % pool.length];
        usedToday.add(String(fallback).toLowerCase());
        return fallback;
    };

    // === MAIN CITY DAYS ===
    for (let day = 0; day < mainCityDays; day++) {
        const dayPlaces = [];
        const usedToday = new Set();
        const primary = pickFromPool(morningPool, day, usedToday);
        const secondary = pickFromPool(afternoonPool, day, usedToday);
        const eveningStop = pickFromPool(eveningPool, day, usedToday);

        dayPlaces.push({
            placeName: primary,
            plannedVisitTimeSlot: 'Morning',
            description: day === 0
                ? `Arrive in ${city}, orient yourself, and start with ${String(primary).toLowerCase()}.`
                : `Continue exploring ${city} with a visit to ${String(primary).toLowerCase()}.`,
            recommendedDurationHours: 2.5,
            localTips: 'Carry water and start early.',
            safetyAdvice: 'Check local timings before visiting.'
        });

        if (stopsPerDay >= 2) {
            dayPlaces.push({
                placeName: secondary,
                plannedVisitTimeSlot: 'Afternoon',
                description: `Continue through ${city} with an afternoon around ${secondary.toLowerCase()}.`,
                recommendedDurationHours: 2,
                localTips: 'Use local transport for shorter hops.',
                safetyAdvice: 'Stay aware of crowd levels.'
            });
        }

        if (stopsPerDay >= 3) {
            dayPlaces.push({
                placeName: eveningStop,
                plannedVisitTimeSlot: 'Evening',
                description: day === mainCityDays - 1
                    ? `Wrap up the day in ${city} with ${String(eveningStop).toLowerCase()} and an easy evening pace.`
                    : `Wind down in ${city} with ${String(eveningStop).toLowerCase()}.`,
                recommendedDurationHours: 1.5,
                localTips: 'Keep plans flexible for weather or traffic.',
                safetyAdvice: 'Return before it gets too late if unfamiliar area.'
            });
        }

        const travellerHint = travellerType === 'family'
            ? 'Plan kid breaks and meal stops near each attraction.'
            : travellerType === 'couple'
                ? 'Add a relaxed café stop between attractions.'
                : travellerType === 'friends'
                    ? 'Include flexible social breaks between spots.'
                    : 'Start early for popular sights and keep a buffer.';

        itinerary.push({
            dayNumber: day + 1,
            location: { city, state: guide.state || city },
            daySummary: day === 0
                ? `Arrive in ${city}, settle in, and begin with a balanced first look at its main highlights.`
                : `Spend day ${day + 1} exploring a different side of ${city}, mixing landmarks, local flavor, and easier evening time.`,
            travelNotes: travellerHint,
            places: dayPlaces
        });
    }

    // === NEARBY CITY EXPANSION DAYS ===
    // Pull guides for nearby cities to give each day meaningful content
    const nearbyNames = nearby.slice(); // Kasol's nearby = ['Manikaran', 'Tosh', 'Kheerganga']
    for (let nd = 0; nd < nearbyCityDays; nd++) {
        const nearbyCity = nearbyNames[nd % nearbyNames.length];
        const nearbyGuide = getDestinationGuide(nearbyCity);
        const nearbyActivities = Array.isArray(nearbyGuide.mustDo) && nearbyGuide.mustDo.length
            ? nearbyGuide.mustDo.slice(0, stopsPerDay)
            : [`Explore ${nearbyCity}`, `Local food in ${nearbyCity}`, `Scenic walk in ${nearbyCity}`].slice(0, stopsPerDay);

        const slots = ['Morning', 'Afternoon', 'Evening'];
        const dayPlaces = nearbyActivities.map((activity, i) => ({
            placeName: activity,
            plannedVisitTimeSlot: slots[i] || 'Afternoon',
            description: i === 0
                ? `Day trip to ${nearbyCity} from ${city} — start with ${String(activity).toLowerCase()}.`
                : `Continue in ${nearbyCity}: ${String(activity).toLowerCase()}.`,
            recommendedDurationHours: i === nearbyActivities.length - 1 ? 1.5 : 2,
            localTips: nearbyGuide.tips?.[0] || `Start early for ${nearbyCity}.`,
            safetyAdvice: `Stay on marked routes around ${nearbyCity}.`
        }));

        itinerary.push({
            dayNumber: mainCityDays + nd + 1,
            location: { city: nearbyCity, state: nearbyGuide.state || guide.state || nearbyCity },
            daySummary: `Day trip to ${nearbyCity} — a perfect complement to your ${city} visit.`,
            travelNotes: `Travel from ${city} to ${nearbyCity} takes roughly 1-2 hours depending on the route. Return to ${city} by evening.`,
            places: dayPlaces
        });
    }

    const perPersonPerDay = mapBudgetLevelToDaily(budgetLevel, city) || 2500;
    const groupSize = Number(requestBody.groupSize) || (
        travellerType === 'solo' ? 1
        : travellerType === 'couple' ? 2
        : travellerType === 'family' ? 4
        : travellerType === 'friends' ? 5
        : 1
    );
    const totalBudgetVal = perPersonPerDay * groupSize * totalDays;

    return {
        success: true,
        city,
        region: requestBody.region || guide.state || '',
        generatedDays: itinerary.length,
        requestedDays: totalDays,
        totalPlaces: itinerary.reduce((sum, d) => sum + d.places.length, 0),
        totalBudget: totalBudgetVal,
        dataSource: 'LOCAL_FALLBACK',
        summary: guide.vibe || `A practical travel plan for ${city}.`,
        aiSummary: nearbyCityDays > 0
            ? `This itinerary covers ${city} for ${mainCityDays} day${mainCityDays > 1 ? 's' : ''} and adds day trips to nearby destinations (${nearbyNames.slice(0, nearbyCityDays).join(', ')}) to fill your ${totalDays}-day trip.`
            : `A local guide itinerary for ${city} based on destination data.`,
        tips: getExtendedTips ? getExtendedTips(city, guide) : (guide.tips || []),
        itinerary
    };
}


// small utility to render nearby attractions list
function renderNearbyAttractions(guide) {
    const nearby = Array.isArray(guide?.nearby) ? guide.nearby : [];
    if (!nearby.length) return '<p style="color:#666;">No nearby attractions listed.</p>';
    return `<ul class="nearby-list">${nearby.map(n => `<li><i class="fas fa-location-dot"></i> ${escapeHtml(n)}</li>`).join('')}</ul>`;
}

function escapeHtml(value) {
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}


// ========== CONTACT FORM FUNCTIONS ==========
function submitContactForm(event) {
    event.preventDefault();

    const firstName = document.getElementById('firstName')?.value;
    const lastName = document.getElementById('lastName')?.value;
    const email = document.getElementById('contactEmail')?.value;
    const phone = document.getElementById('contactPhone')?.value;
    const subject = document.getElementById('contactSubject')?.value;
    const message = document.getElementById('contactMessage')?.value;

    if (!firstName || !lastName || !email || !subject || !message) {
        alert('Please fill in all required fields');
        return;
    }

    console.log('Contact form submission:', { firstName, lastName, email, phone, subject, message });

    const statusDiv = document.getElementById('contactFormStatus');
    if (statusDiv) {
        statusDiv.style.display = 'block';
        statusDiv.style.background = '#c8e6c9';
        statusDiv.style.color = '#2e7d32';
        statusDiv.textContent = '✅ Thank you for reaching out! We\'ll get back to you soon.';
    }

    document.getElementById('contactForm')?.reset();
}

// ========== DESTINATION DETAIL PAGE FUNCTIONS ==========

function getDestinationGuide(placeName) {
    return DESTINATION_GUIDES[placeName] || {
        state: placeName, image: 'images/placeholder.jpg', vibe: 'A unique destination.',
        season: 'Year-round', days: '2 days', budget: 'Rs. 10,000 - Rs. 20,000',
        mustDo: ['Explore local attractions', 'Taste local food', 'Take photos'],
        nearby: ['Nearby attractions'], tips: ['Plan ahead', 'Check weather']
    };
}

function getFamousFor(placeName) {
    return DESTINATION_FAMOUS_FOR[placeName] || 'A beautiful and culturally rich destination worth exploring.';
}

function formatRupees(value) {
    return new Intl.NumberFormat('en-IN').format(Math.max(0, Math.round(value)));
}

function getPlaceIntroSummary(placeName) {
    const guide = getDestinationGuide(placeName);
    const famous = String(getFamousFor(placeName)).replace(/[.\s]+$/, '');
    return `${placeName} is a ${guide.days.replace(/^( )?/, '')} escape known for ${famous}. It suits travelers who want an easygoing base, scenic views, and a mix of local food and landmark stops.`.replace('days escape', 'day escape').replace('3 day escape', '3-day escape').replace('2 day escape', '2-day escape').replace('2-3 day escape', '2-3 day escape');
}

function getPlaceAboutParagraph(placeName) {
    const guide = getDestinationGuide(placeName);
    const famous = getFamousFor(placeName);
    const famousText = String(famous).replace(/[.\s]+$/, '').toLowerCase();
    return `${placeName} has a distinct character shaped by ${guide.vibe.toLowerCase()} and a pace that feels easy to settle into. The destination pairs ${famousText} with relaxed sightseeing, local food, and scenic viewpoints, so the trip feels calm but still full of highlights.`;
}

function parseBudgetRange(budgetRange) {
    const values = String(budgetRange || '')
        .match(/\d[\d,]*/g)
        ?.map(value => Number(value.replace(/,/g, '')))
        .filter(Number.isFinite) || [];

    if (values.length >= 2) {
        return Math.round((values[0] + values[1]) / 2);
    }

    if (values.length === 1) {
        return values[0];
    }

    return 15000;
}

function parseGuideDays(daysText) {
    const values = String(daysText || '')
        .match(/\d+/g)
        ?.map(Number)
        .filter(Number.isFinite) || [];

    if (values.length >= 2) {
        return Math.max(1, (values[0] + values[1]) / 2);
    }

    if (values.length === 1) {
        return Math.max(1, values[0]);
    }

    return 2;
}

function getEstimatedBudgetBreakdown(budgetRange) {
    const avgBudget = parseBudgetRange(budgetRange);
    const stay = Math.round(avgBudget * 0.40);
    const food = Math.round(avgBudget * 0.18);
    const travel = Math.round(avgBudget * 0.20);
    const experiences = avgBudget - stay - food - travel;
    return { stay, food, travel, experiences };
}

// ========== BUDGET CALCULATION ==========

/**
 * Returns the average per-traveler/day rate for a budget tier (INR).
 * Budget:  ₹1,000 – ₹2,000  → avg ₹1,500
 * Comfort: ₹2,000 – ₹3,500  → avg ₹3,000
 * Premium: ₹4,000 – ₹8,000  → avg ₹6,000
 * Luxury:  ₹10,000+          → avg ₹10,000
 */
function mapBudgetLevelToDaily(level, destination) {
    if (!level) return null;
    const l = String(level).toLowerCase();
    
    const tier = getBudgetPreferenceRange(l);
    if (!tier) return null;

    // Determine variance range for this tier (narrower than global min/max)
    const varianceMap = {
        'budget': { min: 1200, max: 1800, step: 100 },
        'midrange': { min: 2500, max: 3200, step: 100 },
        'premium': { min: 4500, max: 7500, step: 100 },
        'luxury': { min: 10000, max: 15000, step: 100 }
    };

    const variance = varianceMap[l] || { min: tier.min, max: tier.max, step: 100 };

    // Get deterministic seed from destination name
    const seed = getDestinationSeed(destination || '');
    const seedFraction = seed % 1000 / 1000; // 0.000 – 0.999

    // Map seed to step index within variance range
    const stepCount = Math.round((variance.max - variance.min) / variance.step) + 1;
    const stepIndex = Math.floor(seedFraction * (stepCount - 1));
    
    // Calculate daily cost: min + (stepIndex * step)
    const dailyCost = variance.min + (stepIndex * variance.step);

    // Clamp to global tier bounds
    return Math.max(tier.min, Math.min(tier.max, dailyCost));
}

function getBudgetPreferenceRange(level) {
    const normalized = String(level || '').toLowerCase();
    switch (normalized) {
        case 'budget':   return { min: 1000,  max: 2000,  average: 1500,  label: 'Budget Traveler' };
        case 'midrange': return { min: 2000,  max: 3500,  average: 3000,  label: 'Comfortable Explorer' };
        case 'premium':  return { min: 4000,  max: 8000,  average: 6000,  label: 'Premium Experience' };
        case 'luxury':   return { min: 10000, max: 15000, average: 10000, label: 'Luxury Adventure' };
        default:         return { min: 2000,  max: 3500,  average: 3000,  label: 'Comfortable Explorer' };
    }
}

function clampBudgetValue(value, min, max) {
    return Math.min(Math.max(value, min), max);
}

/**
 * Deterministic seed from destination name so the same destination always
 * produces the same variance offset — results feel like estimates, not a calculator.
 */
function getDestinationSeed(name) {
    if (!name) return 42;
    let hash = 0;
    const s = String(name).toLowerCase();
    for (let i = 0; i < s.length; i++) {
        hash = ((hash << 5) - hash + s.charCodeAt(i)) | 0;
    }
    return Math.abs(hash);
}

/**
 * Compute a full budget breakdown.
 *
 * Supports two calling conventions:
 *   1. computeBudgetBreakdown(dataObj)               — data object from itinerary/planner
 *   2. computeBudgetBreakdown(level, travelers, days, destName) — explicit args
 *
 * Budget tiers (per traveler / day):
 *   budget   → ₹1,000 – ₹2,000  (avg ₹1,500)
 *   midrange → ₹2,000 – ₹3,500  (avg ₹2,750)
 *   premium  → ₹4,000 – ₹8,000  (avg ₹6,000)
 *   luxury   → ₹10,000 – ₹25,000 (avg ₹12,000)
 */
function computeBudgetBreakdown(budgetLevelOrData, travelers, days, destinationName) {
    let budgetLevel, numTravelers, numDays, destName;

    // Detect single-object calling convention
    if (budgetLevelOrData && typeof budgetLevelOrData === 'object') {
        const data = budgetLevelOrData;
        const req  = data.plannerRequest || {};
        budgetLevel  = req.budgetLevel || data.budgetLevel || 'midrange';

        // Priority: explicit groupSize > travellerType inference
        let travellersCount = null;

        // 1. Try explicit groupSize from planner request (set by planner for family/friends)
        if (req.groupSize && Number(req.groupSize) > 0) {
            travellersCount = Number(req.groupSize);
        }
        // 2. Try travelers/groupSize fields directly on data or req
        else if (req.travelers && Number(req.travelers) > 0) {
            travellersCount = Number(req.travelers);
        }
        else if (data.travelers && Number(data.travelers) > 0) {
            travellersCount = Number(data.travelers);
        }
        // 3. Infer from travellerType with clear defaults
        else {
            const tType = String(req.travellerType || data.travellerType || '').toLowerCase().trim();
            if (tType === 'solo') travellersCount = 1;
            else if (tType === 'couple' || tType === 'romantic') travellersCount = 2;
            else if (tType === 'family') travellersCount = 4;
            else if (tType === 'friends' || tType === 'group') travellersCount = 5;
            else travellersCount = 1;
        }

        numTravelers = Math.max(1, Number(travellersCount) || 1);
        numDays      = Math.max(1, Number(req.days || data.generatedDays || data.days || 3));
        destName     = data.selectedDestination || req.city || data.city || '';
    } else {
        budgetLevel  = String(budgetLevelOrData || 'midrange');
        numTravelers = Math.max(1, Number(travelers) || 1);
        numDays      = Math.max(1, Number(days) || 1);
        destName     = String(destinationName || '');
    }

    // Use deterministic variance based on tier and destination
    const perPersonPerDay  = mapBudgetLevelToDaily(budgetLevel, destName);
    const total = perPersonPerDay * numTravelers * numDays;

    // Calculate category breakdown with exact sum guarantee
    const breakdown = calculateCategoryBreakdown(total);

    return {
        perPersonPerDay:  perPersonPerDay,
        dailyPerTraveller: perPersonPerDay,
        totalPerDay:      perPersonPerDay * numTravelers,
        total:            breakdown.total,
        hotel:            breakdown.hotel,
        food:             breakdown.food,
        transport:        breakdown.transport,
        activities:       breakdown.activities,
        tierLabel:        getBudgetPreferenceRange(budgetLevel).label,
        travelers:        numTravelers,
        travellers:       numTravelers,
        days:             numDays,
        destination:      destName
    };
}

/** Alias — itinerary.js calls this with a single data object */
function computeDestinationAwareBudgetBreakdown(budgetLevelOrData, travelers, days, destinationName) {
    return computeBudgetBreakdown(budgetLevelOrData, travelers, days, destinationName);
}

function getTripDestinationName(data) {
    return data?.selectedDestination
        || data?.plannerRequest?.city
        || data?.plannerResponse?.city
        || data?.city
        || null;
}

/**
 * Allocate total budget into categories with exact sum guarantee.
 * Hotel: 40%, Food: 18%, Transport: 20%, Activities: Remainder
 * The Activities category is computed last to guarantee sum = total exactly.
 */
function calculateCategoryBreakdown(total) {
    const hotel = Math.round(total * 0.40);
    const food = Math.round(total * 0.18);
    const transport = Math.round(total * 0.20);
    const activities = total - hotel - food - transport; // Computed last to guarantee exact sum
    
    return {
        total,
        hotel,
        food,
        transport,
        activities
    };
}

// ========== ACTIVITY / ITINERARY HELPERS ==========

function getActivityDescription(activity, placeName) {
    const descMap = {
        'Calangute Beach sunset': 'Popular golden hour experience with calm water and beach vendors.',
        'Old Goa heritage walk': 'Historic Portuguese architecture dating back centuries.',
        'Local seafood dinner': 'Fresh catch prepared in traditional coastal style.',
        'Om Beach viewpoint': 'Iconic crescent beach with spiritual significance.',
        'Kudle Beach walk': 'Scenic coastal trail with peaceful surroundings.',
        'Half Moon Beach hike': 'Short trek to secluded beach with rock formations.',
        'Cliff promenade walk': 'Elevated pathway with Arabian Sea views.',
        'Papanasam Beach sunset': 'Sacred beach famous for spiritual bathing.',
        'Spa or yoga session': 'Wellness experience with Ayurvedic treatments.',
        'Early morning promenade': 'Peaceful walk along the urban beachfront.',
        'Mylapore temple streets': 'Historic neighborhood with ancient temples.',
        'South Indian breakfast': 'Traditional cuisine featuring dosas and filter coffee.',
        'Lighthouse climb': 'Panoramic views from the historic structure.',
        'Hawa Beach sunset': 'Breezy coastline perfect for evening relaxation.',
        'Ayurveda session': 'Traditional healing and wellness treatments.',
        'Ramanathaswamy Temple': 'Sacred pilgrimage site with intricate architecture.',
        'Pamban Bridge views': 'Iconic bridge with historical significance.',
        'Dhanushkodi trip': "Scenic coastal village at island's tip.",
        'Old Manali walk': 'Charming cobbled streets and local cafes.',
        'Solang Valley activities': 'Adventure sports and scenic viewpoints.',
        'Snow point visit': 'High-altitude viewpoint with mountain vistas.',
        'Mall Road walk': 'Colonial-era shopping and promenade.',
        'Jakhoo Temple': 'Hilltop shrine with panoramic views.',
        'Kalka-Shimla railway': 'UNESCO heritage toy train journey.',
        'Camel Back Road': 'Scenic walking trail with valley views.',
        'Gun Hill views': 'Elevated viewpoint with surrounding valleys.',
        'Landour cafe stroll': 'Charming hill station village exploration.',
        'Tiger Hill sunrise': 'Premium mountain sunrise experience.',
        'Tea garden tasting': 'Local tea estate tour and sampling.',
        'Toy Train ride': 'Heritage railway journey through hills.',
        'Naini Lake boating': 'Peaceful water activity surrounded by mountains.',
        'Mall Road evening': 'Evening shopping and dining destination.',
        'Snow View Point': 'Snowy mountain panorama viewpoint.',
        'Tsuglagkhang complex': 'Tibetan Buddhist monastery and cultural center.',
        'Bhagsu waterfall': 'Scenic waterfall with local legend.',
        'Triund trek': 'Popular day trek with summit views.',
        'Coffee estate walk': 'Guided tour through working coffee plantations.',
        'Abbey Falls': 'Picturesque waterfall in plantation setting.',
        'Raja Seat sunset': 'Historic seat with panoramic views.',
        'MG Marg evening': 'Modern shopping promenade in mountain city.',
        'Rumtek monastery': 'Buddhist monastery with ornate architecture.',
        'Tsomgo Lake trip': 'High-altitude glacial lake near mountain peak.',
        'Dal Lake shikara': 'Traditional boat ride on scenic lake.',
        'Mughal Gardens': 'Historic gardens with architectural heritage.',
        'Old City shopping': 'Historic market lanes with local crafts.',
        'Amber Fort morning': 'Grand hilltop palace with historical exhibits.',
        'City Palace visit': 'Royal residence with cultural displays.',
        'Bapu Bazaar shopping': 'Local market for textiles and handicrafts.',
        'Old Delhi heritage walk': 'Historic lanes with Mughal architecture.',
        'Humayun Tomb': 'Stunning monument with lush gardens.',
        'India Gate evening': 'Iconic monument popular for evening walks.',
        'Gateway heritage walk': 'Colonial architecture and waterfront views.',
        'Marine Drive sunset': 'Seaside promenade offering coastal vistas.',
        'Street food trail': 'Culinary exploration of local specialties.',
        'Cubbon Park': 'Lush urban garden perfect for walks.',
        'Bangalore Palace': 'Victorian mansion with heritage charm.',
        'Church Street': 'Shopping and dining district.',
        'Victoria Memorial': 'Grand colonial monument and museum.',
        'College Street café': 'Intellectual hub with historic cafes.',
        'Hooghly riverside': 'Waterfront area with historical significance.',
        'Marina sunrise': 'Early morning beach walk experience.',
        'Kapaleeshwarar Temple': 'Ancient temple with intricate architecture.',
        'Filter coffee trail': 'Traditional South Indian coffee experience.',
        'Taj Mahal sunrise': 'Premium early-morning monument viewing.',
        'Agra Fort': 'Mughal fortress with historical artifacts.',
        'Mehtab Bagh sunset': 'Riverside garden with monument views.',
        'Rajwada Palace': 'Historic royal residence and cultural site.',
        'Sarafa night market': 'Evening food market famous for street food.',
        'Chappan Dukan': 'Historic market with 56 vendors.',
        'Old City walk': 'Heritage lanes with UNESCO architecture.',
        'Sabarmati Ashram': 'Historic site of spiritual and political importance.',
        'Riverfront sunset': 'Scenic waterfront area for evening relaxation.',
        'Virupaksha Temple': 'Ancient temple with impressive architecture.',
        'Vittala complex': 'Historic monument with ornate stone work.',
        'Matanga Hill sunset': 'Viewpoint offering sunset over ancient ruins.',
        'Mysore Palace': 'Magnificent royal palace with night illumination.',
        'Devaraja Market': 'Historic flower and spice market.',
        'Chamundi Hill': 'Temple hilltop with sweeping city views.',
        'Parvati riverside walk': 'Scenic river valley trail.',
        'Chalal trail': 'Short hiking trail through forests.',
        'Café evening': 'Relaxed social scene in mountain village.',
        'River rafting': 'Water adventure activity on sacred river.',
        'Ganga aarti': 'Evening spiritual ceremony by river.',
        'Yoga session': 'Wellness practice in spiritual setting.',
        'Leh market': 'Local market with handicrafts and goods.',
        'Pangong Lake': 'High-altitude lake with stunning scenery.',
        'Nubra Valley': 'Remote desert valley with sand dunes.',
        'Radhanagar Beach': 'Award-winning pristine island beach.',
        'Scuba or snorkel': 'Underwater marine life exploration.',
        'Cellular Jail': 'Historical prison and museum site.',
        'Bee Falls': 'Scenic waterfall in forest setting.',
        'Dhoopgarh sunset': 'Elevated sunset viewpoint.',
        'Pandav Caves': 'Historical cave complex.',
        'Root bridge trek': 'Trek to unique living root bridges.',
        'Nohkalikai Falls': 'Tallest plunge waterfall in India.',
        'Mawsmai Cave': 'Underground cave exploration.',
        'Sunrise boat ride': 'Early morning spiritual river experience.',
        'Old city lanes': 'Historic alleyways with cultural richness.',
        'Dashashwamedh aarti': 'Evening spiritual fire ceremony.',
        'Har Ki Pauri aarti': 'Sacred evening ritual by sacred river.',
        'Ghat visit': 'Riverside steps for spiritual bathing.',
        'Mansa Devi Temple': 'Hillside temple with panoramic views.',
        'Mahabodhi Temple': 'Ancient Buddhist temple of world significance.',
        'Buddha statue': 'Sacred statue in meditation garden.',
        'Monasteries': 'Buddhist centers for meditation and study.',
        'Golden Temple': 'Sacred shrine of Sikhism.',
        'Langar meal': 'Community kitchen offering free meals.',
        'Jallianwala Bagh': 'Historical site of political significance.',
        'Krishna Temple': 'Sacred shrine with religious significance.',
        'Vishram Ghat aarti': 'Evening sacred ritual by holy river.',
        'Vrindavan circuit': "Pilgrimage to Krishna's legendary playground.",
        'Lake ghats': 'Sacred waterside steps for bathing.',
        'Brahma Temple': 'Rare temple dedicated to Brahma.',
        'Savitri sunset': 'Hilltop sunset experience.',
        'Jeep safari': 'Wildlife spotting expedition in forest.',
        'Corbett museum': 'Museum dedicated to natural heritage.',
        'Kosi river': 'Scenic river landscape.',
        'Early jeep safari': 'Early morning wildlife adventure.',
        'Central range': 'Primary wildlife reserve zone.',
        'Assamese evening': 'Local cultural experience and cuisine.'
    };

    return descMap[activity] || 'A memorable local experience not to be missed.';
}

function getItiinerarySegment(activityName, timeOfDay) {
    const segmentMap = {
        'MorningCalangute Beach sunset': 'Visit local market or enjoy coffee with sea breeze.',
        'MorningOld Goa heritage walk': 'Explore architectural wonders in cool morning light.',
        'MorningOm Beach viewpoint': 'Start with sunrise and coastal walk.',
        'AfternoonOld Goa heritage walk': 'Rest and enjoy lunch at heritage cafe.',
        'AfternoonCalangute Beach sunset': 'Beach activities or water sports.',
        'AfternoonKudle Beach walk': 'Scenic walks through coastal town.',
        'EveningCalangute Beach sunset': 'Watch sunset with drinks at beach shack.',
        'EveningLocal seafood dinner': 'Fresh seafood meal by the beach.',
        'EveningCliff promenade walk': 'Evening walk with sea breeze.',
        'MorningMall Road walk': 'Morning stroll in pleasant hill station weather.',
        'MorningJakhoo Temple': 'Visit temple and enjoy views.',
        'MorningTiger Hill sunrise': 'Early start for sunrise experience.',
        'AfternoonMall Road walk': 'Shopping and local cuisines.',
        'AfternoonJakhoo Temple': 'Afternoon market exploration.',
        'AfternoonRajwada Palace': 'Cultural exploration and heritage.',
        'EveningMall Road walk': 'Evening shopping and dining.',
        'EveningKalka-Shimla railway': 'Scenic train journey with sunset.',
        'EveningSarafa night market': 'Evening food market experience.'
    };

    const key = timeOfDay + activityName;
    if (segmentMap[key]) return segmentMap[key];

    const defaults = {
        'Morning': 'Start your day with local attractions and morning light photography.',
        'Afternoon': 'Explore main attractions and enjoy local lunch specialties.',
        'Evening': 'Relax with sunset views and evening local cuisine.'
    };
    return defaults[timeOfDay] || "Experience the destination's unique character.";
}

function generateMultiDayItinerary(placeName, guide) {
    const must = Array.isArray(guide.mustDo) ? guide.mustDo.slice() : [];
    const nearby = Array.isArray(guide.nearby) ? guide.nearby.slice() : [];
    const pool = must.concat(nearby).filter(Boolean);

    const parsed = String(guide.days || '').match(/(\d+)/);
    const requestedDays = parsed ? Math.max(2, Math.min(3, Number(parsed[1]))) : 2;

    const days = [];
    let idx = 0;
    for (let d = 1; d <= requestedDays; d++) {
        const places = [];
        const placesForDay = d === 1 ? 3 : 2;
        for (let i = 0; i < placesForDay; i++) {
            if (idx < pool.length) {
                places.push(pool[idx++]);
            } else {
                const famous = DESTINATION_FAMOUS_FOR[placeName] || '';
                const fallback = famous.split(/[.,;]/).filter(Boolean)[0] || `${placeName} highlights`;
                places.push(fallback);
            }
        }
        days.push({ dayNumber: d, places });
    }

    return days;
}

function generateMultiDayItineraryHtml(placeName, guide) {
    const days = generateMultiDayItinerary(placeName, guide);
    return days.map(day => {
        return `
            <article class="itinerary-day-card" style="flex:1;min-width:220px;padding:12px;border-radius:10px;background:#fff;border:1px solid rgba(0,0,0,0.04);box-shadow:0 6px 18px rgba(0,0,0,0.04);">
                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
                    <div>
                        <p class="itinerary-day-label">Day ${day.dayNumber}</p>
                        <h4 style="margin:4px 0 0;">${escapeHtml(placeName)}</h4>
                    </div>
                    <div style="font-size:12px;color:#888;">${day.places.length} stops</div>
                </div>
                <ol style="margin:0;padding-left:18px;color:#444;">
                    ${day.places.map((p) => `<li style="margin-bottom:8px;"><strong>${escapeHtml(p)}</strong><div style="font-size:13px;color:#666;margin-top:4px;">${escapeHtml(getActivityDescription(p, placeName))}</div></li>`).join('')}
                </ol>
            </article>
        `;
    }).join('');
}

function getExtendedTips(placeName, guide) {
    const base = Array.isArray(guide.tips) ? guide.tips.slice() : [];
    const famous = DESTINATION_FAMOUS_FOR[placeName] || '';
    const state = guide.state || '';
    const season = guide.season || '';

    const extras = [
        `Best time: ${season}. Plan outdoor activities in cooler morning hours.`,
        `Try local specialties: sample regional dishes and street food with care.`,
        `Transport: use registered taxis or recommended local transport for reliability.`,
        `Money: carry some cash for small vendors; most places accept cards in major spots.`,
        `Packing tip: carry a light rain jacket and comfortable walking shoes.`,
        `Safety: keep valuables secure and avoid isolated areas after dark.`,
        `Local tip: ask at your stay for quieter times to visit popular spots.`,
        famous ? `Don't miss: ${famous.split(/[.,;]/)[0]}.` : null,
        state ? `Nearby region to explore: ${state}.` : null,
        `Connectivity: check local SIM/data options if you'll need constant navigation.`
    ].filter(Boolean);

    const merged = base.concat(extras).reduce((acc, t) => {
        if (!acc.includes(t)) acc.push(t);
        return acc;
    }, []);

    if (merged.length < 6) {
        const fill = [
            'Carry sunscreen and a reusable water bottle.',
            'Start early for popular viewpoints to avoid crowds.',
            'Respect local customs, dress modestly at religious sites.'
        ];
        fill.forEach(f => { if (!merged.includes(f)) merged.push(f); });
    }

    return merged.slice(0, 10);
}


function getPlaceTheme(placeName) {
    const beachPlaces = ['Goa', 'Gokarna', 'Varkala', 'Marina Beach', 'Kovalam', 'Rameswaram', 'Andaman'];
    const mountainPlaces = ['Darjeeling', 'Manali', 'Shimla', 'Mussoorie', 'Nainital', 'Mcleodganj', 'Coorg', 'Gangtok', 'Srinagar', 'Kasol', 'Ladakh', 'Panchmarhi', 'Cherrapunji'];
    const spiritualPlaces = ['Varanasi', 'Haridwar', 'Bodh Gaya', 'Amritsar', 'Mathura', 'Pushkar'];
    const wildlifePlaces = ['Jim Corbett', 'Kaziranga'];
    const heritageCityPlaces = ['Jaipur', 'Delhi', 'Mumbai', 'Kolkata', 'Chennai', 'Bangalore', 'Ahmedabad', 'Indore', 'Mysuru', 'Agra', 'Hampi'];

    if (beachPlaces.includes(placeName)) return 'beach';
    if (mountainPlaces.includes(placeName)) return 'mountain';
    if (spiritualPlaces.includes(placeName)) return 'spiritual';
    if (wildlifePlaces.includes(placeName)) return 'wildlife';
    if (heritageCityPlaces.includes(placeName)) return 'heritage';
    return 'city';
}

function getGalleryPool(theme) {
    const pools = {
        beach: ['goa.jpg', 'gokarna.jpg', 'varkala.jpg', 'kovalam.jpg', 'marina.jpg', 'rameswaram.jpg', 'andaman.jpg'],
        mountain: ['manali.jpg', 'shimla.jpg', 'mussoorie.jpg', 'darjeeling.jpg', 'nainital.jpg', 'mcleodganj.jpg', 'coorg.jpg', 'gangtok.jpg', 'srinagar.jpg', 'kasol.jpg', 'ladakh.jpg', 'panchmarhi.jpg', 'cherrapunji.jpg'],
        spiritual: ['varanasi.jpg', 'haridwar.jpg', 'bodhgaya.jpg', 'amritsar.jpg', 'mathura.jpg', 'pushkar.jpg', 'rameswaram.jpg'],
        wildlife: ['jimcorbett.jpg', 'kaziranga.jpg', 'andaman.jpg'],
        heritage: ['jaipur.jpg', 'delhi.jpg', 'mumbai.jpg', 'kolkata.jpg', 'chennai.jpg', 'ahmedabad.jpg', 'indore.jpg', 'mysuru.jpg', 'agra.jpg', 'hampi.jpg'],
        city: ['delhi.jpg', 'mumbai.jpg', 'bangalore.jpg', 'kolkata.jpg', 'chennai.jpg', 'ahmedabad.jpg', 'indore.jpg', 'mysuru.jpg', 'agra.jpg']
    };

    return pools[theme] || pools.city;
}

function getWhyVisitBullets(placeName) {
    const theme = getPlaceTheme(placeName);
    const bulletMap = {
        mountain: [
            'Slow mornings, pine air, and long valley views',
            'Elegant heritage streets that feel easy to explore',
            'Cooler temperatures that make walking comfortable',
            'Short scenic drives and rail journeys with memorable views',
            'A calm break from busy city routines'
        ],
        beach: [
            'Open beaches with strong sunset color and sea breeze',
            'Casual food spots and relaxed evening plans',
            'Good mix of laid-back hours and active outings',
            'Easy day trips between beaches, forts, and markets',
            'Best enjoyed when the weather is mild and clear'
        ],
        spiritual: [
            'Sacred spaces that feel meaningful and calm',
            'Evening ceremonies and local rituals you can observe respectfully',
            'Compact old areas that are best explored on foot',
            'A strong sense of culture, faith, and daily tradition',
            'Useful for travelers who prefer quiet, reflective trips'
        ],
        wildlife: [
            'Forest landscapes that feel different from city travel',
            'Early safaris and guided drives with high sighting potential',
            'Great for nature photography and birdwatching',
            'A simple escape for families and outdoor travelers',
            'Best when you want a slower, nature-first itinerary'
        ],
        heritage: [
            'Historic buildings and strong city character',
            'Plenty of food, shopping, and cultural stops',
            'Good for short city breaks with many options',
            'Useful for first-time visitors and repeat travelers alike',
            'Easy to combine with nearby day trips or weekend plans'
        ],
        city: [
            'Balanced mix of attractions, food, and easy movement',
            'Good access and transport options for quick planning',
            'Plenty of choice without needing a packed schedule',
            'Easy to customize for a relaxed short trip',
            'Fits travelers who want comfort and flexibility'
        ]
    };

    return bulletMap[theme] || bulletMap.city;
}

function getPlaceSubtitle(placeName) {
    const theme = getPlaceTheme(placeName);
    const subtitleMap = {
        mountain: 'Queen of Hills',
        beach: 'Coastal escape',
        spiritual: 'Sacred journey',
        wildlife: 'Wildlife escape',
        heritage: 'Heritage city break',
        city: 'City getaway'
    };

    return subtitleMap[theme] || 'Travel destination';
}

function getPlaceTagline(placeName) {
    if (!placeName) return '';
    const key = String(placeName).trim();
    const overrides = {
        'Shimla': 'Queen of Hills',
        'Mussoorie': 'Queen of Hills',
        'Manali': 'Gateway to the Himalayas',
        'Darjeeling': 'Land of Tea Gardens',
        'Delhi': 'Capital City of India',
        'Goa': 'Coastal Beaches & Nightlife',
        'Varkala': 'Cliffs & Wellness',
        'Ladakh': 'High-altitude deserts & lakes'
    };

    if (overrides[key]) return overrides[key];

    const famous = String(getFamousFor(placeName) || '').trim();
    if (famous) {
        const shortFamous = famous.split(/[.;]/)[0].trim();
        if (shortFamous) return shortFamous;
    }

    const guide = getDestinationGuide(placeName);
    if (guide?.vibe) {
        const shortVibe = String(guide.vibe).split(/[.;]/)[0].trim();
        if (shortVibe) return shortVibe;
    }

    return getPlaceSubtitle(placeName);
}

function getImageUrl(image) {
    if (!image) return '';
    if (typeof image === 'string') return image;
    return image.regularUrl || image.fullUrl || image.smallUrl || image?.urls?.regular || image?.urls?.full || image?.urls?.small || '';
}

function buildGalleryImages(placeName, placeInfo) {
    const guide = getDestinationGuide(placeName);
    const theme = getPlaceTheme(placeName);
    const liveImages = Array.isArray(placeInfo?.images)
        ? placeInfo.images.map(getImageUrl).filter(Boolean)
        : [];

    const themeImages = getGalleryPool(theme).map(fileName => `assets/images/${fileName}`);
    const seed = [guide.image, ...themeImages].filter(Boolean);

    const combined = [...liveImages, ...seed].filter((image, index, list) => image && list.indexOf(image) === index);

    if (combined.length < 4) {
        const queries = [
            `${placeName}`,
            `${placeName} landscape`,
            `${placeName} skyline`,
            `${placeName} landmark`
        ];
        const unsplashUrls = queries.map(q => `https://loremflickr.com/1600/900/${encodeURIComponent(q)}`);
        unsplashUrls.forEach(u => {
            if (!combined.includes(u)) combined.push(u);
        });
    }

    return combined.slice(0, 5).map(img => resolveImagePath(getImageUrl(img) || img));
}

// ========== PLACE DETAIL PAGE RENDERER ==========
function renderCompactPlaceInsights(placeName, placeInfo, liveStatus) {
    const guide = getDestinationGuide(placeName);
    const famous = getFamousFor(placeName);

    const dayLabel = String(guide.days || '').replace(/\s*days?\s*/i, '-day').replace(/^-/, '');
    const famousText = String(famous).replace(/[.\s]+$/, '');
    const vibeText = String(guide.vibe || '').replace(/[.\s]+$/, '');
    const introPara = `${placeName} is a ${dayLabel} escape known for ${famousText}. It suits travelers who want an easygoing base, scenic views, and a mix of local food and landmark stops.`;
    const aboutPara = `${placeName} has a distinct character shaped by ${vibeText.toLowerCase()}. The destination pairs ${famousText.toLowerCase()} with relaxed sightseeing, local food, and scenic viewpoints, so the trip feels calm but still full of highlights. It is easy to build a short stay around walks, viewpoints, and an unhurried local pace.`;
    const galleryImages = buildGalleryImages(placeName, placeInfo);
    const mapEmbedUrl = `https://www.google.com/maps?q=${encodeURIComponent(placeName + ', India')}&z=13&output=embed`;
    const locationTitle = placeInfo?.location?.city ? `${placeInfo.location.city}, ${placeInfo.location.country || 'India'}` : guide.state;
    const weatherData = placeInfo?.weather || liveStatus?.weather;
    const rawTemp = weatherData?.temperature ?? 'N/A';
    const tempValue = (typeof rawTemp === 'number' || !isNaN(Number(rawTemp))) ? `${Math.round(Number(rawTemp))}°C` : rawTemp;
    const weatherCondition = weatherData?.condition || 'Live weather unavailable';
    const humidityValue = weatherData?.humidity ?? weatherData?.relativeHumidity ?? weatherData?.relative_humidity_2m ?? 'N/A';
    const windValue = weatherData?.windSpeed ?? weatherData?.windSpeed10m ?? weatherData?.wind_speed_10m ?? 'N/A';
    const safetyLabel = Array.isArray(placeInfo?.dangerAlerts) && placeInfo.dangerAlerts.length > 0 ? 'Check alerts' : 'Safe';
    const crowdLabel = Array.isArray(placeInfo?.crowdAlerts) && placeInfo.crowdAlerts.length > 0 ? 'Moderate / Busy' : 'Normal';

    const whyVisitBullets = getWhyVisitBullets(placeName);

    const html = `
        <div class="place-detail-page">
            <section class="place-top-card card">
                <div class="place-top-grid">
                    <div class="place-top-left">
                        <div class="place-eyebrow">${escapeHtml(guide.state.toUpperCase())} • INDIA</div>
                        <h1 class="place-title">${escapeHtml(placeName)}</h1>
                        <div class="place-subtitle">${escapeHtml(getPlaceTagline(placeName))}</div>
                        <p class="place-intro">${escapeHtml(introPara)}</p>

                        <div class="place-chips">
                            <div class="chip"><i class="fas fa-calendar-days"></i> ${escapeHtml(guide.days)}</div>
                            <div class="chip"><i class="fas fa-temperature-high"></i> ${tempValue}</div>
                            <div class="chip"><i class="fas fa-wallet"></i> ${escapeHtml(guide.budget)}</div>
                        </div>

                        <div class="place-top-actions">
                            <button class="btn btn-primary" onclick="navigateToPlanner('${escapeHtml(placeName)}')"><i class="fas fa-edit"></i> Customize Trip</button>
                        </div>
                    </div>
                    <div class="place-top-right">
                        <div class="featured-wrap">
                            <img class="featured-img" src="${galleryImages[0] || resolveImagePath(guide.image)}" alt="${escapeHtml(placeName)}" onerror="this.src='${resolveImagePath('images/placeholder.jpg')}'">
                            <div class="featured-pill"><i class="fas fa-map-marker-alt"></i> ${escapeHtml(locationTitle)}</div>
                        </div>
                    </div>
                </div>
            </section>

            <div class="place-essential-grid">
                <div class="best-time-panel place-panel">
                    <h4><i class="fas fa-sun"></i> Best time to visit</h4>
                    <p style="margin:8px 0 0;color:#56606a;">${escapeHtml(guide.season)} — ${escapeHtml('Best for pleasant weather and sightseeing.')}</p>
                </div>
                <div class="place-panel about-panel">
                    <h3><i class="fas fa-info-circle"></i> About ${escapeHtml(placeName)}</h3>
                    <p class="about-copy">${escapeHtml(aboutPara)}</p>
                    <div class="why-visit-card">
                        <h4><i class="fas fa-star"></i> Why visit ${escapeHtml(placeName)}?</h4>
                        <ul>
                            ${whyVisitBullets.map(item => `<li>${escapeHtml(item)}</li>`).join('')}
                        </ul>
                    </div>
                </div>


                <div class="place-panel map-panel">
                    <h3><i class="fas fa-map"></i> Where is ${escapeHtml(placeName)}</h3>
                    <iframe src="${mapEmbedUrl}" width="100%" height="300" style="border:none;border-radius:8px;margin-top:12px;" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
                </div>

                <div class="place-panel suggested-section">
                    <h3><i class="fas fa-star"></i> Must visit</h3>
                    <div class="chips-wrap">${guide.mustDo.map(item => `<span class="chip"><i class="fas fa-check-circle"></i> ${escapeHtml(item)}</span>`).join('')}</div>
                </div>

                <div class="place-panel">
                    <h3><i class="fas fa-compass"></i> Recommended activities</h3>
                    <ul style="margin:0;padding-left:18px;color:#555;">${guide.mustDo.slice(0, 5).map(activity => {
        const activityDesc = getActivityDescription(activity, placeName);
        return `<li style="margin-bottom:8px;"><strong>${escapeHtml(activity)}</strong><br><span style="font-size:13px;color:#777;">${activityDesc}</span></li>`;
    }).join('')}</ul>
                </div>

                <div class="place-panel">
                    <h3><i class="fas fa-location-dot"></i> Nearby destinations</h3>
                    <div class="chips-wrap">${guide.nearby.map(place => `<span class="chip nearby-chip"><i class="fas fa-map-location-dot"></i> ${escapeHtml(place)}</span>`).join('')}</div>
                </div>

                <div class="place-panel">
                    <h3><i class="fas fa-clock"></i> Suggested itinerary</h3>
                    <div class="itinerary-days" style="display:flex;gap:12px;flex-wrap:wrap;margin-top:8px;">
                        ${generateMultiDayItineraryHtml(placeName, guide)}
                    </div>
                </div>

                <div class="place-panel">
                    <h3><i class="fas fa-lightbulb"></i> Travel tips</h3>
                    <ul style="margin:0;padding-left:18px;color:#555;">${getExtendedTips(placeName, guide).map(t => `<li>${escapeHtml(t)}</li>`).join('')}</ul>
                </div>

                <div class="place-panel">
                    <h3><i class="fas fa-rupee-sign"></i> Estimated Budget</h3>
                    <p>Approx. ₹${formatRupees(parseBudgetRange(guide.budget))}</p>
                </div>

                <div class="place-panel">
                    <h3><i class="fas fa-shield"></i> Safety & Crowd</h3>
                    <p style="margin:0;color:#555;">Safety: <strong>${safetyLabel}</strong> · Crowd: <strong>${crowdLabel}</strong></p>
                </div>
            </div>

        </div>
    `;

    return html;
}

// ========== NAVIGATION HELPERS ==========

function getPlaceNameFromPage() {
    const params = new URLSearchParams(window.location.search);
    const placeName = params.get('place')
        || params.get('destination')
        || params.get('city')
        || params.get('name')
        || sessionStorage.getItem('smarttravelSelectedPlace')
        || localStorage.getItem('smarttravelSelectedPlace');

    return placeName ? String(placeName).trim() : null;
}

function navigateToPlanner(placeName) {
    sessionStorage.setItem('smarttravelSelectedPlace', placeName);
    localStorage.setItem('smarttravelSelectedPlace', placeName);

    const currentTrip = getStoredTripPayload();
    const plannerRequest = {
        ...(currentTrip?.plannerRequest || {})
    };
    if (placeName) {
        plannerRequest.city = placeName;
        delete plannerRequest.region;
    }

    const plannerSeed = {
        destination: placeName,
        plannerRequest
    };

    sessionStorage.setItem('smarttravelPlannerSeed', JSON.stringify(plannerSeed));
    localStorage.setItem('smarttravelPlannerSeed', JSON.stringify(plannerSeed));
    window.location.href = 'planner.html?customize=1';
}

function openMaps(placeName) {
    window.open(`https://www.google.com/maps/search/${encodeURIComponent(placeName + ' India')}`, '_blank');
}

function buildPlaceTripDraft(placeName) {
    const guide = getDestinationGuide(placeName);
    const parsedDays = Number(String(guide.days || '').match(/(\d+)/)?.[1] || 2);
    return buildFallbackItinerary({
        city: placeName,
        placeName,
        destinationCity: placeName,
        region: guide.state,
        days: parsedDays,
        budgetLevel: 'midrange',
        travellerType: 'solo',
        category: 'city',
        mood: 'explorative',
        season: guide.season
    });
}

async function addToMyTrip(buttonEl, placeName) {
    let actualPlaceName = placeName;
    let actualButtonEl = buttonEl;
    if (typeof buttonEl === 'string') {
        actualPlaceName = buttonEl;
        actualButtonEl = null;
    }

    if (actualButtonEl && actualButtonEl.disabled) return;

    let originalHtml = '';
    if (actualButtonEl) {
        actualButtonEl.disabled = true;
        originalHtml = actualButtonEl.innerHTML;
        actualButtonEl.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Saving...`;
    }

    try {
        const currentTrip = getStoredTripPayload();
        const payload = currentTrip?.plannerResponse ? currentTrip : buildPlaceTripDraft(actualPlaceName);
        const requestBody = currentTrip?.plannerRequest || payload?.plannerRequest || {
            city: actualPlaceName,
            days: 2,
            region: getDestinationGuide(actualPlaceName).state,
            travellerType: 'solo'
        };
        const responseBody = currentTrip?.plannerResponse || payload?.plannerResponse || payload;
        const savedTrip = await saveGeneratedTripToBackend(requestBody, responseBody);

        if (actualButtonEl) {
            actualButtonEl.innerHTML = `<i class="fas fa-check-circle"></i> Saved`;
            actualButtonEl.style.background = '#2e7d32';
            actualButtonEl.style.borderColor = '#2e7d32';
            actualButtonEl.style.color = '#fff';
            actualButtonEl.disabled = true;
        }

        showSaveSuccessModal(savedTrip.tripName || savedTrip.destination || actualPlaceName, savedTrip.id);
    } catch (err) {
        console.error('Failed to add to trip:', err);
        alert(err.message || 'Unable to save to your trips right now.');
        if (actualButtonEl) {
            actualButtonEl.disabled = false;
            actualButtonEl.innerHTML = originalHtml;
        }
    }
}

function showSaveSuccessModal(tripName, tripId) {
    const existing = document.getElementById('smarttravel-success-modal');
    if (existing) existing.remove();

    const modal = document.createElement('div');
    modal.id = 'smarttravel-success-modal';
    modal.className = 'st-modal-overlay';

    const isPages = window.location.pathname.includes('/pages/');
    const itineraryUrl = isPages ? 'itinerary.html' : 'pages/itinerary.html';

    modal.innerHTML = `
        <div class="st-modal-card">
            <div class="st-modal-header-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <h2>Trip Saved!</h2>
            <p>Your itinerary for <strong>${escapeHtml(tripName)}</strong> has been successfully saved to your trips.</p>
            <div class="st-modal-actions">
                <a href="${itineraryUrl}" class="btn btn-primary st-modal-btn"><i class="fas fa-list-ul"></i> Go to My Trips</a>
                <button class="btn btn-secondary st-modal-btn" onclick="closeSaveSuccessModal()"><i class="fas fa-times"></i> Close</button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);

    window.closeSaveSuccessModal = () => {
        modal.classList.add('st-modal-closing');
        setTimeout(() => modal.remove(), 250);
    };
}

function showMyTripModal() {
    window.location.href = 'itinerary.html';
}

function closeMyTripModal() {
    const modal = document.getElementById('smarttravel-mytrip-modal');
    if (modal) modal.style.display = 'none';
}

function removeFromMyTrip() {
    showToast('Saved trips are managed from the My Trip page.');
}

function exportMyTrip() {
    window.location.href = 'itinerary.html';
}

function getStoredTripPayload() {
    const raw = sessionStorage.getItem('itineraryData') || localStorage.getItem('itineraryData');
    if (!raw) return null;
    try {
        const data = JSON.parse(raw);
        if (!data || Object.keys(data).length === 0) return null;
        return {
            plannerRequest: JSON.parse(sessionStorage.getItem('plannerRequestData') || localStorage.getItem('plannerRequestData') || 'null'),
            plannerResponse: data
        };
    } catch {
        return null;
    }
}

function showToast(text, timeout = 2500) {
    let t = document.getElementById('smarttravel-toast');
    if (!t) {
        t = document.createElement('div');
        t.id = 'smarttravel-toast';
        t.className = 'smarttravel-toast';
        document.body.appendChild(t);
    }
    t.textContent = text;
    t.style.opacity = '1';
    setTimeout(() => { t.style.opacity = '0'; }, timeout);
}

function resolveImagePath(path) {
    if (!path) path = 'assets/images/placeholder.jpg';

    let basePrefix = '';
    if (window.location.pathname.includes('/pages/')) {
        basePrefix = '../';
    }

    let cleanPath = path;
    if (/^https?:\/\//i.test(path)) {
        return path;
    }

    if (path.startsWith('images/')) {
        cleanPath = 'assets/images/' + path.substring(7);
    } else if (path.startsWith('../assets/images/')) {
        cleanPath = path.substring(3);
    } else if (path.startsWith('assets/images/')) {
        cleanPath = path;
    } else if (!path.includes('/')) {
        cleanPath = 'assets/images/' + path;
    }

    if (cleanPath && cleanPath.toLowerCase().includes('placeholder')) {
        cleanPath = 'assets/images/newbackg.jpg';
    }

    return basePrefix + cleanPath;
}
