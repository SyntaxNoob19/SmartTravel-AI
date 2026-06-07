# Backend Architecture Documentation - SmartTravel

## 1. Directory & Service Architecture

The Spring Boot backend is divided into clear logical layers:

```
com.riya.smarttravel/
├── config/       # Web configurations, MVC resources, CORS permissions
├── controller/   # RestControllers mapping requests to business services
├── dto/          # Serialization schemas and payload contracts
├── entity/       # JPA entity definitions
├── exception/    # Custom exception classes & global advice handler
├── repository/   # Database query layer definitions
├── security/     # Session authentication filters & cryptography
├── service/      # Core business implementations (RAG prompt, clustering)
└── util/         # CSV loaders and sanitizers
```

---

## 2. Session Security Architecture

SmartTravel implements a stateful HTTP Session authentication model:
- **`SessionAuthFilter`**: A standard request filter intercepting REST paths under `/api/trips/**`, `/api/users/**`, `/api/budget/**`, `/api/profile/**`. It verifies if the custom attribute `authenticatedUserEmail` is active in `HttpSession`.
- **Authentication Context:** If active, it populates a `UsernamePasswordAuthenticationToken` into the thread-local `SecurityContextHolder`, granting request authorization.
- **Password Encryption:** Managed using `org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder`.

---

## 3. The RAG Prompt-Enrichment Pipeline

When a user submits a travel request to `/api/planner/generate`, the `PlannerService` initiates the RAG enrichment process:

```
User Query ──> [ Query Database Candidates (CSV Seeded) ]
                     │
                     v
             [ Retrieve Place Profiles & Categories ]
                     │
                     v
             [ Construct Detailed Prompt Context ] ──> [ Call OpenRouter API ]
                                                              │
                                                              v
                                                     [ User Itinerary JSON ]
```

1. **Context Querying:** Backend calls `smartFilter` to find matching tourist locations based on city, budget, and rating constraints.
2. **Sparse Expansion:** If the query returns fewer candidates than `days * 2`, it retrieves additional neighboring spots in the same state/region.
3. **Context Compiler:** The place names, categories, local tips, and descriptions of these candidates are formatted into a markdown layout.
4. **Prompt Assembly:** The compiled text is injected into a system instructions template containing output schemas and safety guidelines.
5. **API Dispatch:** System issues an okHttp POST call to OpenRouter's `/chat/completions` REST endpoint.

---

## 4. Proximity-Based Clustering & Fallback Algorithm

If the OpenRouter service is disabled or fails, the backend runs a **Rule-Based Fallback** to cluster attractions into a balanced itinerary using geographic coordinates:

1. **Centroid Anchor:** Calculates the geometric centroid (average latitude/longitude) of all place candidates.
2. **Proximity Scoring:** For each candidate, computes the distance to the centroid or the previously selected attraction using the Haversine formula:
   $$d = 2R \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta\phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta\lambda}{2}\right)}\right)$$
3. **Sort & Pick:** Sorts candidates by a combination of priority levels (Must Visit > Recommended > Optional), ratings, and proximity distance penalty weights.
4. **Day Allocation:** Fills daily plans by tracking daily visit hours (recommended duration + travel transfer estimates) up to the specified `maxHoursPerDay` constraint.
