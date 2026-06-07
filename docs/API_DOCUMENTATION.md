# REST API Reference Documentation - SmartTravel

This document specifies the REST API endpoints exposed by the SmartTravel backend, including parameters, payloads, response formats, and session requirements.

Base URL: `http://localhost:9090` (Local Development)

---

## 1. Authentication Endpoints (`/api/auth`)

SmartTravel uses stateful **Session-Based Authentication**. Upon successful login or registration, the server attaches a `JSESSIONID` session cookie to the response. Clients must include this cookie in subsequent requests.

### 1.1 User Registration
- **URL:** `/api/auth/register`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "password": "strongPassword123"
  }
  ```
- **Response:** `200 OK`
  ```json
  {
    "success": true,
    "message": "Account created successfully",
    "count": 1,
    "data": {
      "id": 1,
      "name": "Jane Doe",
      "email": "jane@example.com",
      "createdAt": "2026-06-07T15:25:00"
    },
    "timestamp": "2026-06-07T15:25:00.123"
  }
  ```

### 1.2 User Login
- **URL:** `/api/auth/login`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "email": "jane@example.com",
    "password": "strongPassword123"
  }
  ```
- **Response:** `200 OK` (includes session cookie `JSESSIONID`)

### 1.3 Fetch Current Authenticated User
- **URL:** `/api/auth/me`
- **Method:** `GET`
- **Session Required:** Yes
- **Response:** `200 OK` with user details.

### 1.4 User Logout
- **URL:** `/api/auth/logout`
- **Method:** `POST`
- **Session Required:** Yes
- **Response:** `244 No Content` (session invalidated).

---

## 2. Travel Planner Controller (`/api/planner`)

Manages the core AI RAG generative itinerary pipeline.

### 2.1 Generate Itinerary
- **URL:** `/api/planner/generate`
- **Method:** `POST`
- **Session Required:** No (Open/Protected via optional session check depending on configuration; standard setup allows generation without active session, but user must login to save it).
- **Request Body:**
  ```json
  {
    "city": "Goa",
    "region": "South",
    "days": 4,
    "travellerType": "SOLO",
    "minRating": 4.0,
    "maxHoursPerDay": 8.0,
    "budgetLevel": "Medium",
    "preferences": "beach, nature, food",
    "enhanceWithAi": true
  }
  ```
- **Response:** `200 OK`
  ```json
  {
    "success": true,
    "message": "Itinerary generated successfully",
    "count": 1,
    "data": {
      "requestedDays": 4,
      "generatedDays": 4,
      "totalPlaces": 12,
      "travellerType": "SOLO",
      "dataSource": "AI_GENERATED",
      "maxHoursPerDay": 8.0,
      "totalTripHours": 24.5,
      "summary": "A beach and nature focused trip...",
      "budgetAdvice": "Balance paid attractions with public transit...",
      "generalSafetyTips": "Share daily plans with a contact...",
      "itinerary": [
        {
          "dayNumber": 1,
          "daySummary": "Beaches exploration in Goa",
          "location": { "city": "Goa", "state": "Goa" },
          "places": [
            {
              "placeId": "IND045",
              "placeName": "Calangute Beach",
              "category": "Beach",
              "description": "A very scenic sand strip...",
              "rating": 4.6
            }
          ]
        }
      ]
    }
  }
  ```

---

## 3. Saved Trips Controller (`/api/trips`)

Manages saving, retrieving, and deleting generated itineraries.

### 3.1 Save Trip for User
- **URL:** `/api/trips/users/{email}`
- **Method:** `POST`
- **Session Required:** Yes
- **Request Body:** Contains the generated trip metadata and day-wise details.
- **Response:** `200 OK`

### 3.2 List All Trips for User
- **URL:** `/api/trips/users/{email}`
- **Method:** `GET`
- **Session Required:** Yes
- **Response:** `200 OK` with array of saved trip metadata.

### 3.3 Delete Saved Trip
- **URL:** `/api/trips/{id}?email={user_email}`
- **Method:** `DELETE`
- **Session Required:** Yes
- **Response:** `200 OK`

---

## 4. Explore Controller (`/api/explore`)

Endpoints for browsing static places, filtering, and searching.

### 4.1 Filter Places
- **URL:** `/api/explore/filter`
- **Method:** `GET`
- **Query Parameters:**
  - `region`: `North`, `South`, `East`, `West`, etc.
  - `category`: `Heritage`, `Spiritual`, `Beach`, `Nature`, `Adventure`, etc.
  - `mood`: `relaxed`, `active`, `historical`, etc.
  - `budgetLevel`: `Low`, `Medium`, `High`
  - `minRating`: `4.0` (Double)
  - `familyFriendly`: `true` / `false` (Boolean)
- **Response:** `200 OK` with list of place details.

### 4.2 Search Places
- **URL:** `/api/explore/search?query={search_term}`
- **Method:** `GET`
- **Response:** `200 OK` with matching places.

---

## 5. Companion Budget Controller (`/api/budget`)

Reconciles shared companion travel budgets.

### 5.1 Fetch Current Active Budget
- **URL:** `/api/budget/current`
- **Method:** `GET`
- **Session Required:** Yes
- **Response:** `200 OK` with current user's budget details and expense logs.

### 5.2 Save/Update Budget
- **URL:** `/api/budget/save`
- **Method:** `POST`
- **Session Required:** Yes
- **Request Body:**
  ```json
  {
    "tripName": "Goa Trip",
    "totalBudget": 50000.0,
    "members": ["Riya", "Aman", "Pooja"],
    "expenses": [
      {
        "title": "Hotel Stay",
        "amount": 12000.0,
        "paidBy": "Riya"
      }
    ]
  }
  ```
- **Response:** `200 OK`
