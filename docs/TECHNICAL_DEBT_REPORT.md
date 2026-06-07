# Technical Debt & Optimization Report - SmartTravel

This document outlines the architectural trade-offs, technical debt, performance limitations, and recommended optimizations for the SmartTravel application.

---

## 1. Identified Technical Debt & Bottlenecks

### 1.1 Global Scope JavaScript Dependency (Presentation Layer)
- **Issue:** Frontend JS files (`api.js`, `auth.js`, `planner.js`, `itinerary.js`) are structured as global scripts, registering functions directly on the browser's global `window` object. This makes execution dependent on their sequential loading order inside HTML tags.
- **Risk:** Namespace pollution, potential script conflicts, and lack of scoping.
- **Mitigation:** Refactor code to leverage native ES6 modules (`import` / `export` syntax) and wrap pages controllers inside distinct scopes.

### 1.2 Synchronous Third-Party API Calls (Application Layer)
- **Issue:** External API requests for weather (Open-Meteo), city images (Unsplash), and geocoding (Nominatim) are performed synchronously on the backend controller execution threads using OkHttp blocks.
- **Risk:** If an external API is slow or times out, the backend thread remains blocked, decreasing system throughput and increasing API response latency.
- **Mitigation:**
  1. Leverage `CompletableFuture` or WebClient in Spring Boot to call external services asynchronously.
  2. Alternatively, perform these external calls directly from the frontend using AJAX fetches to offload backend server execution threads.

### 1.3 Stateful HTTP Session Persistence
- **Issue:** Authentication status is saved inside standard Servlet `HttpSession` memory blocks, requiring the backend instance to store user login state locally.
- **Risk:** Blocks horizontal scalability of the backend server since sessions are not shared across multiple servers without session replication.
- **Mitigation:** Transition to stateless authentication tokens (e.g. JWT) or configure a shared external session store (e.g., Spring Session with Redis).

### 1.4 Simple In-Memory JVM Caching
- **Issue:** SmartTravel uses standard `simple` in-memory caching for geocoding and weather API responses.
- **Risk:** Memory utilization issues under load and lack of cache invalidation across distributed instances.
- **Mitigation:** Replace the simple caching provider with a production-grade distributed store (e.g. Redis) or set explicit TTL configs using Caffeine.

---

## 2. Refactoring Summary (Completed Steps)

### 2.1 CSS Duplications & Tokenization
- **Before:** `:root` variables were duplicated across three stylesheets (`utilities.css`, `itinerary.css`, `planner.css`), resulting in CSS design drift.
- **After:** Created `design-tokens.css` containing all variables, resolving duplication and ensuring unified styling tokens.

### 2.2 Dynamic Asset Mappings
- **Before:** Backend WebConfig mapped the external static directory using a hardcoded local absolute path (`file:///d:/travel-planner/Frontend/`).
- **After:** Parametrizable property `app.frontend.path` injected via Spring properties, allowing portability on CI/CD pipelines.
