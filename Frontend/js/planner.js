let selectedTravellerType = null;
let placeKnown = null;
let selectedActivities = {};
let selectedInterests = {};
let selectedRegion = '';


// ========== MODERN PLANNER UI CONTROLLER ==========
const plannerState = {
    currentStep: 1,
    destinationChoice: null,
    destinationCity: '',
    selectedRegion: '',
    selectedPlace: '',
    styles: new Set(),
    moods: new Set(),
    enhancements: new Set()
};

const durationMap = {
    '1to2': 2,
    '3to4': 4,
    '5to6': 6,
    '7plus': 7
};

const labelMap = {
    budget: 'Budget',
    midrange: 'Comfort',
    premium: 'Premium',
    luxury: 'Luxury',
    solo: 'Solo',
    couple: 'Couple',
    family: 'Family',
    friends: 'Friends',
    beach: 'Beach',
    mountains: 'Mountains',
    city: 'Urban',
    adventure: 'Adventure',
    spiritual: 'Spiritual',
    wildlife: 'Wildlife',
    food: 'Culinary',
    cultural: 'Cultural',
    relaxed: 'Relaxed',
    explorative: 'Exploratory',
    romantic: 'Romantic',
    offbeat: 'Unique',
    social: 'Social'
};

const regionalPlaces = {
    North: [
        { name: 'Jaipur', city: 'Rajasthan', image: '../assets/images/jaipur.jpg' },
        { name: 'Ladakh', city: 'Jammu & Kashmir', image: '../assets/images/ladakh.jpg' },
        { name: 'Amritsar', city: 'Punjab', image: '../assets/images/amritsar.jpg' },
        { name: 'Rishikesh', city: 'Uttarakhand', image: '../assets/images/rishikesh.jpg' },
        { name: 'Agra', city: 'Uttar Pradesh', image: '../assets/images/agra.jpg' },
        { name: 'Delhi', city: 'NCR', image: '../assets/images/delhi.jpg' }
    ],
    South: [
        { name: 'Varkala', city: 'Kerala', image: '../assets/images/varkala.jpg' },
        { name: 'Kovalam', city: 'Kerala', image: '../assets/images/kovalam.jpg' },
        { name: 'Bangalore', city: 'Karnataka', image: '../assets/images/bangalore.jpg' },
        { name: 'Chennai', city: 'Tamil Nadu', image: '../assets/images/chennai.jpg' },
        { name: 'Mysuru', city: 'Karnataka', image: '../assets/images/mysuru.jpg' },
        { name: 'Coorg', city: 'Karnataka', image: '../assets/images/coorg.jpg' }
    ],
    East: [
        { name: 'Darjeeling', city: 'West Bengal', image: '../assets/images/darjeeling.jpg' },
        { name: 'Gangtok', city: 'Sikkim', image: '../assets/images/gangtok.jpg' },
        { name: 'Kolkata', city: 'West Bengal', image: '../assets/images/kolkata.jpg' },
        { name: 'Kaziranga', city: 'Assam', image: '../assets/images/kaziranga.jpg' }
    ],
    West: [
        { name: 'Goa', city: 'Goa', image: '../assets/images/goa.jpg' },
        { name: 'Pushkar', city: 'Rajasthan', image: '../assets/images/pushkar.jpg' },
        { name: 'Ahmedabad', city: 'Gujarat', image: '../assets/images/ahmedabad.jpg' },
        { name: 'Mumbai', city: 'Maharashtra', image: '../assets/images/mumbai.jpg' }
    ]
};

document.addEventListener('DOMContentLoaded', initModernPlanner);

function initModernPlanner() {
    if (!document.querySelector('.planner-form')) return;

    wireDestinationChoices();
    wireBasicInputs();
    wireTravellerCards();
    wirePreferenceCards();
    wireSeasonToggle();
    wireEnhancementOptions();
    hydratePlannerSeed();
    updatePlannerControls();
    updateSummaryPanel();
}

function readPlannerSeed() {
    const raw = sessionStorage.getItem('smarttravelPlannerSeed') || localStorage.getItem('smarttravelPlannerSeed');
    if (!raw) return null;

    try {
        return JSON.parse(raw);
    } catch {
        return null;
    }
}

function clearPlannerSeed() {
    sessionStorage.removeItem('smarttravelPlannerSeed');
    localStorage.removeItem('smarttravelPlannerSeed');
}

function setActiveDestinationChoice(choice) {
    plannerState.destinationChoice = choice;
    placeKnown = choice === 'known';
    document.querySelectorAll('.choice-btn[data-choice]').forEach(item => {
        item.classList.toggle('active', item.dataset.choice === choice);
    });
    setVisible('branchKnown', placeKnown);
    setVisible('branchUnknown', !placeKnown);
}

function setSelectValue(id, value) {
    const element = document.getElementById(id);
    if (element && value !== undefined && value !== null && value !== '') {
        element.value = String(value);
    }
}

function mapDaysToDurationKey(days) {
    const totalDays = Number(days) || 0;
    if (totalDays <= 2) return '1to2';
    if (totalDays <= 4) return '3to4';
    if (totalDays <= 6) return '5to6';
    return '7plus';
}

function activateTravellerType(travellerType) {
    if (!travellerType) return;
    selectedTravellerType = travellerType;
    document.querySelectorAll('.traveller-card').forEach(item => {
        item.classList.toggle('active', item.dataset.traveller === travellerType);
    });
    setVisible('groupSizeSection', ['family', 'friends'].includes(selectedTravellerType));
}

function activateOptionCard(type, value) {
    const card = document.querySelector(`.option-card[data-type="${type}"][data-value="${value}"]`);
    if (card) {
        card.classList.add('active');
    }
}

function hydratePlannerSeed() {
    const params = new URLSearchParams(window.location.search);
    if (params.has('newTrip')) {
        clearPlannerSeed();
        return;
    }

    const seed = readPlannerSeed();
    if (!seed) return;

    const plannerRequest = seed.plannerRequest || {};
    const destination = seed.destination || plannerRequest.city || '';
    if (destination) {
        setActiveDestinationChoice('known');
        plannerState.destinationCity = destination;
        const destinationInput = document.getElementById('destinationCity');
        if (destinationInput) destinationInput.value = destination;
    }

    setSelectValue('days', mapDaysToDurationKey(plannerRequest.days));
    setSelectValue('budgetLevel', plannerRequest.budgetLevel);
    setSelectValue('groupSize', plannerRequest.groupSize);

    if (plannerRequest.travellerType) {
        activateTravellerType(plannerRequest.travellerType);
    }

    const useFestival = Boolean(plannerRequest.festival);
    const filterByValue = useFestival ? 'festival' : 'season';
    const activeFilter = document.querySelector(`input[name="filterBy"][value="${filterByValue}"]`);
    if (activeFilter) {
        activeFilter.checked = true;
        setVisible('seasonSection', !useFestival);
        setVisible('festivalSection', useFestival);
    }

    setSelectValue('season', plannerRequest.season);
    setSelectValue('festival', plannerRequest.festival);

    if (plannerRequest.category) {
        plannerState.styles.add(plannerRequest.category);
        activateOptionCard('style', plannerRequest.category);
    }

    String(plannerRequest.mood || '')
        .split(',')
        .map(value => value.trim())
        .filter(Boolean)
        .forEach(value => {
            if (document.querySelector(`.option-card[data-type="style"][data-value="${value}"]`)) {
                plannerState.styles.add(value);
                activateOptionCard('style', value);
                return;
            }

            plannerState.moods.add(value);
            activateOptionCard('mood', value);
        });

    String(plannerRequest.preferences || '')
        .split(',')
        .map(value => value.trim())
        .filter(Boolean)
        .forEach(value => {
            plannerState.enhancements.add(value);
            const checkbox = document.querySelector(`input[name="aiEnhance"][value="${value}"]`);
            if (checkbox) checkbox.checked = true;
        });

    renderSelectedChips();
}

function wireDestinationChoices() {
    document.querySelectorAll('.choice-btn[data-choice]').forEach(choice => {
        choice.addEventListener('click', () => {
            plannerState.destinationChoice = choice.dataset.choice;
            placeKnown = plannerState.destinationChoice === 'known';
            document.querySelectorAll('.choice-btn[data-choice]').forEach(item => item.classList.remove('active'));
            choice.classList.add('active');
            setVisible('branchKnown', placeKnown);
            setVisible('branchUnknown', !placeKnown);
            updatePlannerControls();
            updateSummaryPanel();
        });
    });

    document.getElementById('destinationCity')?.addEventListener('input', event => {
        plannerState.destinationCity = event.target.value.trim();
        updatePlannerControls();
        updateSummaryPanel();
    });

    document.querySelectorAll('.region-card').forEach(card => {
        card.addEventListener('click', () => {
            plannerState.selectedRegion = card.dataset.region || '';
            plannerState.selectedPlace = '';
            document.querySelectorAll('.region-card').forEach(item => item.classList.remove('active'));
            card.classList.add('active');

            // Reset AI recommendations display for new region
            const aiRecResult = document.getElementById('aiRecommendationsResult');
            if (aiRecResult) {
                aiRecResult.style.display = 'none';
                aiRecResult.innerHTML = '';
            }

            renderRegionalPlaces(plannerState.selectedRegion);
            setVisible('suggestedPlaces', true);
            updatePlannerControls();
            updateSummaryPanel();
        });
    });

    document.getElementById('autoSuggestBtn')?.addEventListener('click', async () => {
        if (!plannerState.selectedRegion) {
            alert('Please choose a region first to get recommendations.');
            return;
        }

        const btn = document.getElementById('autoSuggestBtn');
        const originalText = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Finding AI recommendations...';

        try {
            const response = await fetch(`${API_BASE_URL}/planner/recommendations?region=${plannerState.selectedRegion}`);
            if (!response.ok) {
                throw new Error(`Failed to fetch recommendations: ${response.status}`);
            }
            const result = await response.json();
            if (result.success && result.data && result.data.length > 0) {
                renderAiRecommendations(result.data);
            } else {
                throw new Error(result.message || 'No recommendations returned.');
            }
        } catch (error) {
            console.error('Error fetching regional recommendations:', error);
            // Fallback: pick the first 3 places from regionalPlaces
            const places = regionalPlaces[plannerState.selectedRegion] || [];
            const mockData = places.slice(0, 3).map(p => ({
                name: p.name,
                city: p.city,
                description: `A highly recommended travel gem in ${plannerState.selectedRegion} India. Perfect for custom itineraries.`,
                category: 'heritage'
            }));
            renderAiRecommendations(mockData);
        } finally {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    });

    document.getElementById('nextStep1')?.addEventListener('click', () => goToStep(2));
}

function wireBasicInputs() {
    ['days', 'budgetLevel', 'season', 'festival', 'groupSize'].forEach(id => {
        document.getElementById(id)?.addEventListener('input', () => {
            updatePlannerControls();
            updateSummaryPanel();
        });
    });

    document.getElementById('nextStep2')?.addEventListener('click', () => {
        const days = document.getElementById('days')?.value;
        const budget = document.getElementById('budgetLevel')?.value;
        if (!days || !budget) {
            alert('Please choose trip duration and budget.');
            return;
        }
        goToStep(3);
    });
}

function wireTravellerCards() {
    document.querySelectorAll('.traveller-card').forEach(card => {
        card.addEventListener('click', () => {
            selectedTravellerType = card.dataset.traveller || null;
            document.querySelectorAll('.traveller-card').forEach(item => item.classList.remove('active'));
            card.classList.add('active');
            setVisible('groupSizeSection', ['family', 'friends'].includes(selectedTravellerType));
            updatePlannerControls();
            updateSummaryPanel();
        });
    });

    document.getElementById('nextStep3')?.addEventListener('click', () => {
        if (!selectedTravellerType) {
            alert('Please select who is traveling.');
            return;
        }
        goToStep(4);
    });
}

function wirePreferenceCards() {
    document.querySelectorAll('.option-card').forEach(card => {
        card.addEventListener('click', () => {
            const group = card.dataset.type === 'mood' ? plannerState.moods : plannerState.styles;
            const value = card.dataset.value;
            if (!value) return;
            if (group.has(value)) {
                group.delete(value);
                card.classList.remove('active');
            } else {
                group.add(value);
                card.classList.add('active');
            }
            renderSelectedChips();
            updateSummaryPanel();
        });
    });

    document.getElementById('nextStep4')?.addEventListener('click', () => goToStep(5));
}

function wireSeasonToggle() {
    document.querySelectorAll('input[name="filterBy"]').forEach(input => {
        input.addEventListener('change', () => {
            const byFestival = document.querySelector('input[name="filterBy"]:checked')?.value === 'festival';
            setVisible('seasonSection', !byFestival);
            setVisible('festivalSection', byFestival);
            updateSummaryPanel();
        });
    });
}

function wireEnhancementOptions() {
    document.querySelectorAll('input[name="aiEnhance"]').forEach(input => {
        input.addEventListener('change', () => {
            if (input.checked) plannerState.enhancements.add(input.value);
            else plannerState.enhancements.delete(input.value);
            updateSummaryPanel();
        });
    });
}

function renderRegionalPlaces(region) {
    const container = document.getElementById('placesContainer');
    if (!container) return;
    const places = regionalPlaces[region] || [];
    container.innerHTML = places.map(place => `
        <button type="button" class="place-card" data-place="${escapeHtml(place.name)}">
            <img src="${escapeHtml(place.image)}" alt="${escapeHtml(place.name)}" class="place-image" loading="lazy">
            <span class="place-info">
                <span class="place-name">${escapeHtml(place.name)}</span>
                <span class="place-city">${escapeHtml(place.city)}</span>
            </span>
        </button>
    `).join('');

    container.querySelectorAll('.place-card').forEach(card => {
        card.addEventListener('click', () => selectSuggestedPlace(card.dataset.place));
    });
}

function selectSuggestedPlace(placeName) {
    plannerState.selectedPlace = placeName || '';
    document.querySelectorAll('.place-card').forEach(card => {
        card.classList.toggle('active', card.dataset.place === plannerState.selectedPlace);
    });
    document.querySelectorAll('.ai-rec-item').forEach(item => {
        item.classList.toggle('active', item.dataset.place === plannerState.selectedPlace);
    });
    updatePlannerControls();
    updateSummaryPanel();
}

function goToStep(step) {
    plannerState.currentStep = Math.max(1, Math.min(5, Number(step) || 1));
    document.querySelectorAll('.step-content').forEach(panel => {
        panel.classList.toggle('active', Number(panel.dataset.step) === plannerState.currentStep);
    });
    document.querySelectorAll('.stepper-step').forEach(item => {
        const itemStep = Number(item.dataset.step);
        item.classList.toggle('active', itemStep === plannerState.currentStep);
        item.classList.toggle('completed', itemStep < plannerState.currentStep);
    });
    updatePlannerControls();
    updateSummaryPanel();
    document.querySelector('.planner-wrapper')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function updatePlannerControls() {
    const destinationReady = placeKnown
        ? Boolean(document.getElementById('destinationCity')?.value.trim())
        : Boolean(plannerState.selectedRegion || plannerState.selectedPlace);
    setDisabled('nextStep1', !destinationReady);
    setDisabled('nextStep3', !selectedTravellerType);
}

function renderSelectedChips() {
    const container = document.getElementById('selectedChips');
    if (!container) return;
    const chips = [
        ...Array.from(plannerState.styles).map(value => ({ type: 'style', value })),
        ...Array.from(plannerState.moods).map(value => ({ type: 'mood', value }))
    ];

    container.innerHTML = chips.map(chip => `
        <button type="button" class="chip" data-chip-type="${chip.type}" data-chip-value="${chip.value}">
            ${escapeHtml(labelMap[chip.value] || chip.value)}
            <span class="chip-remove" aria-hidden="true">x</span>
        </button>
    `).join('');

    container.querySelectorAll('.chip').forEach(chip => {
        chip.addEventListener('click', () => {
            const group = chip.dataset.chipType === 'mood' ? plannerState.moods : plannerState.styles;
            group.delete(chip.dataset.chipValue);
            document.querySelector(`.option-card[data-type="${chip.dataset.chipType}"][data-value="${chip.dataset.chipValue}"]`)?.classList.remove('active');
            renderSelectedChips();
            updateSummaryPanel();
        });
    });
}

function updateSummaryPanel() {
    const destination = document.getElementById('destinationCity')?.value.trim()
        || plannerState.selectedPlace
        || (plannerState.selectedRegion ? `${plannerState.selectedRegion} India` : '');
    const daysValue = document.getElementById('days')?.value || '';
    const budgetValue = document.getElementById('budgetLevel')?.value || '';
    const filterValue = document.querySelector('input[name="filterBy"]:checked')?.value;
    const timing = filterValue === 'festival'
        ? document.getElementById('festival')?.selectedOptions?.[0]?.text
        : document.getElementById('season')?.selectedOptions?.[0]?.text;

    const rows = [
        ['Destination', destination || 'Choose a destination'],
        ['Traveler', selectedTravellerType ? labelMap[selectedTravellerType] : 'Select traveler type'],
        ['Experience', formatList([...plannerState.styles, ...plannerState.moods]) || 'Pick your preferences'],
        ['Timing', timing && !timing.startsWith('Choose') ? timing : 'Flexible']
    ];

    const summaryContent = document.getElementById('summaryContent');
    if (summaryContent) {
        summaryContent.innerHTML = rows.map(([label, value]) => `
            <div class="summary-row">
                <span>${escapeHtml(label)}</span>
                <strong>${escapeHtml(value)}</strong>
            </div>
        `).join('');
    }

    const budgetLabel = budgetValue ? labelMap[budgetValue] || budgetValue : 'Not set';
    const daysLabel = daysValue ? `${durationMap[daysValue] || daysValue} days` : '-';
    const budgetEl = document.getElementById('summaryBudget');
    const daysEl = document.getElementById('summaryDays');
    if (budgetEl) budgetEl.textContent = budgetLabel;
    if (daysEl) daysEl.textContent = daysLabel;
}

function formatList(values) {
    return values.map(value => labelMap[value] || value).join(', ');
}

function setVisible(id, visible) {
    const element = document.getElementById(id);
    if (element) element.style.display = visible ? '' : 'none';
}

function setDisabled(id, disabled) {
    const element = document.getElementById(id);
    if (element) element.disabled = disabled;
}

function buildPlannerRequest() {
    const daysValue = document.getElementById('days')?.value;
    const budgetLevel = document.getElementById('budgetLevel')?.value;
    const city = document.getElementById('destinationCity')?.value.trim() || plannerState.selectedPlace;
    const filterValue = document.querySelector('input[name="filterBy"]:checked')?.value;

    if (!daysValue || !budgetLevel) {
        throw new Error('Please choose trip duration and budget.');
    }
    if (!selectedTravellerType) {
        throw new Error('Please select who is traveling.');
    }
    if (!city && !plannerState.selectedRegion) {
        throw new Error('Please choose a destination or region.');
    }

    const styles = Array.from(plannerState.styles);
    const moods = Array.from(plannerState.moods);
    const enhancements = Array.from(plannerState.enhancements);

    return {
        travellerType: selectedTravellerType,
        groupSize: Number(document.getElementById('groupSize')?.value) || (['family', 'friends'].includes(selectedTravellerType) ? 4 : null),
        city: city || undefined,
        region: !city ? plannerState.selectedRegion : undefined,
        days: durationMap[daysValue] || 3,
        budgetLevel,
        category: styles[0] || undefined,
        mood: [...styles.slice(1), ...moods].join(',') || undefined,
        season: filterValue === 'season' ? document.getElementById('season')?.value || undefined : undefined,
        festival: filterValue === 'festival' ? document.getElementById('festival')?.value || undefined : undefined,
        enhanceWithAi: enhancements.length > 0,
        preferences: enhancements.join(',')
    };
}

async function generatePlan() {
    let requestBody;
    try {
        requestBody = buildPlannerRequest();
    } catch (error) {
        alert(error.message);
        return;
    }

    const button = document.getElementById('submitBtn');
    const originalText = button?.textContent;
    if (button) {
        button.disabled = true;
        button.textContent = 'Creating itinerary...';
    }

    try {
        await fetchItinerary(requestBody);
    } finally {
        if (button) {
            button.disabled = false;
            button.textContent = originalText || 'Create My Perfect Itinerary';
        }
    }
}

function renderAiRecommendations(data) {
    const container = document.getElementById('aiRecommendationsResult');
    if (!container) return;

    container.innerHTML = `
        <div class="ai-recs-card">
            <div class="ai-recs-header">
                <i class="fas fa-sparkles"></i>
                <span>AI Recommended Destinations</span>
            </div>
            <div class="ai-recs-list">
                ${data.map(rec => {
                    const badgeIcon = getCategoryIcon(rec.category);
                    return `
                        <div class="ai-rec-item" data-place="${escapeHtml(rec.name)}" onclick="selectAiRec('${escapeHtml(rec.name)}')">
                            <div class="ai-rec-meta">
                                <span class="ai-rec-badge ${escapeHtml(rec.category || 'general')}">
                                    <i class="${badgeIcon}"></i> ${escapeHtml(rec.category || 'heritage')}
                                </span>
                                <strong class="ai-rec-name">${escapeHtml(rec.name)}</strong>
                                <span class="ai-rec-city">${escapeHtml(rec.city)}</span>
                            </div>
                            <p class="ai-rec-desc">${escapeHtml(rec.description)}</p>
                            <div class="ai-rec-action">
                                <span class="action-btn-text">Select This Destination <i class="fas fa-arrow-right"></i></span>
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        </div>
    `;

    container.style.display = 'block';

    // Auto-select the first recommendation in the suggestedPlaces grid
    if (data.length > 0) {
        selectSuggestedPlace(data[0].name);

        // Find the place card and scroll to it if it exists
        setTimeout(() => {
            const cardEl = document.querySelector(`.place-card[data-place="${data[0].name}"]`);
            if (cardEl) {
                cardEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            }
        }, 100);
    }
}

function selectAiRec(placeName) {
    selectSuggestedPlace(placeName);
}

function getCategoryIcon(cat) {
    if (!cat) return 'fas fa-map-marker-alt';
    switch (cat.toLowerCase()) {
        case 'beach': return 'fas fa-umbrella-beach';
        case 'nature': return 'fas fa-tree';
        case 'spiritual': return 'fas fa-om';
        case 'heritage': return 'fas fa-landmark';
        case 'adventure': return 'fas fa-mountain';
        case 'city': return 'fas fa-city';
        default: return 'fas fa-star';
    }
}

