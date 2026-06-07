# Software Requirements Specification (SRS) - SmartTravel

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) document provides a detailed description of the system goals, functional capabilities, interface designs, and non-functional behavior of the **SmartTravel** application. It serves as the primary technical specification for developers, deployment engineers, and stakeholders.

### 1.2 Scope
SmartTravel is an AI-powered travel itinerary generation platform designed specifically for travel within India. It combines local travel database search (places dataset query) with generative language modeling (via OpenRouter API) to generate detailed, contextual, multi-day itineraries. Users can create, customize, and save itineraries to their profile, view live weather forecasts, inspect map coordinates, and split travel expenses with companions.

---

## 2. System Goals & Objectives
- **Generative Travel Planning:** Dynamically construct custom schedules optimized by region, city, budget level, traveler profiles, and mood settings.
- **RAG-Inspired Architecture:** Ground AI-generated plans in real historical data from a curated dataset containing detailed local insights for Indian destinations.
- **User Management & Profile Preservation:** Secure registration, authentication session handling, and profile trip tracking.
- **Robust Local Offline Fallbacks:** Offer a structured rule-based local backup algorithm that generates functional multi-day itineraries if the external AI service times out or is not configured.
- **Dynamic Companion Expenses Tracking:** Allow users to define group budgets, add travel expenses, and automatically calculate companion split-shares.

---

## 3. User Characteristics & Personas
- **Solo Explorer:** Focuses on safety metrics, budget-friendly options, and highly active sightseeing slots.
- **Couples / Friends:** Prefers scenic spots, local dining, late start hours, and shared expense splitting.
- **Families / Large Groups:** Demands family-friendly spaces, shorter durations, low crowd densities, and pre-planned group transport suggestions.

---

## 4. Functional Requirements

### 4.1 Account & Session Management
- **UC-01 Registration:** Users can register an account with a unique email address, name, and password.
- **UC-02 Login Session:** Users can log in. The backend establishes a secure, HTTP-only cookie-based session tracker (`JSESSIONID`).
- **UC-03 Logout Session:** Users can clear active session cookies and invalidate authentication tokens.

### 4.2 Interactive Planner Engine
- **UC-04 Preference Selection:** Users specify destination city, trip length (1 to 14 days), budget levels, traveler profile, and mood preferences.
- **UC-05 AI Generation:** System attempts to generate a structured AI itinerary through the OpenRouter API.
- **UC-06 Rule-Based Database Fallback:** If the AI API is unavailable, the backend automatically queries the local database, groups nearby locations, and serves a fallback plan.

### 4.3 Trip Tracking & Exploration
- **UC-07 Destinations Exploration:** Users can browse destinations by categories (beaches, mountains, cities, adventure, spiritual, national parks).
- **UC-08 Itinerary Saving:** Authenticated users can save generated plans to their user account.
- **UC-09 Saved Trips Panel:** Users can inspect, detail-view, or delete past saved plans from their dashboard.

### 4.4 Companion Budgeting & Splitting
- **UC-10 Budget Plans:** Users can create custom trip budgets and associate them with their profile.
- **UC-11 Expense Entries:** Track individual expenses, specifying who paid, descriptions, and amounts.
- **UC-12 Companion Balance Calculator:** Dynamically compute who owes whom to simplify expenses reconciliation.

---

## 5. Non-Functional Requirements

### 5.1 Performance & Reliability
- System must serve static pages in less than 500ms under ordinary conditions.
- AI API timeouts must be handled gracefully within 30 seconds, falling back automatically to local database itinerary generation.

### 5.2 Security & Data Privacy
- Encrypted password storage using BCrypt hashing algorithms.
- Custom security validation filters to block unauthorized access to protected endpoints (`/api/trips/**`, `/api/users/**`, `/api/budgets/**`).

### 5.3 Portability & Extensibility
- Dynamic resolution of static assets from configured file system pathways.
- Parametrizable configurations for CORS profiles and external API endpoints.
