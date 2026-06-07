function hydratePlaceInsights() {
    const container = document.getElementById('place-live-insights');
    if (!container) return;

    const placeName = getPlaceNameFromPage();
    if (!placeName) {
        container.innerHTML = '<p style="text-align: center; color: #999; padding: 40px;">No destination selected. Please select from destinations.</p>';
        return;
    }

    // Render with initial guide data
    const initialHtml = renderCompactPlaceInsights(placeName, null, null);
    container.innerHTML = initialHtml;
    applyPlaceCopyOverrides(container, placeName);

    // Fetch live data and re-render
    Promise.allSettled([
        fetch(`${EXTERNAL_BASE_URL}/place-info/${encodeURIComponent(placeName)}`).then(res => res.ok ? res.json() : null),
        fetch(`${EXTERNAL_BASE_URL}/weather/place/${encodeURIComponent(placeName)}`).then(res => res.ok ? res.json() : null)
    ])
        .then(([placeInfoResult, weatherResult]) => {
            const placeInfo = placeInfoResult.status === 'fulfilled' ? placeInfoResult.value : null;
            const weatherPayload = weatherResult.status === 'fulfilled' ? weatherResult.value : null;
            const liveWeather = weatherPayload?.current || placeInfo?.weather || null;

            const liveStatus = liveWeather ? {
                weather: {
                    temperature: liveWeather.temperature ?? 25,
                    condition: liveWeather.condition || 'Live conditions available',
                    humidity: liveWeather.humidity ?? liveWeather.relativeHumidity ?? 65,
                    windSpeed: liveWeather.windSpeed ?? liveWeather.windSpeed10m ?? 12
                }
            } : null;

            container.innerHTML = renderCompactPlaceInsights(placeName, placeInfo, liveStatus);
            applyPlaceCopyOverrides(container, placeName);
        })
        .catch(err => console.log('Live data unavailable:', err));
}

function applyPlaceCopyOverrides(container, placeName) {
    const guide = getDestinationGuide(placeName);
    const famous = String(getFamousFor(placeName)).replace(/[.\s]+$/, '');
    const dayLabel = String(guide.days || '').replace(/\s*days?\s*/i, '-day').replace(/^-/, '');
    const vibeText = String(guide.vibe || '').replace(/[.\s]+$/, '');
    const introText = `${placeName} is a ${dayLabel} escape known for ${famous}. It suits travelers who want an easygoing base, scenic views, and a mix of local food and landmark stops.`;
    const aboutText = `${placeName} has a distinct character shaped by ${vibeText.toLowerCase()}. The destination pairs ${famous.toLowerCase()} with relaxed sightseeing, local food, and scenic viewpoints, so the trip feels calm but still full of highlights. It is easy to build a short stay around walks, viewpoints, and an unhurried local pace.`;

    const introEl = container.querySelector('.place-intro');
    const aboutEl = container.querySelector('.about-copy');
    if (introEl) introEl.textContent = introText;
    if (aboutEl) aboutEl.textContent = aboutText;
}

// Generate a simple collage from available gallery images and open in new tab
async function generatePlaceCollage(placeName) {
    try {
        const container = document.getElementById('place-live-insights');
        if (!container) return;
        // Rebuild gallery list for the place (uses same logic as render)
        const placeInfo = null; // buildGalleryImages will use live images when hydrated; fallback to guide/theme
        const images = buildGalleryImages(placeName, {});
        if (!images || images.length === 0) {
            alert('No images available to generate.');
            return;
        }

        const pick = images.slice(0, 4);
        const canvas = document.createElement('canvas');
        const w = 1200, h = 800;
        canvas.width = w; canvas.height = h;
        const ctx = canvas.getContext('2d');
        ctx.fillStyle = '#fff'; ctx.fillRect(0, 0, w, h);

        const coords = [
            [0, 0, w / 2, h / 2], [w / 2, 0, w / 2, h / 2], [0, h / 2, w / 2, h / 2], [w / 2, h / 2, w / 2, h / 2]
        ];

        await Promise.all(pick.map((src, idx) => new Promise((resolve) => {
            const img = new Image();
            img.crossOrigin = 'anonymous';
            img.onload = () => {
                const [x, y, ww, hh] = coords[idx];
                // cover draw
                const ar = img.width / img.height;
                const tarAr = ww / hh;
                let dw = ww, dh = hh, dx = x, dy = y;
                if (ar > tarAr) {
                    // image wider -> fit height
                    dh = hh; dw = Math.round(hh * ar); dx = x - Math.round((dw - ww) / 2);
                } else {
                    dw = ww; dh = Math.round(ww / ar); dy = y - Math.round((dh - hh) / 2);
                }
                ctx.drawImage(img, dx, dy, dw, dh);
                resolve();
            };
            img.onerror = () => resolve();
            img.src = src;
        })));

        // Overlay title
        ctx.fillStyle = 'rgba(10,61,98,0.85)';
        ctx.fillRect(0, h - 90, w, 90);
        ctx.fillStyle = '#fff';
        ctx.font = 'bold 36px sans-serif';
        ctx.fillText(placeName, 28, h - 34);

        const dataUrl = canvas.toDataURL('image/jpeg', 0.9);
        const win = window.open();
        if (win) {
            win.document.write(`<title>${escapeHtml(placeName)} - Collage</title><img src="${dataUrl}" style="max-width:100%;height:auto;display:block;margin:0 auto;">`);
        } else {
            // fallback: download
            const a = document.createElement('a');
            a.href = dataUrl;
            a.download = `${placeName.replace(/\s+/g, '_')}_collage.jpg`;
            document.body.appendChild(a); a.click(); a.remove();
        }
    } catch (err) {
        console.error('Collage generation failed', err);
        alert('Could not generate image.');
    }
}

function wireDestinationCards() {
    document.querySelectorAll('.destination-card').forEach(card => {
        if (card.dataset.wired === 'true') return;
        card.dataset.wired = 'true';
        card.setAttribute('tabindex', '0');
        card.setAttribute('role', 'link');
        card.setAttribute('aria-label', `View details for ${card.querySelector('h3')?.textContent || 'destination'}`);

        if (!card.querySelector('.destination-card-link')) {
            const linkSpan = document.createElement('span');
            linkSpan.className = 'destination-card-link';
            linkSpan.textContent = 'View details';
            card.appendChild(linkSpan);
        }

        const clickHandler = () => {
            const placeName = card.querySelector('h3')?.textContent?.trim();
            if (placeName) {
                sessionStorage.setItem('smarttravelSelectedPlace', placeName);
                localStorage.setItem('smarttravelSelectedPlace', placeName);
                const placeUrl = new URL('place.html', window.location.href);
                placeUrl.searchParams.set('place', placeName);
                window.location.href = placeUrl.href;
            }
        };

        card.addEventListener('click', clickHandler);
        card.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                clickHandler();
            }
        });
    });
}

// Destinations filter functionality & click events
document.addEventListener('DOMContentLoaded', () => {
    // Wire destination card clicks
    if (typeof wireDestinationCards === 'function') {
        wireDestinationCards();
    }

    // Wire filter button actions
    const filterBtns = document.querySelectorAll('.filter-btn');
    if (filterBtns.length > 0) {
        filterBtns.forEach(btn => {
            btn.addEventListener('click', function () {
                // Update active button
                document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
                this.classList.add('active');

                // Filter cards
                const filter = this.dataset.filter;
                let visibleCount = 0;
                document.querySelectorAll('.destination-card').forEach(card => {
                    if (filter === 'all' || card.dataset.type === filter) {
                        card.classList.remove('hidden');
                        visibleCount += 1;
                    } else {
                        card.classList.add('hidden');
                    }
                });

                const countEl = document.getElementById('destinationCount');
                if (countEl) countEl.textContent = visibleCount;
            });
        });
    }

    // Hydrate place insights on page load
    if (typeof hydratePlaceInsights === 'function' && document.getElementById('place-live-insights')) {
        hydratePlaceInsights();
    }
});
