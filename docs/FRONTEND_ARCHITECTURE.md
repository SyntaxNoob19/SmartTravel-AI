# Frontend Architecture Documentation - SmartTravel

## 1. Overview

The presentation layer of SmartTravel is designed around simple, high-performance vanilla components (HTML5, CSS3, and JavaScript ES6) to avoid the overhead, build complexities, and dependency issues associated with modern single-page-application (SPA) frameworks.

```
Frontend/
├── assets/                # Images, icons, static assets
├── css/                   # Stylesheet files
│   ├── design-tokens.css  # Unified design variables & reset rules
│   ├── auth.css
│   ├── components.css
│   ├── home.css
│   ├── itinerary.css
│   ├── navbar.css
│   ├── planner.css
│   ├── profile.css
│   └── utilities.css
├── js/                    # Modular JavaScript modules
│   ├── api.js             # Shared fetch calls
│   ├── auth.js            # Authentication handlers
│   ├── config.js          # Global app settings
│   ├── group.js           # Companion expense logs
│   ├── home.js            # Landing dashboard controller
│   ├── itinerary.js       # Saved trip renderer
│   ├── navbar.js          # Navbar HTML loader module
│   ├── planner.js         # Multi-step wizard setup
│   ├── profile.js         # User profile metrics controller
│   └── utils.js           # Toast alerts & formatter utility
├── pages/                 # Sub-page HTML templates
└── index.html             # Application entry point
```

---

## 2. Design Tokens & CSS Styling

- **`design-tokens.css`**: The design system defines core brand attributes using CSS custom properties (`--primary: #2d8a83`, `--bg-light: #f5f7fa`, `--radius-lg: 12px`, `--transition: all 0.3s ...`). This file is pre-linked in all views to avoid styling drift.
- **Glassmorphism Theme:** High-end glassmorphism attributes (`--glass-bg: rgba(255, 255, 255, 0.75)`, `--glass-border: rgba(255, 255, 255, 0.3)`) are applied across landing cards, forms, and trip dashboards.

---

## 3. JavaScript Component Modules

- **API Fetch Abstractor (`api.js`):**
  A central handler exposing asynchronous JS methods (e.g. `api.post('/auth/login', body)`) that communicate with the backend. It preserves Cookie-based credentials automatically using browser default credentials options.
- **Session Auth Monitor (`auth.js`):**
  Controls the user register and login processes. Redirects users to index dashboard upon authentication, and handles cleanup upon logging out.
- **Planner Wizard (`planner.js`):**
  Implements a step-by-step form wizard mapping user selections (destination, days, budget tier, traveler count, weather type, and interests). Submits the form data to backend APIs.
- **Itinerary Renderer (`itinerary.js`):**
  Renders the day-wise itinerary layout dynamically, showing location metrics, places details, significance descriptions, safety logs, and local recommendations. Initializes map pointers.
- **Navbar Loader (`navbar.js`):**
  Asynchronously fetches and loads [navbar.html](file:///d:/travel-planner/Frontend/components/navbar.html) into views, dynamically updating navigation items (e.g. replacing Login/Register with Logout and Profile links) based on session presence.
