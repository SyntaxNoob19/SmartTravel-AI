# System Architecture & Database Design Specification

This document details the architectural layout, core design patterns, database schemas, and identified technical debt for the SmartTravel application.

---

## 1. System Overview

SmartTravel is structured as a classic 3-tier web application designed to separate concerns between the presentation layer, the application logic, and the persistent storage.

*   **Presentation Layer (Frontend):** Vanilla HTML5, CSS3, and modern JavaScript (ES6) views served directly by the server or decoupled simple HTTP servers.
*   **Application Layer (Backend):** Spring Boot 3.x backend exposing REST web services, executing the Retrieval-Augmented Generation (RAG) planning pipeline, and administering security filters.
*   **Storage Layer (Database):** MySQL database schemas mapping user profiles, itineraries, and expense accounts, alongside a local CSV travel spots dataset.

---

## 2. Layered Architecture Specifications

### 2.1 Presentation Layer (Frontend)
The frontend relies on lightweight, high-performance vanilla components to eliminate frame-compilation overhead and external JS package vulnerabilities.
*   **Design Tokens System (`Frontend/css/design-tokens.css`):** The master stylesheet defining all root variables (colors, spacing scales, typography weight, border radii, shadows) to guarantee UI uniformity across views.
*   **Modular Stylesheets:** Feature-specific layout sheets (e.g. `auth.css`, `planner.css`, `itinerary.css`) that inherit properties defined in `design-tokens.css`.
*   **API Client Wrapper (`Frontend/js/api.js`):** A centralized asynchronous wrapper module for HTTP operations (POST, GET, DELETE) that manages Cookie-based credential transmission.
*   **Dynamic Component Loader (`Frontend/js/navbar.js`):** Asynchronously injects navbar layouts and handles navigation link states dynamically based on user session status.
*   **Page Controllers:** Modular scripts (e.g., `planner.js`, `itinerary.js`, `auth.js`) mapping DOM nodes to API responses and coordinating the client-side lifecycle.

### 2.2 Application Layer (Spring Boot Backend)
The Java backend employs standard enterprise packaging guidelines:
*   **`config`:** Houses web resource routing maps, CORS authorization registries, and Spring MVC dispatcher setups.
*   **`controller`:** Registers REST endpoints (mapped under `/api/*`), validates incoming payloads, and delegates processing to service handlers.
*   **`dto`:** Data Transfer Objects mapping request contracts and formatting structured response payloads.
*   **`entity`:** Persistent JPA models annotated with Hibernate metadata mappings.
*   **`repository`:** Data Access interfaces implementing Spring Data `JpaRepository` to abstract database interactions.
*   **`security`:** Implements a stateful session interception layer (`SessionAuthFilter`) to authenticate requests via `HttpSession` and encrypt credentials using BCrypt.
*   **`service`:** Core business implementations, including RAG context assembly, OpenRouter client calls, and group budgeting split-share calculators.
*   **`util`:** Parsers, input validators, and database ingestion loaders.

---

## 3. Core Generative & Fallback Pipelines

### 3.1 Retrieval-Augmented Generation (RAG) Pipeline
SmartTravel incorporates database queries with generative language models to guarantee accurate, real-world travel suggestions.
1.  **Retrieve Candidates:** When an itinerary request is received, the backend queries the database (loaded from the CSV travel dataset) using filters such as city, budget level, and rating.
2.  **Sparse Expansion:** If candidate count is less than `days * 2`, the query dynamically expands to retrieve additional tourist spots from the same state or region.
3.  **Context Construction:** The system parses candidate details (descriptions, ratings, tips) and structures them into a markdown text context block.
4.  **Prompt Composition:** Inserts the context block into a strict system instruction template specifying the output JSON schema.
5.  **LLM Call:** Dispatches an HTTP request to the OpenRouter API (`gpt-4o-mini`). The resulting JSON is parsed directly to a structured `PlannerResponseDto`.

### 3.2 Proximity-Based Fallback Clustering
If the OpenRouter endpoint is unreachable, the system executes a deterministic fallback algorithm:
1.  **Centroid Calculation:** Identifies the geometric centroid (average latitude and longitude) of all retrieved places.
2.  **Haversine Distance Scoring:** Measures the geographic distance between candidates using the Haversine formula:
    $$d = 2R \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta\phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta\lambda}{2}\right)}\right)$$
3.  **Priority Sorting:** Orders the candidates based on priority tags (Must Visit, Recommended, Optional) combined with distance penalty values.
4.  **Time-Window Allocation:** Groups places sequentially into daily plans, ensuring total daily activities do not exceed the `maxHoursPerDay` constraint.

---

## 4. Relational Database Design

### 4.1 Schema Overview
```
+------------+          1:N          +-------------+
|   users    |----------------------<| saved_trips |
+------------+                       +-------------+
      |
      | 1:N
      v
+-------------+
| budget_plan |
+-------------+
      |
      |-- 1:N (Element Collection) --> [ budget_members ]
      |
      +-- 1:N (Element Collection) --> [ budget_expenses ]
```

### 4.2 Database Tables Specification

#### Table: `users`
Tracks registered travelers credentials.
*   `id` (`BIGINT`, PK, Auto-Increment)
*   `name` (`VARCHAR(255)`, Not Null)
*   `email` (`VARCHAR(255)`, Not Null, Unique)
*   `password_hash` (`VARCHAR(255)`, Not Null)
*   `created_at` (`TIMESTAMP`, Not Null)

#### Table: `saved_trips`
Persists generated itinerary plans.
*   `id` (`BIGINT`, PK, Auto-Increment)
*   `user_id` (`BIGINT`, Not Null, FK pointing to `users.id`)
*   `user_email` (`VARCHAR(255)`, Not Null)
*   `trip_name` (`VARCHAR(255)`, Not Null)
*   `destination` (`VARCHAR(255)`, Not Null)
*   `planner_request_json` (`LONGTEXT`, Not Null)
*   `planner_response_json` (`LONGTEXT`, Not Null)
*   `created_at` (`TIMESTAMP`, Not Null)
*   `updated_at` (`TIMESTAMP`, Not Null)

#### Table: `places`
Static dataset of tourist attractions.
*   `place_id` (`VARCHAR(20)`, PK)
*   `place_name` (`VARCHAR(255)`, Not Null)
*   `city` (`VARCHAR(120)`)
*   `state` (`VARCHAR(120)`)
*   `region` (`VARCHAR(50)`)
*   `place_type` (`VARCHAR(100)`)
*   `category` (`VARCHAR(100)`)
*   `description` (`TEXT`)
*   `best_time_to_visit` (`VARCHAR(100)`)
*   `recommended_duration_hours` (`DOUBLE`)
*   `rating` (`DOUBLE`)
*   `crowd_level` (`VARCHAR(50)`)
*   `family_friendly` (`BOOLEAN`)
*   `latitude` (`DOUBLE`)
*   `longitude` (`DOUBLE`)
*   `local_tips` (`TEXT`)
*   `weather_type` (`VARCHAR(50)`)

#### Table: `budget_plan`
Stores shared budgeting plans.
*   `id` (`BIGINT`, PK, Auto-Increment)
*   `user_id` (`BIGINT`, Not Null, FK pointing to `users.id`)
*   `total_amount` (`DOUBLE`)
*   `participants` (`INT`)
*   `created_at` (`TIMESTAMP`)
*   `updated_at` (`TIMESTAMP`)

#### Table: `budget_members` (Element Collection)
*   `budget_id` (`BIGINT`, FK pointing to `budget_plan.id`)
*   `member_name` (`VARCHAR(255)`)

#### Table: `budget_expenses` (Element Collection)
*   `budget_id` (`BIGINT`, FK pointing to `budget_plan.id`)
*   `id` (`BIGINT`)
*   `name` (`VARCHAR(255)`): Name of the member who paid.
*   `amount` (`DOUBLE`)
*   `date` (`VARCHAR(255)`)
*   `description` (`VARCHAR(255)`)

---

## 5. Technical Debt & Identified Optimizations

*   **Global Javascript Bindings:** Frontend modules are loaded globally via HTML `<script>` tags, exposing variables to the global window scope. *Recommendation:* Transition to native ES6 import/export modules to isolate variables and control execution.
*   **Synchronous Controller Operations:** Third-party APIs (weather and images) are called synchronously during HTTP request mapping. *Recommendation:* Implement asynchronous handling using Spring `WebClient` or retrieve non-critical parameters via secondary AJAX fetches from the client.
*   **Memory-Bound Caching:** Current simple in-memory caching will not persist across distributed deployments. *Recommendation:* Transition caching targets (weather, image configurations) to an external key-value store (e.g. Redis) and set explicit Time-To-Live (TTL) strategies.
*   **Local Session Storage:** Session management relies on stateful `HttpSession` memory in Tomcat. *Recommendation:* Convert authorization filters to utilize stateless tokens (JWT) or persistent backend session replication.
