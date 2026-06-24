function renderLegacyItineraryShell(data) {
    const contentShell = document.getElementById('itinerary-content');
    if (!contentShell) return;

    const summaryGrid = document.getElementById('itinerary-summary');
    const daysGrid = document.getElementById('itinerary-days');
    const aiShell = document.getElementById('itinerary-ai');
    const cityLabel = document.getElementById('itineraryCityLabel');
    const daysLabel = document.getElementById('itineraryDaysLabel');
    const placesLabel = document.getElementById('itineraryPlacesLabel');
    const sourceLabel = document.getElementById('itinerarySourceLabel');
    const title = document.getElementById('itineraryTitle');
    const subtitle = document.getElementById('itinerarySubtitle');
    const badge = document.getElementById('itineraryBadge');

    const currentUser = JSON.parse(localStorage.getItem('smarttravelCurrentUser') || 'null');
    const displayName = currentUser?.name ? currentUser.name : 'Traveler';
    const tripCity = getTripCity(data) || data.city || data.region || 'Destination';
    const tripDays = data.generatedDays || data.days || (Array.isArray(data.itinerary) ? data.itinerary.length : '—');
    const totalPlaces = data.totalPlaces || 0;
    const sourceText = data.dataSource === 'AI_GENERATED'
        ? 'AI Generated'
        : data.dataSource === 'HYBRID'
            ? 'AI Enhanced'
            : data.dataSource === 'DATABASE'
                ? 'Database'
                : 'Saved Plan';

    if (title) title.textContent = `${displayName}'s ${tripCity} Itinerary`;
    if (subtitle) subtitle.textContent = data.summary || 'A clean, day-by-day trip plan with practical travel details.';
    if (badge) badge.textContent = sourceText;
    if (cityLabel) cityLabel.textContent = tripCity;
    if (daysLabel) daysLabel.textContent = String(tripDays);
    if (placesLabel) placesLabel.textContent = String(totalPlaces);
    if (sourceLabel) sourceLabel.textContent = sourceText;

    if (summaryGrid) {
        const totalBudget = data.totalBudget || data.budget || null;
        summaryGrid.innerHTML = `
            <div class="itinerary-summary-card">
                <i class="fas fa-calendar-days"></i>
                <div>
                    <span>Trip length</span>
                    <strong>${tripDays}</strong>
                </div>
            </div>
            <div class="itinerary-summary-card">
                <i class="fas fa-map-location-dot"></i>
                <div>
                    <span>Places</span>
                    <strong>${totalPlaces}</strong>
                </div>
            </div>
            <div class="itinerary-summary-card">
                <i class="fas fa-wallet"></i>
                <div>
                    <span>Budget</span>
                    <strong>${totalBudget ? `₹${Number(totalBudget).toLocaleString('en-IN')}` : '—'}</strong>
                </div>
            </div>
            <div class="itinerary-summary-card">
                <i class="fas fa-sparkles"></i>
                <div>
                    <span>Mode</span>
                    <strong>${sourceText}</strong>
                </div>
            </div>
        `;
    }

    if (daysGrid) {
        if (data.itinerary && Array.isArray(data.itinerary) && data.itinerary.length > 0) {
            daysGrid.innerHTML = data.itinerary.map(day => {
                const dayPlaces = Array.isArray(day.places) ? day.places : [];
                return `
                    <article class="itinerary-day-card">
                        <div class="itinerary-day-head">
                            <div>
                                <p class="itinerary-day-label">Day ${day.dayNumber || '—'}</p>
                                <h3>${day.location?.city || tripCity}</h3>
                            </div>
                            ${day.daySummary ? `<p class="itinerary-day-summary">${day.daySummary}</p>` : ''}
                        </div>
                        <div class="itinerary-place-list">
                            ${dayPlaces.map((place, idx) => `
                                <div class="itinerary-place-item">
                                    <div class="itinerary-place-top">
                                        <strong>${idx + 1}. ${place.placeName || 'Place'}</strong>
                                        <span>${place.plannedVisitTimeSlot || 'Flexible time'}</span>
                                    </div>
                                    <p>${place.description || 'No description available'}</p>
                                    <div class="itinerary-place-tags">
                                        ${place.recommendedDurationHours ? `<span><i class="fas fa-hourglass-half"></i> ${place.recommendedDurationHours}h</span>` : ''}
                                        ${place.localTips ? `<span><i class="fas fa-lightbulb"></i> ${place.localTips}</span>` : ''}
                                        ${place.safetyAdvice ? `<span><i class="fas fa-triangle-exclamation"></i> ${place.safetyAdvice}</span>` : ''}
                                    </div>
                                </div>
                            `).join('')}
                        </div>
                        ${day.travelNotes ? `<p class="itinerary-day-notes"><strong>Travel notes:</strong> ${day.travelNotes}</p>` : ''}
                    </article>
                `;
            }).join('');
        } else {
            daysGrid.innerHTML = `
                <div class="itinerary-empty card">
                    <h3>No itinerary data found</h3>
                    <p>Generate a new plan from the planner to see the final itinerary here.</p>
                </div>
            `;
        }
    }

    if (aiShell) {
        aiShell.innerHTML = renderAISection(data);
    }
}

function togglePlaceForm() {
    const value = document.querySelector('input[name="placeKnown"]:checked')?.value;
    if (!value) return;

    placeKnown = value === 'yes';

    if (placeKnown) {
        document.getElementById('branchPlaceKnown').style.display = 'block';
        document.getElementById('branchPlaceUnknown').style.display = 'none';
    } else {
        document.getElementById('branchPlaceKnown').style.display = 'none';
        document.getElementById('branchPlaceUnknown').style.display = 'block';
    }
}

// STEP 3A: Activity tiles (place known)
function initActivityTiles() {
    document.querySelectorAll('.activity-tile').forEach(tile => {
        tile.addEventListener('click', (e) => {
            e.preventDefault();
            const activity = tile.dataset.activity;

            if (selectedActivities[activity]) {
                delete selectedActivities[activity];
                tile.classList.remove('selected');
            } else {
                selectedActivities[activity] = true;
                tile.classList.add('selected');
            }

            updateSelectedActivities();
        });
    });
}

function updateSelectedActivities() {
    const container = document.getElementById('selectedActivities');
    container.innerHTML = '';
    Object.keys(selectedActivities).forEach(act => {
        const chip = document.createElement('span');
        chip.className = 'chip';
        chip.innerHTML = `${act} <span class="remove" data-activity="${act}">✕</span>`;
        container.appendChild(chip);
        chip.querySelector('.remove').addEventListener('click', () => {
            delete selectedActivities[act];
            document.querySelector(`.activity-tile[data-activity="${act}"]`).classList.remove('selected');
            updateSelectedActivities();
        });
    });
}

// STEP 3B: Interest tiles & region cards (place unknown)
function initInterestTiles() {
    document.querySelectorAll('.interest-tile').forEach(tile => {
        tile.addEventListener('click', (e) => {
            e.preventDefault();
            const interest = tile.dataset.interest;

            if (selectedInterests[interest]) {
                delete selectedInterests[interest];
                tile.classList.remove('selected');
            } else {
                selectedInterests[interest] = true;
                tile.classList.add('selected');
            }

            updateSelectedInterests();
        });
    });
}

function updateSelectedInterests() {
    const container = document.getElementById('selectedInterests');
    container.innerHTML = '';
    Object.keys(selectedInterests).forEach(int => {
        const chip = document.createElement('span');
        chip.className = 'chip';
        chip.innerHTML = `${int} <span class="remove" data-interest="${int}">✕</span>`;
        container.appendChild(chip);
        chip.querySelector('.remove').addEventListener('click', () => {
            delete selectedInterests[int];
            document.querySelector(`.interest-tile[data-interest="${int}"]`).classList.remove('selected');
            updateSelectedInterests();
        });
    });
}

function initRegionCards() {
    document.querySelectorAll('.region-card').forEach(card => {
        card.addEventListener('click', (e) => {
            e.preventDefault();
            const region = card.dataset.region;

            document.querySelectorAll('.region-card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
            selectedRegion = region;
            // if region selected, reveal suggested interests
            const interestSection = document.getElementById('interest-section');
            if (interestSection) interestSection.classList.remove('hidden');
        });
    });
}

// Main planner submit handler
function generatePlan() {
    // Validate required fields
    if (!selectedTravellerType) {
        alert('Please select a traveler type');
        return;
    }

    if (placeKnown === null) {
        alert('Please select if you know the place');
        return;
    }

    let requestBody = {
        travellerType: selectedTravellerType,
        enhanceWithAi: document.getElementById('enhanceWithAi')?.checked || false,
        preferences: document.getElementById('preferences')?.value || ''
    };

    if (placeKnown) {
        // Branch A: Place known
        const city = document.getElementById('city')?.value.trim();
        const days = document.getElementById('days')?.value;
        const budgetLevel = document.getElementById('budgetLevel')?.value;

        if (!city || !days || !budgetLevel) {
            alert('Please fill in City, Duration, and Budget');
            return;
        }

        requestBody.city = city;
        requestBody.days = parseInt(days);
        requestBody.budgetLevel = budgetLevel;
        requestBody.maxHoursPerDay = parseFloat(document.getElementById('maxHoursPerDay')?.value) || null;

        // Add selected activities as category/mood
        const activities = Object.keys(selectedActivities);
        if (activities.length > 0) {
            requestBody.category = activities[0]; // First activity as category
            requestBody.mood = activities.slice(1).join(','); // Rest as mood
        }
    } else {
        // Branch B: Place unknown
        const days = document.getElementById('days_unknown')?.value;
        const budgetLevel = document.getElementById('budgetLevel_unknown')?.value;
        const season = document.getElementById('season_unknown')?.value;

        if (!days || !budgetLevel || !selectedRegion) {
            alert('Please fill in Duration, Budget, and select a Region');
            return;
        }

        requestBody.days = parseInt(days);
        requestBody.budgetLevel = budgetLevel;
        requestBody.region = selectedRegion;
        requestBody.season = season || '';

        // Add selected interests as category/mood
        const interests = Object.keys(selectedInterests);
        if (interests.length > 0) {
            requestBody.category = interests[0]; // First interest as category
            requestBody.mood = interests.slice(1).join(','); // Rest as mood
        }
    }

    console.log('Generated payload:', requestBody);

    alert('Generating your itinerary...');
    fetchItinerary(requestBody);
}


function showPlannerToast(message) {
    const existing = document.getElementById('planner-toast-message');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'planner-toast-message';
    toast.className = 'smarttravel-toast';
    toast.textContent = message;
    document.body.appendChild(toast);

    requestAnimationFrame(() => {
        toast.style.opacity = '1';
    });

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 250);
    }, 2600);
}

function normalizeTripDestinationValue(value) {
    return String(value || '').trim();
}

function getKnownTripDestinations() {
    return typeof DESTINATION_GUIDES === 'object' && DESTINATION_GUIDES
        ? Object.keys(DESTINATION_GUIDES)
        : [];
}

function detectReferencedDestinations(value) {
    const text = normalizeTripDestinationValue(value).toLowerCase();
    if (!text) return [];

    return getKnownTripDestinations().filter(destination => text.includes(destination.toLowerCase()));
}

function hasConflictingDestinationReference(value, canonicalDestination) {
    const canonical = normalizeTripDestinationValue(canonicalDestination).toLowerCase();
    return detectReferencedDestinations(value).some(destination => destination.toLowerCase() !== canonical);
}

function getPrimaryReferencedDestination(value, fallbackDestination) {
    const references = detectReferencedDestinations(value);
    if (!references.length) {
        return normalizeTripDestinationValue(fallbackDestination) || '';
    }

    const fallback = normalizeTripDestinationValue(fallbackDestination).toLowerCase();
    return references.find(destination => destination.toLowerCase() === fallback) || references[0];
}

function sanitizeSavedTripPlace(place, canonicalDestination) {
    if (!place || typeof place !== 'object') {
        return null;
    }

    const textFields = [
        place.placeName,
        place.description,
        place.significance,
        place.localTips,
        place.safetyAdvice,
        place.city,
        place.location?.city
    ];

    if (textFields.some(field => hasConflictingDestinationReference(field, canonicalDestination))) {
        return null;
    }

    const sanitizedPlace = { ...place };

    if (sanitizedPlace.city) {
        sanitizedPlace.city = canonicalDestination;
    }

    if (sanitizedPlace.location && typeof sanitizedPlace.location === 'object') {
        sanitizedPlace.location = {
            ...sanitizedPlace.location,
            city: canonicalDestination
        };
    }

    return sanitizedPlace;
}

function sanitizeSavedTripItinerary(itinerary, canonicalDestination) {
    if (!Array.isArray(itinerary)) {
        return [];
    }

    return itinerary.map((day, index) => {
        const originalPlaces = Array.isArray(day?.places) ? day.places : [];
        const places = originalPlaces
            .map(place => sanitizeSavedTripPlace(place, canonicalDestination))
            .filter(Boolean);

        return {
            ...day,
            dayNumber: day?.dayNumber || index + 1,
            location: {
                ...(day?.location || {}),
                city: canonicalDestination
            },
            daySummary: hasConflictingDestinationReference(day?.daySummary, canonicalDestination)
                ? `Explore more of ${canonicalDestination}.`
                : day?.daySummary,
            travelNotes: hasConflictingDestinationReference(day?.travelNotes, canonicalDestination)
                ? ''
                : day?.travelNotes,
            places
        };
    });
}

function sanitizeDestinationRecommendations(recommendations, canonicalDestination) {
    if (!Array.isArray(recommendations)) {
        return [];
    }

    return recommendations.filter(recommendation => {
        const recommendationText = [
            recommendation?.placeName,
            recommendation?.category,
            recommendation?.reason
        ].filter(Boolean).join(' ');

        return !hasConflictingDestinationReference(recommendationText, canonicalDestination);
    });
}

function getItineraryDestinations(itinerary, fallbackDestination) {
    const destinations = new Set();
    const fallback = normalizeTripDestinationValue(fallbackDestination);
    if (fallback) {
        destinations.add(fallback);
    }

    if (!Array.isArray(itinerary)) {
        return Array.from(destinations);
    }

    itinerary.forEach(day => {
        const dayCity = normalizeTripDestinationValue(day?.location?.city);
        if (dayCity) {
            destinations.add(dayCity);
        }

        if (Array.isArray(day?.places)) {
            day.places.forEach(place => {
                const placeCity = normalizeTripDestinationValue(place?.city || place?.location?.city);
                if (placeCity) {
                    destinations.add(placeCity);
                }

                detectReferencedDestinations([
                    place?.placeName,
                    place?.description,
                    place?.significance
                ].filter(Boolean).join(' ')).forEach(destination => destinations.add(destination));
            });
        }

        detectReferencedDestinations([
            day?.daySummary,
            day?.travelNotes
        ].filter(Boolean).join(' ')).forEach(destination => destinations.add(destination));
    });

    return Array.from(destinations);
}

function buildTripDestinationContext(data) {
    const selectedDestination = normalizeTripDestinationValue(
        data?.selectedDestination
        || data?.plannerRequest?.city
        || data?.city
        || data?.destination
        || getTripCity(data)
    );
    const savedDestination = normalizeTripDestinationValue(
        data?.savedDestination
        || data?.destination
        || data?.plannerRequest?.city
    );
    const loadedDestination = normalizeTripDestinationValue(
        data?.city
        || data?.destination
        || data?.plannerRequest?.city
        || data?.plannerRequest?.region
        || getTripCity(data)
    );
    const canonicalDestination = savedDestination || selectedDestination || loadedDestination || 'Destination';
    const itineraryDestinations = getItineraryDestinations(data?.itinerary, canonicalDestination);
    const aboutText = normalizeTripDestinationValue(data?.aboutPlace);
    const aboutDestination = aboutText && !hasConflictingDestinationReference(aboutText, canonicalDestination)
        ? getPrimaryReferencedDestination(aboutText, canonicalDestination)
        : canonicalDestination;

    const sanitizedPlannerRequest = {
        ...(data?.plannerRequest || {})
    };
    if (canonicalDestination) {
        sanitizedPlannerRequest.city = canonicalDestination;
    }

    const sanitizedData = {
        ...data,
        city: canonicalDestination,
        destination: savedDestination || canonicalDestination,
        savedDestination: savedDestination || canonicalDestination,
        canonicalDestination,
        plannerRequest: sanitizedPlannerRequest,
        aboutPlace: aboutText && !hasConflictingDestinationReference(aboutText, canonicalDestination)
            ? aboutText
            : '',
        whyChoosePlace: Array.isArray(data?.whyChoosePlace)
            ? data.whyChoosePlace.filter(item => !hasConflictingDestinationReference(item, canonicalDestination))
            : [],
        additionalRecommendations: sanitizeDestinationRecommendations(data?.additionalRecommendations, canonicalDestination),
        itinerary: sanitizeSavedTripItinerary(data?.itinerary, canonicalDestination)
    };

    return {
        selectedDestination: selectedDestination || canonicalDestination,
        savedDestination: savedDestination || canonicalDestination,
        loadedDestination: loadedDestination || canonicalDestination,
        canonicalDestination,
        aboutDestination,
        itineraryDestinations,
        sanitizedData
    };
}

function logTripDestinationDebug(context, budgetDestination) {
    console.groupCollapsed(`[Trip Debug] ${context.canonicalDestination}`);
    console.log('Selected Destination', context.selectedDestination);
    console.log('Saved Trip Destination', context.savedDestination);
    console.log('Loaded Destination', context.loadedDestination);
    console.log('About Destination', context.aboutDestination);
    console.log('Budget Destination', budgetDestination || context.canonicalDestination);
    console.log('Itinerary Destinations', context.itineraryDestinations);
    console.groupEnd();
}

// Render itinerary on the itinerary page
function renderItinerary(data) {
    const container = document.getElementById('itinerary-output');
    if (!container) return;

    if (data && !data.plannerRequest) {
        const storedPayload = getStoredTripPayload();
        data.plannerRequest = storedPayload?.plannerRequest || JSON.parse(localStorage.getItem('plannerRequestData') || sessionStorage.getItem('plannerRequestData') || 'null') || {};
    }

    const destinationContext = buildTripDestinationContext(data || {});
    data = destinationContext.sanitizedData;

    const params = new URLSearchParams(window.location.search);
    const hasTripId = params.has('tripId') || data.savedTripId;

    const storedPayload = getStoredTripPayload();
    const plannerRequest = data.plannerRequest || storedPayload?.plannerRequest || JSON.parse(localStorage.getItem('plannerRequestData') || sessionStorage.getItem('plannerRequestData') || 'null') || {};
    const tripCity = destinationContext.canonicalDestination;
    const tripName = data.tripName || `${tripCity} Trip`;
    const tripDays = data.generatedDays || data.requestedDays || plannerRequest.days || data.days || (Array.isArray(data.itinerary) ? data.itinerary.length : '—');
    const totalPlaces = data.totalPlaces || 0;
    const totalBudget = data.totalBudget || data.budget || null;
    const sourceText = data.dataSource === 'AI_GENERATED'
        ? 'AI Generated'
        : data.dataSource === 'HYBRID'
            ? 'AI Enhanced'
            : data.dataSource === 'DATABASE'
                ? 'Database'
                : 'Saved Plan';
    const summaryText = data.summary || 'A clean, day-by-day trip plan with practical travel details.';

    // Sourced directly from AI response fields if available, otherwise fallback to local guides
    const overview = (data.aboutPlace && data.aboutPlace.trim() !== '')
        ? data.aboutPlace
        : (getPlaceAboutParagraph(tripCity) || getPlaceIntroSummary(tripCity) || summaryText);

    const heroGuide = getDestinationGuide(tripCity) || {};
    const heroImage = resolveImagePath(heroGuide.image || 'assets/images/varkala.jpg');
    const budgetValue = Number(totalBudget || 0);
    const galleryImages = (typeof buildGalleryImages === 'function') ? buildGalleryImages(tripCity, {}) : [resolveImagePath(heroGuide.image || 'assets/images/placeholder.jpg')];

    const whyVisitBullets = (typeof getWhyVisitBullets === 'function') ? getWhyVisitBullets(tripCity) : [];
    const whyVisitList = (Array.isArray(data.whyChoosePlace) && data.whyChoosePlace.length > 0)
        ? data.whyChoosePlace
        : whyVisitBullets;

    const preferenceLabels = {
        budget: 'Budget (₹1k-2k/day)',
        midrange: 'Comfort (₹2k-3.5k/day)',
        premium: 'Premium (₹4k-8k/day)',
        luxury: 'Luxury (₹10k+/day)',
        solo: 'Solo Traveler',
        couple: 'Couple',
        family: 'Family Trip',
        friends: 'Friend Group',
        group: 'Group Tour',
        beach: 'Beach Vibe',
        mountains: 'Mountain Escape',
        city: 'Urban Explorer',
        adventure: 'Adventure Seeker',
        spiritual: 'Spiritual Journey',
        wildlife: 'Wildlife Safari',
        food: 'Culinary Trail',
        cultural: 'Cultural Immersion',
        relaxed: 'Relaxed Pace',
        explorative: 'Exploratory Vibe',
        romantic: 'Romantic getaway',
        offbeat: 'Offbeat/Unique',
        social: 'Social Vibe',
        hiddenGems: 'Hidden Gems',
        avoidCrowds: 'Skip Crowds',
        localExp: 'Local Experience',
        instaWorthy: 'Insta-Worthy',
        decideAll: 'Full AI Customization',
        summer: 'Summer Bliss',
        monsoon: 'Monsoon Magic',
        winter: 'Winter Charm',
        diwali: 'Diwali Celebration',
        holi: 'Holi Festival',
        navratri: 'Navratri divine',
        Christmas: 'Christmas Holiday',
        pongal: 'Pongal Harvest'
    };

    const getPreferenceLabel = (val) => {
        if (!val) return '';
        const lower = String(val).trim().toLowerCase();
        for (const key in preferenceLabels) {
            if (key.toLowerCase() === lower) {
                return preferenceLabels[key];
            }
        }
        return val.charAt(0).toUpperCase() + val.slice(1);
    };

    const topChips = [
        plannerRequest.travellerType ? `Traveler: ${getPreferenceLabel(plannerRequest.travellerType)}` : '',
        plannerRequest.category ? `Style: ${getPreferenceLabel(plannerRequest.category)}` : '',
        plannerRequest.mood ? `Mood: ${plannerRequest.mood.split(',').map(getPreferenceLabel).join(', ')}` : '',
        plannerRequest.season ? `Season: ${getPreferenceLabel(plannerRequest.season)}` : '',
        plannerRequest.festival ? `Festival: ${getPreferenceLabel(plannerRequest.festival)}` : '',
        plannerRequest.preferences ? `Preferences: ${plannerRequest.preferences.split(',').map(getPreferenceLabel).join(', ')}` : '',
        plannerRequest.region ? `Region: ${getPreferenceLabel(plannerRequest.region)}` : ''
    ].filter(Boolean);

    // Determine number of travelers from plannerRequest (mirrors computeBudgetBreakdown logic)
    let displayTravelerCount = null;
    if (plannerRequest.groupSize && Number(plannerRequest.groupSize) > 0) {
        displayTravelerCount = Number(plannerRequest.groupSize);
    } else if (plannerRequest.travelers && Number(plannerRequest.travelers) > 0) {
        displayTravelerCount = Number(plannerRequest.travelers);
    } else {
        const tType = String(plannerRequest.travellerType || data.travellerType || '').toLowerCase().trim();
        if (tType === 'solo') displayTravelerCount = 1;
        else if (tType === 'couple' || tType === 'romantic') displayTravelerCount = 2;
        else if (tType === 'family') displayTravelerCount = 4;
        else if (tType === 'friends' || tType === 'group') displayTravelerCount = 5;
        else if (tType) displayTravelerCount = 1;
    }

    const choiceRows = [
        ['Traveler', getPreferenceLabel(plannerRequest.travellerType || data.travellerType)],
        ['No. of Travelers', displayTravelerCount ? `${displayTravelerCount} person${displayTravelerCount > 1 ? 's' : ''}` : null],
        ['Destination', tripCity],
        ['Duration', tripDays ? `${tripDays} days` : null],
        ['Budget Level', getPreferenceLabel(plannerRequest.budgetLevel)],
        ['Travel Style', [plannerRequest.category, plannerRequest.mood].filter(Boolean).map(getPreferenceLabel).join(', ')],
        ['Season / Festival', getPreferenceLabel(plannerRequest.festival || plannerRequest.season)],
        ['AI Settings', plannerRequest.preferences ? plannerRequest.preferences.split(',').map(getPreferenceLabel).join(', ') : (plannerRequest.enhanceWithAi ? 'Enabled' : null)],
        ['Pace limit', plannerRequest.maxHoursPerDay ? `${Number(plannerRequest.maxHoursPerDay).toFixed(1)} hrs/day` : null]
    ].filter(([, value]) => value !== null && value !== undefined && String(value).trim() !== '');


    // Set dynamic hero background image if available
    const heroEl = document.querySelector('.itinerary-hero');
    if (heroEl) {
        if (heroGuide && heroGuide.image) {
            const resolvedImg = resolveImagePath(heroGuide.image);
            heroEl.style.backgroundImage = `linear-gradient(rgba(0, 0, 0, 0.45), rgba(0, 0, 0, 0.75)), url('${resolvedImg}')`;
            heroEl.style.backgroundSize = 'cover';
            heroEl.style.backgroundPosition = 'center';
            heroEl.style.color = '#ffffff';
        }
    }

    const headerTitle = document.getElementById('tripDetailTitle');
    if (headerTitle) headerTitle.textContent = tripCity;
    const headerSubtitle = document.getElementById('tripDetailSubtitle');
    // Use a short tagline if available, otherwise fall back to a place-specific tagline or the guide vibe
    const tagline = data.tagline || (typeof getPlaceTagline === 'function' ? getPlaceTagline(tripCity) : (heroGuide.vibe ? String(heroGuide.vibe).split(/[.\n]/)[0] : summaryText));
    if (headerSubtitle) headerSubtitle.textContent = tagline;
    const eyebrow = document.querySelector('.itinerary-eyebrow');
    if (eyebrow) eyebrow.innerHTML = `<strong>${escapeHtml((heroGuide.state || '').toUpperCase())} • INDIA</strong>`;

    const weatherBadge = document.getElementById('itinerary-weather-badge');
    if (weatherBadge) {
        weatherBadge.innerHTML = '';
    }

    // Inject beautiful preference chips inside the Hero banner
    const heroCopy = document.querySelector('.itinerary-hero-copy');
    if (heroCopy) {
        let chipsList = document.getElementById('hero-chips-list');
        if (!chipsList) {
            chipsList = document.createElement('div');
            chipsList.id = 'hero-chips-list';
            chipsList.className = 'place-chips';
            chipsList.style.marginTop = '14px';
            heroCopy.appendChild(chipsList);
        }
        chipsList.innerHTML = `
            ${topChips.map(chip => `
                <span class="chip" style="background: rgba(255, 255, 255, 0.15); color: #ffffff; padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; display: inline-flex; align-items: center; gap: 6px; border: 1px solid rgba(255, 255, 255, 0.25);">
                    <i class="fas fa-hashtag"></i> ${escapeHtml(chip)}
                </span>
            `).join('')}
            <span class="chip" style="background: rgba(255, 255, 255, 0.15); color: #ffffff; padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; display: inline-flex; align-items: center; gap: 6px; border: 1px solid rgba(255, 255, 255, 0.25);">
                <i class="fas fa-location-dot"></i> ${escapeHtml(tripCity)}
            </span>
            <span class="chip" style="background: rgba(255, 255, 255, 0.15); color: #ffffff; padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; display: inline-flex; align-items: center; gap: 6px; border: 1px solid rgba(255, 255, 255, 0.25);">
                <i class="fas fa-calendar-days"></i> ${escapeHtml(String(tripDays))} Days
            </span>
            <span class="chip" style="background: rgba(255, 255, 255, 0.15); color: #ffffff; padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: 700; display: inline-flex; align-items: center; gap: 6px; border: 1px solid rgba(255, 255, 255, 0.25);">
                <i class="fas fa-wand-magic-sparkles"></i> ${escapeHtml(sourceText)}
            </span>
        `;
    }

    function groupPlacesBySlot(places) {
        const slots = { morning: [], afternoon: [], evening: [], other: [] };
        (places || []).forEach(p => {
            const slot = String(p.plannedVisitTimeSlot || '').toLowerCase();
            if (slot.includes('morning')) slots.morning.push(p);
            else if (slot.includes('afternoon')) slots.afternoon.push(p);
            else if (slot.includes('evening') || slot.includes('night')) slots.evening.push(p);
            else slots.other.push(p);
        });
        return slots;
    }

    function renderPlannerDay(day, idx) {
        // Show only the primary place for each day (simpler one-place-per-day view)
        const places = Array.isArray(day.places) ? day.places : [];
        const topPlace = places.length ? places[0] : null;

        return `
            <article class="itinerary-day-card trip-day-card">
                <div class="itinerary-day-head" style="margin-bottom: 12px;">
                    <div>
                        <p class="itinerary-day-label">Day ${day.dayNumber || (idx + 1)}</p>
                        <h3>${escapeHtml(String(day.location?.city || tripCity))}</h3>
                    </div>
                    <div class="trip-day-compact">
                        ${day.totalPlannedHours ? `<span>${escapeHtml(String(day.totalPlannedHours))}h planned</span>` : ''}
                        ${day.estimatedTravelHours ? `<span>${escapeHtml(String(day.estimatedTravelHours))}h travel</span>` : ''}
                    </div>
                </div>
                ${day.daySummary ? `<p class="itinerary-day-summary" style="margin-top: 8px; font-weight: 500; color: var(--primary);">${escapeHtml(String(day.daySummary))}</p>` : ''}
                ${places && places.length > 0 ? `
                    <div style="display: grid; gap: 16px; margin-top: 12px;">
                        ${places.map((place, idx) => `
                            <div class="itinerary-place-item">
                                <div class="itinerary-place-top">
                                    <strong>${idx + 1}. ${escapeHtml(String(place.placeName || 'Place'))}</strong>
                                    <span>${escapeHtml(String(place.plannedVisitTimeSlot || place.bestTimeToVisit || 'Flexible'))}</span>
                                </div>
                                <p>${escapeHtml(String(place.description || place.significance || 'No description available'))}</p>
                                <div class="itinerary-place-tags">
                                    ${place.category ? `<span><i class="fas fa-tag"></i> ${escapeHtml(String(place.category))}</span>` : ''}
                                    ${place.rating ? `<span><i class="fas fa-star"></i> ${escapeHtml(String(place.rating))}</span>` : ''}
                                    ${place.recommendedDurationHours ? `<span><i class="fas fa-hourglass-half"></i> ${escapeHtml(String(place.recommendedDurationHours))}h</span>` : ''}
                                    ${place.localTips ? `<span><i class="fas fa-lightbulb"></i> ${escapeHtml(String(place.localTips))}</span>` : ''}
                                    ${place.safetyAdvice ? `<span><i class="fas fa-triangle-exclamation"></i> ${escapeHtml(String(place.safetyAdvice))}</span>` : ''}
                                </div>
                            </div>
                        `).join('')}
                    </div>
                ` : `<p style="color:var(--text-muted);">No places planned for this day.</p>`}
                ${day.travelNotes ? `<p class="itinerary-day-notes" style="background: rgba(10, 61, 98, 0.03); padding: 10px 14px; border-radius: 10px; margin-top: 14px; border-left: 3px solid var(--primary);"><strong>Travel notes:</strong> ${escapeHtml(String(day.travelNotes))}</p>` : ''}
            </article>
        `;
    }

    const budget = typeof computeDestinationAwareBudgetBreakdown === 'function'
        ? computeDestinationAwareBudgetBreakdown(data)
        : computeBudgetBreakdown(data);
    const dayPlannerCards = Array.isArray(data.itinerary)
        ? data.itinerary.map((day, idx) => renderPlannerDay(day, idx)).join('')
        : '';

    logTripDestinationDebug(destinationContext, budget?.destination);

    // Append floating CTA to body (Customize Trip primary + Add to My Trip quick)
    setTimeout(() => {
        let c = document.getElementById('smarttravel-floating-cta');
        const cityNameEscaped = escapeHtml(String(tripCity));
        const floatingBtnHtml = hasTripId ?
            `<button id="saveItineraryFloatingBtn" class="btn btn-secondary" style="background:#2e7d32; border-color:#2e7d32; color:#fff;" disabled><i class="fas fa-check-circle"></i> Saved</button>` :
            `<button id="saveItineraryFloatingBtn" class="btn btn-secondary" onclick="addToMyTrip(this, '${cityNameEscaped}')"><i class="fas fa-heart"></i> Add to My Trip</button>`;

        if (!c) {
            c = document.createElement('div');
            c.id = 'smarttravel-floating-cta';
            c.className = 'floating-cta';
            c.innerHTML = `
                <button class="btn btn-primary" onclick="navigateToPlanner('${cityNameEscaped}')"><i class="fas fa-edit"></i> Customize Trip</button>
                ${floatingBtnHtml}
            `;
            document.body.appendChild(c);
        } else {
            c.innerHTML = `
                <button class="btn btn-primary" onclick="navigateToPlanner('${cityNameEscaped}')"><i class="fas fa-edit"></i> Customize Trip</button>
                ${floatingBtnHtml}
            `;
            c.style.display = 'flex';
        }
    }, 50);

    const mapEmbedUrl = `https://www.google.com/maps?q=${encodeURIComponent(tripCity + ', India')}&z=12&output=embed`;
    const guide = heroGuide;

    // Build the grid layout output (single main image inside the grid)
    container.innerHTML = `
        <div class="place-essential-grid trip-detail-grid">
            
            <!-- Row 1: About the Place (AI Generated Description) -->
            <section class="place-panel about-panel trip-detail-panel-wide">
                <h3 style="font-size: 22px; font-weight: 800;"><i class="fas fa-info-circle"></i> About ${escapeHtml(tripCity)}</h3>
                <div style="display:grid;grid-template-columns:1fr 340px;gap:24px;align-items:start;margin-top:14px;">
                    <div>
                        <p class="about-copy" style="line-height:1.8; font-size:15px; color:#4a545e; margin-bottom: 20px;">${escapeHtml(overview)}</p>
                        <div class="why-visit-card" style="margin-top:16px;">
                            <h4 style="font-size: 16px; font-weight: 800; color: var(--primary);"><i class="fas fa-star"></i> Why Choose This Destination</h4>
                            <ul style="margin-top: 10px; display:flex; flex-direction:column; gap:6px;">
                                ${whyVisitList.map(item => `<li>${escapeHtml(item)}</li>`).join('')}
                            </ul>
                        </div>
                    </div>
                    <div style="display:grid;gap:10px;">
                        <img src="${escapeHtml(galleryImages[0] || heroImage)}" style="width:100%;height:320px;object-fit:cover;border-radius:12px;box-shadow: 0 8px 20px rgba(0,0,0,0.12);" alt="${escapeHtml(tripCity)}" onerror="this.src='${resolveImagePath('images/placeholder.jpg')}'">
                    </div>
                </div>
            </section>

            <!-- Row 2: Planner Choices & Specifications (Left 50%) -->
            <section class="place-panel trip-detail-panel">
                <h2><i class="fas fa-sliders"></i> Planner Choices</h2>
                <p style="font-size: 13px; color: var(--text-light); margin-bottom: 12px; line-height:1.5;">Preferences chosen in the trip builder for customized AI routing.</p>
                <div style="display: flex; flex-direction: column; gap: 8px;">
                    ${choiceRows.map(([label, value]) => `
                        <div style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid rgba(10, 61, 98, 0.05);">
                            <span style="font-size: 13px; color: var(--text-muted); font-weight: 500;">${escapeHtml(label)}</span>
                            <strong style="font-size: 13px; color: var(--primary); text-align: right;">${escapeHtml(String(value))}</strong>
                        </div>
                    `).join('')}
                    <div style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid rgba(10, 61, 98, 0.05);">
                        <span style="font-size: 13px; color: var(--text-muted); font-weight: 500;">Total places</span>
                        <strong style="font-size: 13px; color: var(--primary); text-align: right;">${escapeHtml(String(totalPlaces))} spots</strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 8px 0;">
                        <span style="font-size: 13px; color: var(--text-muted); font-weight: 500;">Total trip hours</span>
                        <strong style="font-size: 13px; color: var(--primary); text-align: right;">${escapeHtml(data.totalTripHours ? `${Number(data.totalTripHours).toFixed(1)} hrs` : '—')}</strong>
                    </div>
                </div>
            </section>

            <!-- Row 2: Budget Breakdown & Pace (Right 50%) -->
            <section class="place-panel trip-detail-panel">
                <h2><i class="fas fa-wallet"></i> Estimated Budget</h2>
                <p style="font-size: 13px; color: var(--text-light); margin-bottom: 12px; line-height: 1.5;">Estimated category allocation tailored to your spending style.</p>
                ${renderBudgetPanel(budget)}
            </section>

            ${data.dataSource === 'AI_GENERATED' ? `
            <!-- AI Generated Banner -->
            <section class="place-panel trip-detail-panel trip-detail-panel-wide" style="background: #e0f7f4; border-left: 4px solid #14B8A6; padding: 16px 20px;">
                <div style="display: flex; align-items: center; gap: 12px; color: #004d40;">
                    <i class="fas fa-wand-magic-sparkles" style="font-size: 18px; color: #14B8A6;"></i>
                    <div>
                        <strong>AI-Generated Itinerary</strong>
                        <p style="margin: 4px 0 0 0; font-size: 13px; line-height: 1.4;">This itinerary was generated by AI for <strong>${escapeHtml(tripCity)}</strong> because it's not yet in our local database. Please verify places and timings locally before traveling.</p>
                    </div>
                </div>
            </section>
            ` : ''}

            <!-- Row 3: Day-by-Day Itinerary (Full width, Detailed Planner day cards) -->
            <section class="place-panel trip-detail-panel trip-detail-panel-wide">
                <h2><i class="fas fa-calendar-days"></i> Day-by-Day Travel Itinerary</h2>
                <div class="itinerary-top-gallery" style="margin-top: 16px; margin-bottom: 20px; display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 16px;">
                    ${getUnsplashDestinationImages(tripCity, 3, getTripPlaceNames(data)).map((imgUrl, index) => `
                        <img src="${escapeHtml(imgUrl)}" alt="${escapeHtml(tripCity)} ${index + 1}" style="width: 100%; height: 260px; object-fit: cover; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1);" onerror="this.src='${resolveImagePath('images/placeholder.jpg')}'">
                    `).join('')}
                </div>
                <div class="itinerary-days-grid trip-day-list" style="display:grid; gap:20px; margin-top:16px;">
                    ${dayPlannerCards}
                </div>
            </section>

            <!-- Row 4: AI Travel Guidance and Advice (Full width) -->
            <section class="place-panel trip-detail-panel trip-detail-panel-wide">
                <h2><i class="fas fa-wand-magic-sparkles"></i> AI Guidance and Insights</h2>
                ${renderAISection(data)}
                ${Array.isArray(data.additionalRecommendations) && data.additionalRecommendations.length > 0 ? `
                <div class="trip-detail-extra-recommendations">
                    <h3>More places to consider</h3>
                    ${renderRecommendationCards(data.additionalRecommendations || [])}
                </div>` : ''}
            </section>

            <!-- Row 5: Live Destination Insights (Full width) -->
            <section class="place-panel trip-detail-panel trip-detail-panel-wide">
                <h2><i class="fas fa-cloud-sun"></i> Live Insights</h2>
                <p style="font-size: 13px; color: var(--text-light); margin-bottom: 12px; line-height: 1.5;">Real-time weather parameters and travel safety alerts for ${escapeHtml(tripCity)}.</p>
                <div id="live-insights-grid" class="insight-small-grid" style="margin-top:12px;">
                    <div id="live-weather-card" class="insight-small-card">Loading weather...</div>
                    <div id="live-danger-card" class="insight-small-card">Loading alerts...</div>
                    <div id="live-crowd-card" class="insight-small-card">Loading crowd...</div>
                </div>
            </section>

            <!-- Row 6: Map & Local Attractions (Full width) -->
            <section class="place-panel trip-detail-panel trip-detail-panel-wide">
                <h2><i class="fas fa-map-location-dot"></i> Map & Attractions</h2>
                <div class="itinerary-map-overview trip-detail-map-grid" style="display:grid; grid-template-columns:1fr; gap:16px;">
                    <div class="itinerary-map-box box" style="padding:0; margin:0; border:none; box-shadow:none; background:transparent;">
                        <iframe src="${mapEmbedUrl}" width="100%" height="180" style="border:none;border-radius:12px;" loading="lazy"></iframe>
                    </div>
                    <div class="itinerary-overview-box box" style="padding:0; margin:0; border:none; box-shadow:none; background:transparent;">
                        <h4 style="margin-top:0; font-size:15px; font-weight:700;"><i class="fas fa-compass"></i> Nearby in ${escapeHtml(String(tripCity))}</h4>
                        <div class="nearby-anchors" style="margin-top:8px;">
                            ${renderNearbyAttractions(guide)}
                        </div>
                    </div>
                </div>
            </section>
        </div>
    `;

    function truncateText(s, n) {
        if (!s) return '';
        const str = String(s).trim();
        if (str.length <= n) return str;
        return str.slice(0, n - 1).trim() + '…';
    }

    hydrateLiveInsights(data);
    hydrateAlternativeRecommendations(data);

    // Select the hero actions container in trip-detail.html and dynamically inject the hero save button
    const heroActions = document.querySelector('.itinerary-hero-actions');
    if (heroActions) {
        let saveHeroBtn = document.getElementById('saveItineraryHeroBtn');
        if (!hasTripId) {
            if (!saveHeroBtn) {
                saveHeroBtn = document.createElement('button');
                saveHeroBtn.id = 'saveItineraryHeroBtn';
                saveHeroBtn.className = 'btn btn-primary itinerary-hero-btn';
                saveHeroBtn.innerHTML = `<i class="fas fa-heart"></i> Save Itinerary`;
                saveHeroBtn.addEventListener('click', saveCurrentItinerary);
                // Insert it before the first child of heroActions
                heroActions.insertBefore(saveHeroBtn, heroActions.firstChild);
            }
        } else if (saveHeroBtn) {
            saveHeroBtn.remove();
        }
    }

    // (Data toggle button removed - no longer showing raw JSON data in frontend)
}

// Export current itinerary to a print-friendly window (user can Save as PDF)
function exportItineraryPdf() {
    const container = document.getElementById('itinerary-output');
    if (!container) {
        alert('No itinerary available to export.');
        return;
    }

    const w = window.open('', '_blank');
    if (!w) {
        alert('Unable to open new window for printing.');
        return;
    }

    const styleEl = Array.from(document.querySelectorAll('link[rel="stylesheet"], style')).map(n => n.outerHTML).join('\n');
    w.document.write(`<!doctype html><html><head><meta charset="utf-8"><title>Itinerary - Print</title>${styleEl}</head><body>`);
    const cloned = container.cloneNode(true);
    // remove interactive controls in cloned content
    cloned.querySelectorAll('button, a').forEach(n => n.remove());
    w.document.body.appendChild(cloned);
    w.document.close();
    // Give browser a moment to layout then call print
    setTimeout(() => {
        w.print();
    }, 300);
}

// Render AI insights section
function renderAISection(data) {
    let html = '';

    // AI Summary
    if (data.aiSummary) {
        html += '<div style="margin-top: 30px; background: #e3f2fd; padding: 15px; border-radius: 8px; border-left: 4px solid #2196F3;">';
        html += '<h3 style="margin-top: 0; color: #1976D2;"><i class="fas fa-robot"></i> AI Travel Insight</h3>';
        html += `<p>${data.aiSummary}</p>`;
        html += '</div>';
    }

    // Tips
    if (data.tips && Array.isArray(data.tips) && data.tips.length > 0) {
        html += '<div style="margin-top: 20px; background: #f3e5f5; padding: 15px; border-radius: 8px; border-left: 4px solid #9c27b0;">';
        html += '<h3 style="margin-top: 0; color: #7b1fa2;"><i class="fas fa-list"></i> Quick Tips</h3>';
        html += '<ul style="margin: 10px 0;">';
        data.tips.forEach(tip => {
            html += `<li style="margin: 5px 0;">${tip}</li>`;
        });
        html += '</ul>';
        html += '</div>';
    }

    // Budget Advice
    if (data.budgetAdvice) {
        html += '<div style="margin-top: 20px; background: #e8f5e9; padding: 15px; border-radius: 8px; border-left: 4px solid #4CAF50;">';
        html += '<h3 style="margin-top: 0; color: #388e3c;"><i class="fas fa-money-bill-wave"></i> Budget Advice</h3>';
        html += `<p>${data.budgetAdvice}</p>`;
        html += '</div>';
    }

    // Safety Tips
    if (data.generalSafetyTips) {
        html += '<div style="margin-top: 20px; background: #fff3e0; padding: 15px; border-radius: 8px; border-left: 4px solid #ff9800;">';
        html += '<h3 style="margin-top: 0; color: #e65100;"><i class="fas fa-exclamation-triangle"></i> Safety Tips</h3>';
        html += `<p>${data.generalSafetyTips}</p>`;
        html += '</div>';
    }

    if (Array.isArray(data.additionalRecommendations) && data.additionalRecommendations.length > 0) {
        html += '<div style="margin-top: 20px; background: #eef7ff; padding: 15px; border-radius: 8px; border-left: 4px solid #1976d2;">';
        html += '<h3 style="margin-top: 0; color: #0d47a1;"><i class="fas fa-compass"></i> Extra recommendations</h3>';
        html += '<div style="display:grid; gap:10px;">';
        data.additionalRecommendations.slice(0, 6).forEach(rec => {
            html += `<div style="background:#fff; padding:10px 12px; border-radius:10px; border:1px solid rgba(25,118,210,0.12);"><strong>${escapeHtml(String(rec.placeName || 'Place'))}</strong>${rec.category ? ` <span style="color:#1976d2;">(${escapeHtml(String(rec.category))})</span>` : ''}<div style="margin-top:4px; color:#546e7a;">${escapeHtml(String(rec.reason || 'Recommended as an alternative stop.'))}</div></div>`;
        });
        html += '</div></div>';
    }

    return html;
}

async function hydrateLiveInsights(data) {
    const city = getTripCity(data);
    const weatherCard = document.getElementById('live-weather-card');
    const dangerCard = document.getElementById('live-danger-card');
    const crowdCard = document.getElementById('live-crowd-card');
    if (!city) {
        if (weatherCard) weatherCard.innerHTML = '';
        if (dangerCard) dangerCard.innerHTML = '';
        if (crowdCard) crowdCard.innerHTML = '';
        return;
    }

    if (weatherCard) weatherCard.innerHTML = '<div style="text-align:center;">Loading weather...</div>';
    if (dangerCard) dangerCard.innerHTML = '<div style="text-align:center;">Loading alerts...</div>';
    if (crowdCard) crowdCard.innerHTML = '<div style="text-align:center;">Loading crowd...</div>';

    try {
        const insights = await fetchExternalInsights(city);
        // Weather
        try {
            if (weatherCard) {
                weatherCard.className = 'insight-small-card insight-weather-card';
                weatherCard.innerHTML = renderWeatherCardCompact(city, insights.weather);
            }
        } catch (e) { console.warn('render weather failed', e); }

        // Danger alerts (short)
        try {
            if (dangerCard) {
                const hasAlerts = Array.isArray(insights.dangerAlerts) && insights.dangerAlerts.length > 0;
                dangerCard.className = `insight-small-card ${hasAlerts ? 'insight-danger-card-warning' : 'insight-danger-card-safe'}`;
                dangerCard.innerHTML = renderDangerSummary(insights.dangerAlerts, city);
            }
        } catch (e) { console.warn('render danger failed', e); }

        // Crowd alerts (short)
        try {
            if (crowdCard) {
                const hasAlerts = Array.isArray(insights.crowdAlerts) && insights.crowdAlerts.length > 0;
                crowdCard.className = `insight-small-card ${hasAlerts ? 'insight-crowd-card-advisory' : 'insight-crowd-card-normal'}`;
                crowdCard.innerHTML = renderCrowdSummary(insights.crowdAlerts, city);
            }
        } catch (e) { console.warn('render crowd failed', e); }

        // quick weather badge in header
        try {
            const badge = document.getElementById('itinerary-weather-badge');
            if (badge && insights.weather && insights.weather.current) {
                const w = insights.weather.current;
                const temp = w.temperature ?? 'N/A';
                const cond = w.condition || '';
                badge.innerHTML = `<div class="weather-badge"><i class="fas fa-cloud-sun"></i> ${escapeHtml(String(temp))}°C ${escapeHtml(String(cond))}</div>`;
            }
        } catch (e) { console.warn('weather badge update failed', e); }
    } catch (error) {
        console.error('Failed to load live external insights:', error);
        if (weatherCard) weatherCard.innerHTML = '<div style="text-align:center; color:#b71c1c;">Weather unavailable</div>';
        if (dangerCard) dangerCard.innerHTML = '<div style="text-align:center; color:#b71c1c;">Alerts unavailable</div>';
        if (crowdCard) crowdCard.innerHTML = '<div style="text-align:center; color:#b71c1c;">Crowd data unavailable</div>';
    }
}

async function hydrateAlternativeRecommendations(data) {
    const target = document.getElementById('trip-alternative-recommendations');
    if (!target) return;

    const city = getTripCity(data);
    if (!city) {
        target.innerHTML = '';
        return;
    }

    const itineraryPlaces = new Set(getTripPlaceNames(data));

    try {
        const response = await fetch(`${API_BASE_URL}/explore/city/${encodeURIComponent(city)}`);
        const result = await response.json();
        const places = Array.isArray(result?.data) ? result.data : [];
        const filtered = places.filter(place => !itineraryPlaces.has(normalizeTripPlaceName(place.placeName)));
        const recs = filtered.slice(0, 4);

        if (!recs.length) {
            target.innerHTML = '';
            return;
        }

        target.innerHTML = `
            <div class="box">
                <h3><i class="fas fa-compass"></i> More places to consider</h3>
                <div class="itinerary-ai-grid">
                    ${recs.map(place => `
                        <article class="insight-card insight-card-blue">
                            <div class="insight-icon"><i class="fas fa-location-dot"></i></div>
                            <h3>${escapeHtml(String(place.placeName || 'Place'))}</h3>
                            <p>${escapeHtml(String(place.category || place.placeType || 'Alternative stop'))}</p>
                            <ul>
                                <li>${escapeHtml(String(place.description || place.significance || 'Good alternative for your trip'))}</li>
                                ${place.localTips ? `<li>${escapeHtml(String(place.localTips))}</li>` : ''}
                            </ul>
                        </article>
                    `).join('')}
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Failed to load alternative recommendations:', error);
        target.innerHTML = '';
    }
}

function getTripPlaceNames(data) {
    const names = [];
    if (Array.isArray(data?.itinerary)) {
        data.itinerary.forEach(day => {
            if (Array.isArray(day?.places)) {
                day.places.forEach(place => {
                    if (place?.placeName) names.push(place.placeName);
                });
            }
        });
    }
    return names;
}

function normalizeTripPlaceName(name) {
    return String(name || '').trim().toLowerCase();
}

function getTripCity(data) {
    if (data?.canonicalDestination && String(data.canonicalDestination).trim()) {
        return String(data.canonicalDestination).trim();
    }

    if (data?.savedDestination && String(data.savedDestination).trim()) {
        return String(data.savedDestination).trim();
    }

    if (data?.city && String(data.city).trim()) {
        return String(data.city).trim();
    }

    if (data?.destination && String(data.destination).trim()) {
        return String(data.destination).trim();
    }

    if (data?.plannerRequest?.city && String(data.plannerRequest.city).trim()) {
        return String(data.plannerRequest.city).trim();
    }

    if (data?.plannerRequest?.region && String(data.plannerRequest.region).trim()) {
        return String(data.plannerRequest.region).trim();
    }

    const storedCity = sessionStorage.getItem('itineraryCity');
    if (storedCity && storedCity.trim()) {
        return storedCity.trim();
    }

    if (Array.isArray(data?.itinerary)) {
        for (const day of data.itinerary) {
            if (day?.location?.city && String(day.location.city).trim()) {
                return String(day.location.city).trim();
            }
        }
    }

    return null;
}

function renderBudgetPanel(budget) {
    const days = budget?.days || 1;
    const travellers = budget?.travelers || budget?.travellers || 1;
    const perPersonPerDay = budget?.perPersonPerDay || budget?.dailyPerTraveller || 1500;
    const totalPerDay = perPersonPerDay * travellers;
    const totalAmount = budget?.total || (totalPerDay * days);

    const breakdown = {
        hotel: budget?.hotel || 0,
        food: budget?.food || 0,
        transport: budget?.transport || 0,
        activities: budget?.activities || 0
    };

    // Validate exact sum
    const breakdownSum = breakdown.hotel + breakdown.food + breakdown.transport + breakdown.activities;
    const budgetDisplayAmount = breakdownSum > 0 ? breakdownSum : totalAmount;

    const tierLabel = budget?.tierLabel || 'Estimated';

    const breakdownItems = [
        { label: 'Hotel', value: breakdown.hotel, icon: 'fa-building', modifier: 'budget-breakdown-hotel' },
        { label: 'Food', value: breakdown.food, icon: 'fa-utensils', modifier: 'budget-breakdown-food' },
        { label: 'Transport', value: breakdown.transport, icon: 'fa-car-side', modifier: 'budget-breakdown-transport' },
        { label: 'Activities', value: breakdown.activities, icon: 'fa-star', modifier: 'budget-breakdown-activities' }
    ].map(item => ({
        ...item,
        percent: Math.round((Number(item.value || 0) / budgetDisplayAmount) * 100)
    }));

    return `
        <div class="trip-budget-shell">
            <!-- Budget tier banner -->
            <div class="trip-budget-daily-rate-banner" style="background: linear-gradient(135deg, #0a3d62 0%, #1b7e71 100%); color: #fff; border-radius: 12px; padding: 16px 20px; margin-bottom: 16px; display: flex; align-items: center; justify-content: space-between;">
                <div>
                    <span style="font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; opacity: 0.75;">${escapeHtml(tierLabel)} Plan</span>
                    <div style="font-size: 28px; font-weight: 900; margin-top: 2px;">₹${formatRupees(perPersonPerDay)}<span style="font-size: 13px; font-weight: 500; opacity: 0.8;">/person/day</span></div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 11px; opacity: 0.75; font-weight: 600;">Grand Total</div>
                    <div style="font-size: 22px; font-weight: 900; color: #81ecec;">₹${formatRupees(budgetDisplayAmount)}</div>
                </div>
            </div>

            <!-- Trip parameters strip: travelers × days -->
            <div style="background: #f0f9ff; border: 1px solid #bde8f7; border-radius: 10px; padding: 14px 16px; margin-bottom: 14px;">
                <div style="font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; color: #0369a1; margin-bottom: 10px;">Trip Parameters</div>
                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px;">
                    <div style="text-align: center; background: #fff; border-radius: 8px; padding: 10px 6px; border: 1px solid #e0f2fe;">
                        <div style="font-size: 20px; font-weight: 800; color: #0a3d62;">${escapeHtml(String(travellers))}</div>
                        <div style="font-size: 11px; color: #64748b; font-weight: 600; margin-top: 2px;"><i class="fas fa-users" style="margin-right: 3px; color: #0ea5e9;"></i>Traveler${travellers > 1 ? 's' : ''}</div>
                    </div>
                    <div style="text-align: center; background: #fff; border-radius: 8px; padding: 10px 6px; border: 1px solid #e0f2fe;">
                        <div style="font-size: 20px; font-weight: 800; color: #0a3d62;">${escapeHtml(String(days))}</div>
                        <div style="font-size: 11px; color: #64748b; font-weight: 600; margin-top: 2px;"><i class="fas fa-calendar-days" style="margin-right: 3px; color: #0ea5e9;"></i>Days</div>
                    </div>
                    <div style="text-align: center; background: #fff; border-radius: 8px; padding: 10px 6px; border: 1px solid #e0f2fe;">
                        <div style="font-size: 14px; font-weight: 800; color: #1b7e71;">₹${formatRupees(totalPerDay)}</div>
                        <div style="font-size: 11px; color: #64748b; font-weight: 600; margin-top: 2px;"><i class="fas fa-wallet" style="margin-right: 3px; color: #1b7e71;"></i>/Day Total</div>
                    </div>
                </div>
                <!-- Formula row -->
                <div style="margin-top: 10px; padding: 8px 12px; background: #e0f2fe; border-radius: 6px; font-size: 12px; color: #0369a1; text-align: center; font-weight: 600;">
                    ₹${formatRupees(perPersonPerDay)}/person/day × ${escapeHtml(String(travellers))} traveler${travellers > 1 ? 's' : ''} × ${escapeHtml(String(days))} days = <strong style="color: #0a3d62; font-size: 13px;">₹${formatRupees(budgetDisplayAmount)}</strong>
                </div>
            </div>

            <div class="trip-budget-overview-title" style="font-size: 13px; font-weight: 700; color: #475569; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 10px;">Category Breakdown <span style="font-weight: 400; text-transform: none; font-size: 12px;">(for all travelers, full trip)</span></div>
            <div class="trip-budget-breakdown-block">
                <div class="trip-budget-breakdown-grid">
                    ${breakdownItems.map(item => `
                        <article class="budget-breakdown-card ${escapeHtml(item.modifier)}">
                            <div class="budget-breakdown-head">
                                <span class="budget-breakdown-icon"><i class="fas ${escapeHtml(item.icon)}"></i></span>
                                <div>
                                    <h4>${escapeHtml(item.label)}</h4>
                                </div>
                                <span class="budget-breakdown-percent">${escapeHtml(String(item.percent))}%</span>
                            </div>
                            <strong class="budget-breakdown-value">₹${formatRupees(item.value)}</strong>
                            <div class="budget-breakdown-footer" style="font-size: 11px; color: #64748b; margin-top: 4px;">
                                ₹${formatRupees(Math.round(item.value / Math.max(1, travellers) / Math.max(1, days)))}/person/day
                            </div>
                            <div class="budget-breakdown-footer">${escapeHtml({
        hotel: 'Comfortable stays',
        food: 'Local & variety meals',
        transport: 'Local travel & transfers',
        activities: 'Experiences & entry'
    }[item.label.toLowerCase()] || '')}</div>
                        </article>
                    `).join('')}
                </div>
                <div class="budget-disclaimer" style="margin-top: 14px; padding: 12px; background: #fff3e0; border-left: 3px solid #ff9800; border-radius: 4px; font-size: 12px; color: #e65100; line-height: 1.4;">
                    <i class="fas fa-info-circle" style="margin-right: 6px;"></i>
                    <strong>Disclaimer:</strong> Estimated costs are approximate and may vary by season, hotel choice, and activities.
                </div>
            </div>
        </div>
    `;
}

function renderRecommendationCards(recommendations = []) {
    if (!Array.isArray(recommendations) || recommendations.length === 0) {
        return '';
    }

    return `
        <div class="trip-recommendation-grid">
            ${recommendations.slice(0, 6).map(rec => `
                <article class="trip-recommendation-card">
                    <p class="trip-recommendation-kicker">${escapeHtml(String(rec.category || 'Recommendation'))}</p>
                    <h4>${escapeHtml(String(rec.placeName || 'Place'))}</h4>
                    <p>${escapeHtml(String(rec.reason || 'Recommended for this itinerary.'))}</p>
                </article>
            `).join('')}
        </div>
    `;
}

function resolveItineraryImage(place, fallbackCity) {
    const placeName = (place?.placeName || place?.city || fallbackCity || 'destination').trim();
    const guide = getDestinationGuide(placeName) || getDestinationGuide(fallbackCity || '') || {};

    // Prefer explicit image fields from planner/place DTO or guide
    const explicit = place?.imageUrl || place?.image || guide.image || null;
    if (explicit) return resolveImagePath(explicit);

    // Fallback to a curated image from Unsplash/Flickr via LoremFlickr
    try {
        const city = (fallbackCity || placeName || 'travel').split(',')[0].trim();
        const term = placeName.split(',')[0].trim();
        //  return `https://loremflickr.com/640/480/${encodeURIComponent(city)},${encodeURIComponent(term)}`;
    } catch (e) {
        return resolveImagePath('assets/images/placeholder.jpg');
    }
}

function getUnsplashDestinationImages(destination, count = 3, placeNames = []) {
    const city = String(destination || 'travel').split(',')[0].trim();
    if (!city) {
        return [resolveImagePath('assets/images/placeholder.jpg')];
    }

    const urls = [];

    if (Array.isArray(placeNames) && placeNames.length > 0) {
        const uniquePlaces = [...new Set(placeNames)];
        for (const place of uniquePlaces) {
            if (urls.length >= count) break;
            const placeKey = String(place).trim();
            urls.push(`https://loremflickr.com/1600/900/${encodeURIComponent(city)},${encodeURIComponent(placeKey)}`);
        }
    }

    while (urls.length < count) {
        const index = urls.length + 1;
        urls.push(`https://loremflickr.com/1600/900/${encodeURIComponent(city)}?lock=${index * 13}`);
    }

    return urls;
}

function getUnsplashDestinationImage(destination) {
    return getUnsplashDestinationImages(destination, 1)[0];
}

function renderDayImageStrip(day, tripCity) {
    const places = Array.isArray(day?.places) ? day.places : [];
    const destinationImage = getUnsplashDestinationImage(tripCity);

    // Reduce visual clutter: only show up to 2 images per day, prioritized by places with images
    const images = places
        .map(place => ({ place, image: resolveItineraryImage(place, tripCity) }))
        .filter(item => Boolean(item.image));

    // Always lead with the selected destination image from Unsplash.
    images.unshift({
        place: { placeName: tripCity },
        image: destinationImage
    });

    const deduped = images.filter((item, index, list) => {
        return list.findIndex(entry => entry.image === item.image) === index;
    });

    // If no place-specific images, fall back to a city image
    if (!deduped.length) {
        deduped.push({ place: { placeName: tripCity }, image: resolveItineraryImage(null, tripCity) });
    }

    const selected = deduped.slice(0, 2);

    if (selected.length === 1) {
        return `<div class="trip-day-images single-image"><img src="${escapeHtml(selected[0].image)}" alt="${escapeHtml(String(selected[0].place?.placeName || tripCity))}"></div>`;
    }

    // Two-image layout: large left, small right
    return `
        <div class="trip-day-images two-images">
            <div class="left"><img src="${escapeHtml(selected[0].image)}" alt="${escapeHtml(String(selected[0].place?.placeName || tripCity))}"></div>
            <div class="right"><img src="${escapeHtml(selected[1].image)}" alt="${escapeHtml(String(selected[1].place?.placeName || tripCity))}"></div>
        </div>
    `;
}

async function fetchExternalInsights(city) {
    const encodedCity = encodeURIComponent(city);

    const [weatherResult, dangerResult, crowdResult] = await Promise.allSettled([
        fetch(`${EXTERNAL_BASE_URL}/weather/place/${encodedCity}`).then(r => r.ok ? r.json() : null),
        fetch(`${EXTERNAL_BASE_URL}/alerts/danger/${encodedCity}`).then(r => r.ok ? r.json() : []),
        fetch(`${EXTERNAL_BASE_URL}/alerts/crowd/${encodedCity}`).then(r => r.ok ? r.json() : [])
    ]);

    return {
        weather: weatherResult.status === 'fulfilled' ? weatherResult.value : null,
        dangerAlerts: dangerResult.status === 'fulfilled' && Array.isArray(dangerResult.value) ? dangerResult.value : [],
        crowdAlerts: crowdResult.status === 'fulfilled' && Array.isArray(crowdResult.value) ? crowdResult.value : []
    };
}

function renderLiveInsightsHtml(city, insights) {
    const weatherHtml = renderWeatherCards(insights.weather);
    const dangerHtml = renderDangerAlerts(insights.dangerAlerts);
    const crowdHtml = renderCrowdAlerts(insights.crowdAlerts);

    return [
        '<div class="box" style="padding: 18px;">',
        `<h3 style="margin-top:0;">Live Insights for ${escapeHtml(city)}</h3>`,
        weatherHtml,
        dangerHtml,
        crowdHtml,
        '</div>'
    ].join('');
}

function renderWeatherCards(weatherData) {
    // Deprecated: keep for backwards compat. Use compact card renderer instead.
    if (!weatherData || !weatherData.current) return '<p style="color:#666;">Weather data is currently unavailable.</p>';
    const current = weatherData.current;
    return renderWeatherCardCompact(null, weatherData);
}

function renderWeatherCardCompact(city, weatherData) {
    if (!weatherData || !weatherData.current) {
        return `
            <div class="insight-small-icon"><i class="fas fa-cloud-sun"></i></div>
            <div>
                <strong>No Weather</strong>
                <div style="font-size:13px;color:var(--text-muted);">Data unavailable</div>
            </div>
        `;
    }
    const w = weatherData.current;
    const temp = w.temperature ?? 'N/A';
    const cond = w.condition || '—';
    const cityName = (city || '').toLowerCase();
    const short = cityName === 'jaipur';

    return `
        <div class="insight-small-icon"><i class="fas fa-cloud-sun"></i></div>
        <div>
            <strong>${escapeHtml(String(temp))}°C</strong>
            <div style="font-size:13px;color:var(--text-muted);">${escapeHtml(String(cond))}${short ? '' : ` · Hum ${escapeHtml(String(w.humidity ?? 'N/A'))}%`}</div>
        </div>
    `;
}

function renderDangerSummary(alerts = [], city) {
    if (!Array.isArray(alerts) || alerts.length === 0) {
        return `
            <div class="insight-small-icon"><i class="fas fa-shield-halved"></i></div>
            <div>
                <strong>No Active Alerts</strong>
                <div style="font-size:13px;color:var(--text-muted);">Safe to travel</div>
            </div>
        `;
    }
    const top = alerts.slice(0, 2).map(a => `${escapeHtml(String(a.alertType || a.description || 'Alert'))}`).join(' · ');
    return `
        <div class="insight-small-icon"><i class="fas fa-triangle-exclamation"></i></div>
        <div>
            <strong>${escapeHtml(String(alerts[0].riskLevel || 'Alert'))}</strong>
            <div style="font-size:13px;color:var(--text-muted);">${top}</div>
        </div>
    `;
}

function renderCrowdSummary(alerts = [], city) {
    if (!Array.isArray(alerts) || alerts.length === 0) {
        return `
            <div class="insight-small-icon"><i class="fas fa-users"></i></div>
            <div>
                <strong>Normal Density</strong>
                <div style="font-size:13px;color:var(--text-muted);">No crowd advisory</div>
            </div>
        `;
    }
    const top = alerts.slice(0, 2).map(a => `${escapeHtml(String(a.placeName || 'Area'))}: ${escapeHtml(String(a.crowdLevel || 'Busy'))}`).join(' · ');
    return `
        <div class="insight-small-icon"><i class="fas fa-users-viewfinder"></i></div>
        <div>
            <strong>Crowd Advisory</strong>
            <div style="font-size:13px;color:var(--text-muted);">${top}</div>
        </div>
    `;
}

function renderDangerAlerts(alerts) {
    if (!Array.isArray(alerts) || alerts.length === 0) {
        return '<p style="margin-top:12px; color:#2e7d32;">No active danger alerts reported for this city.</p>';
    }

    const items = alerts.slice(0, 5).map(alert => {
        const risk = alert.riskLevel || 'UNKNOWN';
        const desc = alert.description || 'No details provided';
        const recommendation = alert.recommendation || 'Follow local authority guidance.';
        return [
            '<div style="background:#fff3e0; border-left:4px solid #f57c00; padding:10px; margin-top:8px; border-radius:6px;">',
            `<p style="margin:0 0 4px 0;"><strong>${escapeHtml(String(risk))}</strong> - ${escapeHtml(String(alert.alertType || 'Safety'))}</p>`,
            `<p style="margin:0 0 4px 0;">${escapeHtml(String(desc))}</p>`,
            `<p style="margin:0; color:#5d4037;"><strong>Advice:</strong> ${escapeHtml(String(recommendation))}</p>`,
            '</div>'
        ].join('');
    }).join('');

    return `<div style="margin-top:14px;"><h4 style="margin:0 0 8px 0; color:#bf360c;">Danger Alerts</h4>${items}</div>`;
}

function renderCrowdAlerts(alerts) {
    if (!Array.isArray(alerts) || alerts.length === 0) {
        return '<p style="margin-top:12px; color:#1565c0;">No crowd alerts currently available.</p>';
    }

    const items = alerts.slice(0, 5).map(alert => {
        const level = alert.crowdLevel || 'UNKNOWN';
        const message = alert.message || 'No crowd advisory details.';
        const trend = alert.trend || 'STABLE';
        return [
            '<div style="background:#e8eaf6; border-left:4px solid #3949ab; padding:10px; margin-top:8px; border-radius:6px;">',
            `<p style="margin:0 0 4px 0;"><strong>${escapeHtml(String(alert.placeName || 'Area'))}</strong> - ${escapeHtml(String(level))}</p>`,
            `<p style="margin:0 0 4px 0;">${escapeHtml(String(message))}</p>`,
            `<p style="margin:0; color:#303f9f;"><strong>Trend:</strong> ${escapeHtml(String(trend))}</p>`,
            '</div>'
        ].join('');
    }).join('');

    return `<div style="margin-top:14px;"><h4 style="margin:0 0 8px 0; color:#283593;">Crowd Alerts</h4>${items}</div>`;
}

function renderAISection(data) {
    const cards = [];
    if (data.aiSummary) {
        cards.push(renderInsightCard('fa-robot', 'AI Travel Insight', data.aiSummary, 'blue'));
    }
    if (Array.isArray(data.tips) && data.tips.length > 0) {
        cards.push(renderInsightCard('fa-list-check', 'Quick Tips', `<ul>${data.tips.map(tip => `<li>${escapeHtml(String(tip))}</li>`).join('')}</ul>`, 'teal', true));
    }
    if (data.budgetAdvice) {
        cards.push(renderInsightCard('fa-wallet', 'Budget Advice', data.budgetAdvice, 'green'));
    }
    if (data.generalSafetyTips) {
        cards.push(renderInsightCard('fa-shield-halved', 'Safety Tips', data.generalSafetyTips, 'amber'));
    }
    return cards.length ? `<div class="itinerary-ai-grid">${cards.join('')}</div>` : '';
}

function renderInsightCard(icon, title, body, tone, bodyIsHtml = false) {
    return `
        <article class="insight-card insight-card-${tone}">
            <div class="insight-icon"><i class="fas ${icon}"></i></div>
            <div>
                <h3>${escapeHtml(title)}</h3>
                ${bodyIsHtml ? body : `<p>${escapeHtml(String(body))}</p>`}
            </div>
        </article>
    `;
}


/* --- TRIP PAGES JS BEHAVIOR --- */
// Trip page-specific UI wiring for saved trips and trip detail pages.
// Loaded after script.js so it can reuse the shared helpers and API constants.

async function initializeTripPages() {
    if (window.currentUserPromise) {
        await window.currentUserPromise;
    }

    const listContainer = document.getElementById('saved-trips-output');
    const detailContainer = document.getElementById('itinerary-output');

    if (listContainer) {
        const gridViewBtn = document.getElementById('grid-view');
        const listViewBtn = document.getElementById('list-view');
        if (gridViewBtn && listViewBtn) {
            gridViewBtn.addEventListener('click', () => {
                gridViewBtn.classList.add('grid-active');
                listViewBtn.classList.remove('grid-active');
                listContainer.classList.remove('list-view-active');
            });
            listViewBtn.addEventListener('click', () => {
                listViewBtn.classList.add('grid-active');
                gridViewBtn.classList.remove('grid-active');
                listContainer.classList.add('list-view-active');
            });
        }
        loadSavedTripsPage();
        return;
    }

    if (detailContainer) {
        const params = new URLSearchParams(window.location.search);
        const tripId = params.get('tripId');
        if (tripId) {
            loadTripDetailPage(tripId);
            return;
        }

        const storedPayload = getStoredTripPayload();
        const data = storedPayload?.plannerResponse || JSON.parse(sessionStorage.getItem('itineraryData') || localStorage.getItem('itineraryData') || '{}');
        if (data && Object.keys(data).length > 0) {
            renderItinerary(data);
        }
    }
}

let allUserTrips = [];
let activeDurationFilter = 'all';

async function loadSavedTripsPage() {
    const container = document.getElementById('saved-trips-output');
    if (!container) return;

    const headerRow = document.querySelector('.itinerary-section-header');
    const emptyState = document.getElementById('empty-state');

    const heroEyebrow = document.querySelector('.itinerary-eyebrow');
    const heroDesc = document.querySelector('.hero-description');
    const heroActions = document.querySelector('.itinerary-hero-actions');

    const currentUser = getCurrentUserAccount();
    if (!currentUser?.email) {
        // Remove grid layout so unauthenticated content centers correctly without squishing
        container.classList.remove('itinerary-grid', 'saved-trips-grid');

        if (headerRow) headerRow.classList.add('hidden');
        if (emptyState) emptyState.classList.add('hidden');

        // Customize Hero Banner for logged-out state
        if (heroEyebrow) {
            heroEyebrow.innerHTML = `<i class="fas fa-folder"></i> SAVED ITINERARIES`;
        }
        if (heroDesc) {
            heroDesc.textContent = 'Your generated itineraries are stored in the backend per user. Sign in to view your saved trips, details, and more.';
        }
        if (heroActions) {
            heroActions.style.display = 'none';
        }

        container.innerHTML = `
            <div class="login-promo-wrapper">
                <div class="login-promo-card">
                    <div class="login-promo-badge">
                        <i class="fas fa-lock"></i>
                    </div>
                    <h2>Sign in to view your saved trips</h2>
                    <p>Save and access all your generated itineraries in one place.</p>
                    <div class="login-promo-actions">
                        <a href="login.html" class="btn login-btn"><i class="fas fa-right-to-bracket"></i> Login</a>
                        <a href="register.html" class="btn register-btn"><i class="fas fa-user-plus"></i> Create Account</a>
                    </div>
                </div>
                
                <hr class="promo-divider">
                
                <div class="promo-features">
                    <div class="feature-item">
                        <div class="feature-icon-wrap feature-icon-teal">
                            <i class="fas fa-wand-magic-sparkles"></i>
                        </div>
                        <div class="feature-text">
                            <h3>Save AI-generated itineraries</h3>
                            <p>Keep all your personalized trip plans in one place.</p>
                        </div>
                    </div>
                    <div class="feature-item">
                        <div class="feature-icon-wrap feature-icon-blue">
                            <i class="fas fa-mobile-screen-button"></i>
                        </div>
                        <div class="feature-text">
                            <h3>Access from any device</h3>
                            <p>View your trips anytime, anywhere.</p>
                        </div>
                    </div>
                    <div class="feature-item">
                        <div class="feature-icon-wrap feature-icon-purple">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="feature-text">
                            <h3>Split travel expenses</h3>
                            <p>Easily split and manage budgets with your companions.</p>
                        </div>
                    </div>
                    <div class="feature-item">
                        <div class="feature-icon-wrap feature-icon-rose">
                            <i class="fas fa-wand-magic-sparkles"></i>
                        </div>
                        <div class="feature-text">
                            <h3>Customize & regenerate</h3>
                            <p>Edit your trips and regenerate plans as you like.</p>
                        </div>
                    </div>
                </div>
            </div>
        `;
        return;
    }

    // Apply grid layout for authenticated user trips
    container.classList.add('itinerary-grid', 'saved-trips-grid');

    if (headerRow) headerRow.classList.remove('hidden');
    if (emptyState) emptyState.classList.add('hidden');

    // Restore Hero Banner for logged-in state
    if (heroEyebrow) {
        heroEyebrow.innerHTML = `<i class="fas fa-folder-open"></i> SAVED TRIPS`;
        heroEyebrow.style.color = '';
    }
    if (heroDesc) {
        heroDesc.textContent = 'Your generated itineraries are stored in the backend per user. Open any saved trip to see the full day‑by‑day plan, AI recommendations, maps, and export options.';
    }
    if (heroActions) {
        heroActions.style.display = 'flex';
    }

    container.innerHTML = Array.from({ length: 4 }).map(() => renderSkeletonCard()).join('');

    try {
        const response = await fetch(`${API_BASE_URL}/users/${encodeURIComponent(currentUser.email)}/profile`, {
            credentials: 'include',
            headers: { 'Accept': 'application/json' }
        });
        const result = await response.json();
        if (!response.ok || !result.success || !result.data) {
            throw new Error(result.message || 'Unable to load profile');
        }

        const profile = result.data;
        const trips = Array.isArray(profile.trips) ? profile.trips : [];
        allUserTrips = trips;

        if (!trips.length) {
            if (headerRow) headerRow.classList.add('hidden');
            container.innerHTML = '';
            if (emptyState) emptyState.classList.remove('hidden');
            return;
        }

        if (headerRow) headerRow.classList.remove('hidden');
        if (emptyState) emptyState.classList.add('hidden');
        container.innerHTML = trips.map(renderTripCard).join('');
    } catch (error) {
        console.error('Failed to load saved trips/profile:', error);
        if (headerRow) headerRow.classList.add('hidden');
        if (emptyState) emptyState.classList.add('hidden');
        container.innerHTML = `
            <div class="itinerary-empty-container">
                <div class="itinerary-empty-icon-wrap" style="background:rgba(231,76,60,0.1); color:#e74c3c;">
                    <i class="fas fa-triangle-exclamation"></i>
                </div>
                <h3>Could not load saved trips</h3>
                <p>We encountered an error connecting to the server. Please verify the backend service is running and try again.</p>
            </div>
        `;
    }
}

function renderUserProfile(profile) {
    // Keep hero section static as in HTML to match exact user request
}

function renderSkeletonCard() {
    return `
        <article class="saved-trip-card skeleton-card" aria-hidden="true" style="opacity: 0.6; pointer-events: none;">
            <div class="saved-trip-media" style="background: #e0e0e0; height: 160px;"></div>
            <div class="saved-trip-body" style="padding: 14px 16px; display: flex; flex-direction: column; gap: 10px;">
                <div style="background: #e0e0e0; height: 12px; width: 40%; border-radius: 4px;"></div>
                <div style="background: #e0e0e0; height: 20px; width: 70%; border-radius: 4px;"></div>
                <div style="background: #e0e0e0; height: 14px; width: 50%; border-radius: 4px;"></div>
                <div style="background: #e0e0e0; height: 40px; width: 100%; border-radius: 4px; margin-top: 10px;"></div>
            </div>
        </article>
    `;
}

function renderTripCard(trip) {
    const destination = trip.destination || 'Destination';
    const guide = getDestinationGuide(destination);
    const img = resolveImagePath(guide.image || 'images/placeholder.jpg');
    const summary = trip.summary || getPlaceIntroSummary(destination);

    return `
        <article class="saved-trip-card" role="button" tabindex="0">
            <div class="saved-trip-media" onclick="openSavedTripDetail(${trip.id})" aria-hidden="true">
                <img src="${escapeHtml(img)}" alt="${escapeHtml(destination)}" onerror="this.src='${resolveImagePath('images/placeholder.jpg')}'">
                <button class="card-options-btn" aria-label="Trip options"><i class="fas fa-ellipsis-h"></i></button>
            </div>
            <div class="saved-trip-body">
                <h3>${escapeHtml(String(trip.tripName || destination))}</h3>
                <p class="saved-trip-destination"><i class="fas fa-map-marker-alt"></i> ${escapeHtml(destination)}</p>
                <p class="saved-trip-summary">${escapeHtml(String(summary))}</p>
                <div class="saved-trip-actions">
                    <button class="btn btn-primary" onclick="openSavedTripDetail(${trip.id})"><i class="fas fa-eye"></i> View Trip</button>
                    <button class="btn btn-secondary btn-delete" onclick="deleteSavedTrip(event, ${trip.id})"><i class="fas fa-trash-alt"></i> Delete</button>
                </div>
            </div>
        </article>
    `;
}

async function deleteSavedTrip(event, tripId) {
    if (event) {
        event.stopPropagation();
        event.preventDefault();
    }

    if (!confirm('Are you sure you want to delete this saved trip? This action cannot be undone.')) {
        return;
    }

    const currentUser = getCurrentUserAccount();
    if (!currentUser?.email) {
        alert('You must be logged in to delete trips.');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/trips/${tripId}?email=${encodeURIComponent(currentUser.email)}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        const result = await response.json();

        if (!response.ok || !result.success) {
            throw new Error(result.message || 'Failed to delete trip');
        }

        alert('Trip deleted successfully');
        loadSavedTripsPage(); // reload user trips
    } catch (error) {
        console.error('Error deleting trip:', error);
        alert(error.message || 'Failed to delete trip. Please try again.');
    }
}

function openSavedTripDetail(tripId) {
    const currentUser = getCurrentUserAccount();
    const url = `trip-detail.html?tripId=${encodeURIComponent(String(tripId))}${currentUser?.email ? `&email=${encodeURIComponent(currentUser.email)}` : ''}`;
    window.location.href = url;
}

async function loadTripDetailPage(tripId) {
    const container = document.getElementById('itinerary-output');
    if (!container) return;

    const currentUser = getCurrentUserAccount();
    if (!currentUser?.email) {
        container.innerHTML = `
            <div class="itinerary-empty card">
                <h3>Sign in to view this trip</h3>
                <p>Please log in so we can load your saved trip from the backend.</p>
                <div class="itinerary-empty-actions">
                    <a href="login.html" class="btn btn-primary"><i class="fas fa-right-to-bracket"></i> Login</a>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = '<div class="itinerary-empty card"><h3>Loading trip...</h3><p>Fetching your saved itinerary from the backend.</p></div>';

    try {
        const response = await fetch(`${TRIPS_ENDPOINT}/${encodeURIComponent(String(tripId))}`, {
            credentials: 'include',
            headers: { 'Accept': 'application/json' }
        });
        const result = await response.json();
        const trip = result?.data;

        if (!trip) {
            throw new Error(result?.message || 'Trip not found');
        }

        const tripResponse = trip.plannerResponse || trip;
        const tripCity = trip.destination || tripResponse.city || tripResponse.region || 'Destination';
        const detailPayload = {
            ...tripResponse,
            city: tripCity,
            destination: trip.destination,
            savedDestination: trip.destination || tripCity,
            selectedDestination: trip.plannerRequest?.city || trip.destination || tripCity,
            tripName: trip.tripName,
            plannerRequest: trip.plannerRequest,
            savedTripId: trip.id
        };

        sessionStorage.setItem('itineraryCity', tripCity);
        localStorage.setItem('itineraryCity', tripCity);
        sessionStorage.setItem('itineraryData', JSON.stringify(detailPayload));
        localStorage.setItem('itineraryData', JSON.stringify(detailPayload));
        sessionStorage.setItem('plannerRequestData', JSON.stringify(trip.plannerRequest || {}));
        localStorage.setItem('plannerRequestData', JSON.stringify(trip.plannerRequest || {}));

        renderItinerary(detailPayload);
        const eyebrow = document.querySelector('.itinerary-eyebrow');
        if (eyebrow) eyebrow.innerHTML = `<i class="fas fa-route"></i> Saved trip • ${escapeHtml(String(trip.destination || tripCity || 'Destination'))}`;
        const cta = document.getElementById('exportPdfBtn');
        if (cta) cta.textContent = 'Export PDF';
    } catch (error) {
        console.error('Failed to load trip detail:', error);
        container.innerHTML = `
            <div class="itinerary-empty card">
                <h3>Could not load trip</h3>
                <p>${escapeHtml(String(error.message || 'The saved trip could not be loaded.'))}</p>
            </div>
        `;
    }
}

window.filterSavedTrips = function () {
    const query = document.getElementById('trip-search-input')?.value.toLowerCase().trim() || '';
    const gridContainer = document.getElementById('saved-trips-output');
    if (!gridContainer) return;

    const filtered = allUserTrips.filter(trip => {
        return (trip.tripName || '').toLowerCase().includes(query) ||
            (trip.destination || '').toLowerCase().includes(query) ||
            (trip.summary || '').toLowerCase().includes(query);
    });

    if (filtered.length === 0) {
        gridContainer.innerHTML = `
            <div class="itinerary-empty-container" style="grid-column: 1 / -1; text-align: center; padding: 40px 20px;">
                <div class="itinerary-empty-icon-wrap" style="font-size: 36px; color: var(--text-muted); margin-bottom: 12px; background: transparent;">
                    <i class="fas fa-search"></i>
                </div>
                <h3>No matching trips found</h3>
                <p style="color: var(--text-muted);">Try adjusting your search query.</p>
            </div>
        `;
    } else {
        gridContainer.innerHTML = filtered.map(renderTripCard).join('');
    }
};

document.addEventListener('DOMContentLoaded', initializeTripPages);

async function saveCurrentItinerary() {
    const saveBtn = document.getElementById('saveItineraryBtn');
    const saveHeroBtn = document.getElementById('saveItineraryHeroBtn');
    const saveFloatingBtn = document.getElementById('saveItineraryFloatingBtn');

    const setSaving = (btn) => {
        if (btn) {
            btn.disabled = true;
            btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Saving...`;
        }
    };

    setSaving(saveBtn);
    setSaving(saveHeroBtn);
    setSaving(saveFloatingBtn);

    try {
        const currentUser = getCurrentUserAccount();
        if (!currentUser?.email) {
            alert('Please log in or create an account to save this itinerary.');
            window.location.href = 'login.html';
            return;
        }

        const storedPayload = getStoredTripPayload();
        if (!storedPayload || !storedPayload.plannerResponse) {
            throw new Error('No active itinerary found to save.');
        }

        const requestBody = storedPayload.plannerRequest || {
            city: storedPayload.plannerResponse.city || 'Destination',
            days: storedPayload.plannerResponse.generatedDays || 3,
            travellerType: 'solo'
        };

        const savedTrip = await saveGeneratedTripToBackend(requestBody, storedPayload.plannerResponse);

        showSaveSuccessModal(savedTrip.tripName || savedTrip.destination || 'your itinerary', savedTrip.id);

        const setSaved = (btn) => {
            if (btn) {
                btn.innerHTML = `<i class="fas fa-check-circle"></i> Saved`;
                btn.style.background = '#2e7d32';
                btn.style.borderColor = '#2e7d32';
                btn.style.color = '#fff';
                btn.disabled = true;
            }
        };

        setSaved(saveBtn);
        setSaved(saveHeroBtn);
        setSaved(saveFloatingBtn);

        // Update URL to include the new tripId so it acts as loaded from DB
        const newUrl = `${window.location.pathname}?tripId=${savedTrip.id}`;
        window.history.pushState({ path: newUrl }, '', newUrl);

        // Update the hero eyebrow to reflect saved status
        const eyebrow = document.querySelector('.itinerary-eyebrow');
        if (eyebrow) eyebrow.innerHTML = `<i class="fas fa-route"></i> Saved trip • ${escapeHtml(savedTrip.destination)}`;

    } catch (err) {
        console.error('Failed to save itinerary:', err);
        alert(err.message || 'Failed to save itinerary. Please try again.');

        const resetBtn = (btn, isFloating = false) => {
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = isFloating ? `<i class="fas fa-heart"></i> Add to My Trip` : `<i class="fas fa-heart"></i> Save Itinerary`;
            }
        };
        resetBtn(saveBtn);
        resetBtn(saveHeroBtn);
        resetBtn(saveFloatingBtn, true);
    }
}
window.saveCurrentItinerary = saveCurrentItinerary;

