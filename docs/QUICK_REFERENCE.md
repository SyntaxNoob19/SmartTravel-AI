# Quick Reference Guide

## Frontend Structure & Key Files

### JavaScript Files
```
Frontend/js/
├── api.js              # API endpoints, fetch wrappers, fetchItinerary()
├── auth.js             # Login/register/session handling
├── config.js           # API endpoints configuration
├── planner.js          # Multi-step planner form logic, buildPlannerRequest()
├── itinerary.js        # Trip display, renderItinerary(), renderBudgetPanel()
└── utils.js            # Utilities: budget calculations, formatters, hash functions
```

### CSS Files
```
Frontend/css/
├── utilities.css       # Global utilities (colors, spacing, typography)
├── components.css      # Reusable components
├── home.css            # Home page styles
├── planner.css         # Planner form styles
└── itinerary.css       # Trip detail page styles (RECENTLY UPDATED)
```

### HTML Pages
```
Frontend/pages/
├── index.html          # Landing page
├── planner.html        # Trip planner form (5 steps)
├── trip-detail.html    # Trip itinerary display (with budget breakdown)
├── itinerary.html      # Saved trips list
└── ... (other pages)
```

---

## Budget Display Components

### Daily Rate Banner
**File:** `Frontend/css/itinerary.css` (lines 1372-1395)
**Class:** `.trip-budget-daily-rate-banner`
**Display:** Large, prominent, teal gradient background

### Budget Metadata Strip
**File:** `Frontend/js/itinerary.js` (lines 1237-1253)
**Classes:** `.trip-budget-meta-strip`, `.trip-budget-meta-item`
**Content:** Duration | Travelers | **Estimated Total**

### Category Breakdown
**File:** `Frontend/js/itinerary.js` (lines 1261-1268)
**Classes:** `.trip-budget-breakdown-card`
**Categories:** Hotel 40%, Food 18%, Transport 20%, Activities 22%

---

## Itinerary Display Components

### Day-by-Day Rendering
**File:** `Frontend/js/itinerary.js` → `renderPlannerDay()` (lines 730-760)
**Layout:** Grid of place items with 16px gap
**Content per place:** Name, time, description, tags (category, rating, duration, tips, safety)

### Place Item Structure
```
<div class="itinerary-place-item">
  <div class="itinerary-place-top">
    <strong>1. Place Name</strong>
    <span>Time Slot</span>
  </div>
  <p>Description</p>
  <div class="itinerary-place-tags">
    <span>Category</span>
    <span>Rating</span>
    <span>Duration</span>
    <span>Tips</span>
    <span>Safety</span>
  </div>
</div>
```

---

## Key Functions & Their Locations

### Frontend (JavaScript)

| Function | File | Purpose |
|----------|------|---------|
| `buildPlannerRequest()` | planner.js | Collects form data into request object |
| `fetchItinerary()` | api.js | Sends POST to backend, handles response |
| `openTripDetailPage()` | api.js | Opens trip-detail.html with data |
| `renderItinerary()` | itinerary.js | Main render function for trip detail page |
| `renderBudgetPanel()` | itinerary.js | Renders budget section with breakdown |
| `renderPlannerDay()` | itinerary.js | Renders each day with all places |
| `computeBudgetBreakdown()` | utils.js | Calculates budget (client-side) |
| `mapBudgetLevelToDaily()` | utils.js | Returns daily rate for tier |
| `getBudgetPreferenceRange()` | utils.js | Returns tier min/max/average |

### Backend (Java/Spring Boot)

| Class | Purpose |
|-------|---------|
| `PlannerController` | Receives `/api/planner/generate` requests |
| `PlannerService` | Orchestrates itinerary generation |
| `PlaceRepository` | Queries place database |
| `PlannerAiService` | Calls OpenRouter API for AI generation |
| `PlannerResponseDto` | Response object with itinerary data |

---

## Data Flow: Planner → Itinerary

```
1. User fills planner.html form (5 steps)
   ↓
2. buildPlannerRequest() creates request object
   ↓
3. fetchItinerary() POSTs to /api/planner/generate
   ↓
4. Backend: PlannerService.generate() processes request
   ↓
5. Returns PlannerResponseDto with itinerary
   ↓
6. openTripDetailPage() stores data and opens trip-detail.html
   ↓
7. renderItinerary() renders full page
   ├── renderBudgetPanel() - Budget section
   └── renderPlannerDay() - Each day with places
   ↓
8. User sees complete trip with budget & daily schedule
```

---

## Budget Calculation Example

**Scenario:** Delhi, 4 travelers, 3 days, Comfort tier

```javascript
// Backend calculates:
dailyRate = 2,800 (deterministic, based on destination hash)
totalPerDay = 2,800 × 4 = 11,200
totalTrip = 11,200 × 3 = 33,600

// Category breakdown:
hotel = Math.round(33,600 × 0.40) = 13,440
food = Math.round(33,600 × 0.18) = 6,048
transport = Math.round(33,600 × 0.20) = 6,720
activities = 33,600 - 13,440 - 6,048 - 6,720 = 7,392
// Sum check: 13,440 + 6,048 + 6,720 + 7,392 = 33,600 ✓

// Frontend displays:
Daily Rate: ₹2,800
Total: ₹33,600
Hotel: ₹13,440 (40%)
Food: ₹6,048 (18%)
Transport: ₹6,720 (20%)
Activities: ₹7,392 (22%)
```

---

## Common CSS Classes

### Budget Display
```css
.trip-budget-shell                    /* Main container */
.trip-budget-daily-rate-banner        /* Daily rate banner (NEW) */
.trip-budget-daily-rate-label         /* Label text (NEW) */
.trip-budget-daily-rate-value         /* Amount text (NEW) */
.trip-budget-meta-strip               /* Duration/Travelers/Total row */
.trip-budget-meta-item                /* Individual metadata item */
.trip-budget-breakdown-card           /* Category card */
.budget-breakdown-hotel               /* Hotel card (green) */
.budget-breakdown-food                /* Food card (orange) */
.budget-breakdown-transport           /* Transport card (blue) */
.budget-breakdown-activities          /* Activities card (purple) */
```

### Itinerary Display
```css
.itinerary-day-card                   /* Day container */
.itinerary-day-head                   /* Day header */
.itinerary-place-item                 /* Place item in day */
.itinerary-place-top                  /* Place name + time */
.itinerary-place-tags                 /* Category/rating/tips */
.itinerary-place-tags span            /* Individual tag */
```

---

## Common DOM IDs

### Planner Form
```html
#destinationCity          <!-- Known destination input -->
#branchKnown              <!-- Branch A: Known city -->
#branchUnknown            <!-- Branch B: Region selection -->
#days                     <!-- Duration select -->
#budgetLevel              <!-- Budget tier select -->
#groupSize                <!-- Group size (if family/friends) -->
#season                   <!-- Season select -->
#festival                 <!-- Festival select -->
#submitBtn                <!-- "Create My Perfect Itinerary" button -->
```

### Trip Detail Page
```html
#itinerary-output         <!-- Main container -->
#tripDetailTitle          <!-- Destination title -->
#tripDetailSubtitle       <!-- Tagline -->
#itinerary-weather-badge  <!-- Weather info -->
```

---

## API Endpoints

### Planner
```
POST /api/planner/generate
Content-Type: application/json

Request:
{
  travellerType: "family",
  groupSize: 4,
  city: "Jaipur",
  days: 3,
  budgetLevel: "midrange",
  category: "cultural",
  mood: "explorative,romantic",
  enhanceWithAi: true
}

Response:
{
  success: true,
  data: {
    generatedDays: 3,
    totalPlaces: 9,
    totalBudget: 33600,
    dataSource: "DATABASE",
    itinerary: [...],
    budget: {...}
  }
}
```

### Trips (Save/Fetch)
```
POST /api/trips/users/{email}       <!-- Save trip -->
GET /api/trips/users/{email}        <!-- Get all trips -->
GET /api/trips/{tripId}             <!-- Get single trip -->
DELETE /api/trips/{tripId}          <!-- Delete trip -->
```

---

## Environment Variables

```bash
OPENROUTER_API_KEY          # Required for AI itinerary generation
MYSQL_DATABASE=smarttravel  # Database name
MYSQL_USERNAME=root         # MySQL user
MYSQL_PASSWORD=password     # MySQL password
SPRING_PORT=9090            # Backend port (default)
```

---

## Development Tips

### Running Tests
```bash
cd Backend
./mvnw.cmd test
```

### Building Frontend
```bash
# No build step required - vanilla JS/HTML/CSS
# Just open Frontend/index.html or use local server
```

### Debug Mode
1. Open DevTools (F12)
2. Check Console for errors
3. Check Application tab for localStorage data
4. Check Network tab for API calls

### Useful Console Commands
```javascript
// View current itinerary data
JSON.parse(localStorage.getItem('itineraryData'))

// View planner request
JSON.parse(localStorage.getItem('plannerRequestData'))

// Clear all app data
localStorage.clear(); sessionStorage.clear();
```

---

## Responsive Breakpoints

| Device | Breakpoint | Changes |
|--------|-----------|---------|
| Desktop | 1200px+ | 3-column layouts, full features |
| Tablet | 768px-1199px | 2-column layouts |
| Mobile | <520px | 1-column layouts, stacked budget items |

### Key Media Queries
```css
@media (max-width: 1100px) { /* Tablet */ }
@media (max-width: 768px)  { /* Small tablet */ }
@media (max-width: 520px)  { /* Mobile */ }
```

---

## Troubleshooting

### Budget not showing
- Check localStorage: `localStorage.getItem('itineraryData')`
- Verify backend returned `budget` object
- Check browser console for errors

### Places not expanding
- Ensure `itinerary` array has valid place objects
- Check that places have `placeName` property
- Verify CSS grid layout not hidden by parent

### Page not loading
- Clear cache: Ctrl+Shift+Del
- Check console for 404 errors
- Verify backend is running on port 9090

### Mobile view issues
- Check if viewport meta tag present in HTML
- Verify media queries in CSS
- Test with DevTools device emulation

---

## Code Style Guidelines

### JavaScript
- Use `const` by default, `let` for reassignment
- Use arrow functions `() => {}`
- Use template literals `` `text ${var}` ``
- Escape HTML with `escapeHtml()` utility function

### CSS
- Use CSS custom properties for colors
- Use flexbox/grid for layouts
- Mobile-first approach (base styles, then @media)
- BEM naming convention when possible

### HTML
- Use semantic tags (`<section>`, `<article>`, `<nav>`)
- Use `aria-` attributes for accessibility
- Include `data-*` attributes for JavaScript selectors

---

## Resources

- [Frontend Improvements Doc](FRONTEND_IMPROVEMENTS.md) - Detailed UI/UX changes
- [API Reference](API_REFERENCE.md) - Complete API documentation
- [Architecture](ARCHITECTURE.md) - System design overview
- [Project Setup](PROJECT_SETUP.md) - Installation & configuration

---

*Last Updated: June 6, 2025*
