# Architecture Overview

## Frontend Structure
```
Frontend/
├── assets/                # Images, icons, static assets
├── css/                   # Modular stylesheet files
│   ├── utilities.css
│   ├── components.css
│   ├── home.css
│   ├── planner.css
│   └── itinerary.css
├── js/                    # Modular JavaScript modules
│   ├── api.js            # API endpoint definitions & fetch wrappers
│   ├── auth.js           # Login, registration, session handling
│   ├── home.js           # Home page interactions
│   ├── planner.js        # Multi‑step planner wizard logic
│   ├── itinerary.js      # Rendering saved trip details
│   └── utils.js          # Shared utilities (formatting, toast notifications)
├── pages/                # Individual HTML pages
│   ├── index.html
│   ├── about.html
│   ├── planner.html
│   ├── itinerary.html
│   ├── destinations.html
│   ├── group.html
│   ├── login.html
│   ├── register.html
│   └── ...
└── index.html            # Landing page that loads the CSS/JS bundles
```

## Backend Structure
```
Backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/riya/smarttravel/
│   │   │   ├── config/            # WebConfig, CORS, static resource mapping
│   │   │   ├── controller/       # REST controllers (Auth, Planner, Trip, Profile)
│   │   │   ├── dto/              # Request/response DTOs
│   │   │   ├── entity/           # JPA entity classes (User, Trip, Place)
│   │   │   ├── repository/       # Spring Data JPA repositories
│   │   │   ├── service/          # Business logic layer
│   │   │   └── util/             # Helper utilities (CSV import, AI client)
│   │   └── resources/
│   │       └── application.properties
│   └── test/ ...
```

## Package Explanation
- **config** – Spring configuration beans (CORS, static resource handler, security).
- **controller** – Handles HTTP requests, validates input, delegates to services.
- **dto** – Plain objects used for JSON serialization/deserialization, keeping entities separate from API contracts.
- **entity** – JPA‑mapped classes that represent database tables (`users`, `trips`, `places`).
- **repository** – Spring Data interfaces providing CRUD operations.
- **service** – Core business rules (authentication, itinerary generation, budget calculations).
- **util** – Misc utilities such as CSV import, external API clients (OpenRouter, Nominatim, Unsplash).

## API Communication
The frontend communicates with the backend via **REST** endpoints under the `/api` namespace. All requests are JSON‑encoded and use the `api.js` module to abstract `fetch` calls.
- **Authentication** – `POST /api/auth/register` and `POST /api/auth/login` return a JWT stored in `localStorage`. The token is added to the `Authorization: Bearer <token>` header for protected calls.
- **Planner** – `POST /api/planner/generate` receives user preferences and returns an itinerary payload.
- **Saved Trips** – `POST /api/trips/users/{email}` stores a trip; `GET /api/trips/users/{email}` fetches all trips for the logged‑in user.
- **Profile** – `GET /api/users/{email}/profile` returns user details and summary data.

## Database Relationships
```
User (1) ────< (many) Trip
Trip (many) ────< (many) TripPlace
Place (1) ────< (many) TripPlace
```
- **User** has many **Trip** records (one‑to‑many).
- **Trip** contains an ordered list of **TripPlace** entries, each linking a **Place** with day, order, and optional notes.
- **Place** stores static location data (name, type, latitude, longitude, image URL) loaded from the CSV seed file.

## Interaction Flow
1. Frontend loads `index.html` → CSS/JS bundles.
2. User logs in → JWT stored.
3. Planner form posts to `/api/planner/generate`.
4. Backend creates itinerary, optionally augments with AI, returns JSON.
5. Frontend renders UI, offers **Save Trip** → POST `/api/trips/...`.
6. Saved trips displayed via GET `/api/trips/...`.

---
*This file provides a concise reference for developers navigating the codebase.*
