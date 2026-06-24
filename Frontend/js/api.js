// Planner API integration
// API base is set by config.js (loaded before this script)
const API_BASE_URL    = window.SMARTTRAVEL_API_BASE;
const PLANNER_ENDPOINT = `${API_BASE_URL}/planner/generate`;
const EXTERNAL_BASE_URL = `${API_BASE_URL}/v1/external`;
const TRIPS_ENDPOINT  = `${API_BASE_URL}/trips`;

// Send the request and load itinerary page
async function fetchItinerary(requestBody) {
    // Show a friendly loading overlay while AI generates (can take 20-60s)
    showPlannerLoading(requestBody.city || requestBody.region || 'your destination');

    // Use AbortController so we can time out after 90 seconds (AI calls can be slow)
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 90000);

    try {
        const response = await fetch(PLANNER_ENDPOINT, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
            signal: controller.signal
        });

        clearTimeout(timeoutId);
        hidePlannerLoading();

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            const errorMessage = errData.message || `Server error (${response.status})`;
            const city = requestBody.city || 'the destination';

            // Handle specific backend error cases for unknown/small cities
            if (response.status === 404) {
                if (errorMessage.includes('AI generation is disabled')) {
                    // AI not configured — use client-side fallback with local guide data
                    console.warn(`AI disabled for "${city}" — using local guide fallback`);
                    const fallbackData = buildFallbackItinerary(requestBody);
                    openTripDetailPage(fallbackData, requestBody);
                    return;
                } else if (errorMessage.includes('AI generation failed') || errorMessage.includes('No places found')) {
                    // AI failed or city has no DB data — use client-side fallback
                    console.warn(`No backend data for "${city}" — using local guide fallback`);
                    const fallbackData = buildFallbackItinerary(requestBody);
                    openTripDetailPage(fallbackData, requestBody);
                    return;
                }
            }

            throw new Error(errorMessage);
        }

        const result = await response.json();

        if (result.success && result.data) {
            localStorage.setItem('plannerRequestData', JSON.stringify(requestBody));
            sessionStorage.setItem('plannerRequestData', JSON.stringify(requestBody));

            const isCustomize = new URLSearchParams(window.location.search).has('customize');
            if (isCustomize) {
                const currentUser = getCurrentUserAccount();
                if (currentUser?.email) {
                    try {
                        await saveGeneratedTripToBackend(requestBody, result.data);
                    } catch (saveErr) {
                        console.error('Failed to auto-save customized trip:', saveErr);
                    }
                }
                window.location.href = 'itinerary.html';
            } else {
                openTripDetailPage(result.data, requestBody);
            }
        } else {
            throw new Error(result.message || 'Failed to generate itinerary');
        }
    } catch (error) {
        clearTimeout(timeoutId);
        hidePlannerLoading();
        console.error('Error fetching itinerary:', error);

        if (error.name === 'AbortError') {
            // Timeout — try client-side fallback instead of just showing error
            console.warn('Backend timeout — using local guide fallback');
            try {
                const fallbackData = buildFallbackItinerary(requestBody);
                openTripDetailPage(fallbackData, requestBody);
                return;
            } catch (fallbackErr) {
                console.error('Fallback also failed:', fallbackErr);
            }
            alert('The AI is taking longer than expected. Please try again — it usually completes within 60 seconds.');
            return;
        }

        // Network or other error — try client-side fallback for known destinations
        try {
            const fallbackData = buildFallbackItinerary(requestBody);
            console.warn('Backend error — using local guide fallback:', error.message);
            openTripDetailPage(fallbackData, requestBody);
            return;
        } catch (fallbackErr) {
            console.error('Fallback also failed:', fallbackErr);
        }

        alert('Sorry, we could not generate your itinerary right now. Please try again in a moment.');
    }
}


/** Show a full-page loading overlay while generating the itinerary */
function showPlannerLoading(destination) {
    let overlay = document.getElementById('planner-ai-loading');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'planner-ai-loading';
        overlay.innerHTML = `
            <div class="planner-loading-card">
                <div class="planner-loading-spinner"></div>
                <h3>✈ Generating your itinerary</h3>
                <p id="planner-loading-dest">Planning your perfect trip to <strong></strong></p>
                <p class="planner-loading-note">This may take up to 60 seconds for AI-powered plans</p>
                <div class="planner-loading-dots"><span></span><span></span><span></span></div>
            </div>
        `;
        // Inline styles so no extra CSS file needed
        overlay.style.cssText = `
            position:fixed;inset:0;background:rgba(10,40,70,0.85);backdrop-filter:blur(6px);
            z-index:9999;display:flex;align-items:center;justify-content:center;
        `;
        const card = overlay.querySelector('.planner-loading-card');
        card.style.cssText = `
            background:#fff;border-radius:20px;padding:48px 40px;text-align:center;
            max-width:420px;width:90%;box-shadow:0 24px 60px rgba(0,0,0,0.3);
        `;
        const spinner = overlay.querySelector('.planner-loading-spinner');
        spinner.style.cssText = `
            width:56px;height:56px;border:4px solid #e2e8f0;border-top-color:#14B8A6;
            border-radius:50%;animation:spin 0.9s linear infinite;margin:0 auto 24px;
        `;
        card.querySelector('h3').style.cssText = 'margin:0 0 12px;color:#0F2940;font-size:22px;font-weight:700;';
        card.querySelector('p').style.cssText = 'margin:0 0 8px;color:#4B5563;font-size:15px;';
        card.querySelector('.planner-loading-note').style.cssText = 'margin:0 0 20px;color:#9CA3AF;font-size:13px;';
        const dots = overlay.querySelector('.planner-loading-dots');
        dots.style.cssText = 'display:flex;gap:8px;justify-content:center;';
        dots.querySelectorAll('span').forEach((s, i) => {
            s.style.cssText = `width:8px;height:8px;background:#14B8A6;border-radius:50%;
                animation:bounce 1.2s ease-in-out ${i * 0.2}s infinite;`;
        });
        // Inject keyframes if not present
        if (!document.getElementById('planner-loading-styles')) {
            const style = document.createElement('style');
            style.id = 'planner-loading-styles';
            style.textContent = `
                @keyframes spin { to { transform: rotate(360deg); } }
                @keyframes bounce { 0%,80%,100%{transform:translateY(0)} 40%{transform:translateY(-10px)} }
            `;
            document.head.appendChild(style);
        }
        document.body.appendChild(overlay);
    }
    const destEl = overlay.querySelector('#planner-loading-dest strong');
    if (destEl) destEl.textContent = destination;
    overlay.style.display = 'flex';
}

function hidePlannerLoading() {
    const overlay = document.getElementById('planner-ai-loading');
    if (overlay) overlay.style.display = 'none';
}



function openTripDetailPage(tripPayload, requestBody = {}) {
    const payload = tripPayload?.plannerResponse ? tripPayload.plannerResponse : (tripPayload?.success && tripPayload.data ? tripPayload.data : tripPayload);
    const tripId = tripPayload?.id || tripPayload?.tripId || '';
    if (!payload) return;

    localStorage.setItem('plannerRequestData', JSON.stringify(requestBody));
    sessionStorage.setItem('plannerRequestData', JSON.stringify(requestBody));
    localStorage.setItem('itineraryData', JSON.stringify(payload));
    sessionStorage.setItem('itineraryData', JSON.stringify(payload));
    localStorage.setItem('itineraryCity', requestBody.city || requestBody.placeName || (requestBody.region ? `${requestBody.region} region` : payload.city || 'Destination'));
    sessionStorage.setItem('itineraryCity', requestBody.city || requestBody.placeName || (requestBody.region ? `${requestBody.region} region` : payload.city || 'Destination'));

    const detailUrl = tripId ? `trip-detail.html?tripId=${encodeURIComponent(String(tripId))}` : 'trip-detail.html';
    const newWin = window.open(detailUrl, '_blank');
    if (newWin) newWin.focus();
    else window.location.href = detailUrl;
}

async function saveGeneratedTripToBackend(requestBody, plannerResponse) {
    const currentUser = getCurrentUserAccount();
    if (!currentUser?.email) {
        throw new Error('Please login to save trips to your account.');
    }

    const destination = requestBody.city || requestBody.placeName || requestBody.destinationCity || requestBody.region || plannerResponse?.city || plannerResponse?.region || 'Destination';
    const tripName = buildTripTitle(destination, plannerResponse);
    const payload = {
        userId: currentUser.id || null,
        userEmail: currentUser.email,
        tripName,
        destination,
        plannerRequest: requestBody,
        plannerResponse
    };

    const response = await fetch(`${TRIPS_ENDPOINT}/users/${encodeURIComponent(currentUser.email)}`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    });

    const result = await response.json();
    if (!response.ok || !result.success || !result.data) {
        throw new Error(result.message || 'Unable to save trip');
    }

    return result.data;
}

function buildTripTitle(destination, plannerResponse) {
    const base = String(destination || plannerResponse?.destination || 'Saved Trip').trim();
    const days = plannerResponse?.generatedDays ? `${plannerResponse.generatedDays}-day ` : '';
    return `${days}${base} Trip`;

}

