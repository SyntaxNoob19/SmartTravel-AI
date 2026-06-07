# REST API Specification

This document defines the REST API endpoints exposed by the SmartTravel backend server, including validation rules, request bodies, response formats, and session requirements.

The base URL for local development is: `https://smarttravel-ai.onrender.com`

---

## 1. Session & Access Control

SmartTravel implements a stateful **Session-Based Authentication** model. 
*   Upon successful registration or login, the server establishes a session and returns a `JSESSIONID` cookie.
*   The browser client must include this session cookie in all subsequent requests to protected routes.
*   Protected routes are intercepted by the `SessionAuthFilter` security filter. If the session is invalid, the server rejects requests with `401 Unauthorized`.

---

## 2. Authentication Services (`/api/auth`)

### 2.1 Register User Account
Creates a new user profile.
*   **URL:** `/api/auth/register`
*   **Method:** `POST`
*   **Auth Required:** No
*   **Request Body:**
    ```json
    {
      "name": "Jane Doe",
      "email": "jane@example.com",
      "password": "strongPassword123"
    }
    ```
*   **Success Response:** `200 OK`
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

### 2.2 Login User Session
Validates credentials and initializes the session.
*   **URL:** `/api/auth/login`
*   **Method:** `POST`
*   **Auth Required:** No
*   **Request Body:**
    ```json
    {
      "email": "jane@example.com",
      "password": "strongPassword123"
    }
    ```
*   **Success Response:** `200 OK` (sets the `JSESSIONID` session cookie)

### 2.3 Retrieve Authenticated User Profile
Gets session details for the logged-in user.
*   **URL:** `/api/auth/me`
*   **Method:** `GET`
*   **Auth Required:** Yes
*   **Success Response:** `200 OK` with user credentials detail.

### 2.4 End User Session
Invalidates the current session.
*   **URL:** `/api/auth/logout`
*   **Method:** `POST`
*   **Auth Required:** Yes
*   **Success Response:** `204 No Content` (invalidates session cookie)

---

## 3. Travel Planner Services (`/api/planner`)

### 3.1 Generate Travel Itinerary
Processes traveler choices to create a daily itinerary.
*   **URL:** `/api/planner/generate`
*   **Method:** `POST`
*   **Auth Required:** No (Clients can run queries anonymously; saving plans requires a session).
*   **Request Body:**
    ```json
    {
      "city": "Goa",
      "region": "South",
      "days": 3,
      "travellerType": "SOLO",
      "minRating": 4.0,
      "maxHoursPerDay": 8.0,
      "budgetLevel": "Medium",
      "preferences": "beach, nature, food",
      "enhanceWithAi": true
    }
    ```
*   **Success Response:** `200 OK`
    ```json
    {
      "success": true,
      "message": "Itinerary generated successfully",
      "count": 1,
      "data": {
        "requestedDays": 3,
        "generatedDays": 3,
        "totalPlaces": 6,
        "travellerType": "SOLO",
        "dataSource": "AI_GENERATED",
        "maxHoursPerDay": 8.0,
        "totalTripHours": 18.5,
        "summary": "A beach and nature focused trip...",
        "budgetAdvice": "Estimated budget per day: 2500 INR",
        "generalSafetyTips": "Verify taxi fares beforehand...",
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
                "description": "Scenic beach with watersports...",
                "rating": 4.6
              }
            ]
          }
        ]
      }
    }
    ```

---

## 4. Saved Itineraries Services (`/api/trips`)

### 4.1 Save Itinerary
Associates a generated plan with a user account.
*   **URL:** `/api/trips/users/{email}`
*   **Method:** `POST`
*   **Auth Required:** Yes
*   **Request Body:** Contains the complete generated itinerary JSON payload.
*   **Success Response:** `200 OK`

### 4.2 List Saved Itineraries
Retrieves all itineraries saved by a user.
*   **URL:** `/api/trips/users/{email}`
*   **Method:** `GET`
*   **Auth Required:** Yes
*   **Success Response:** `200 OK` with an array of saved trip models.

### 4.3 Delete Saved Itinerary
Deletes a specific saved trip.
*   **URL:** `/api/trips/{id}?email={user_email}`
*   **Method:** `DELETE`
*   **Auth Required:** Yes
*   **Success Response:** `200 OK`

---

## 5. Explore Services (`/api/explore`)

### 5.1 Filter Destinations
Queries local databases with parameters.
*   **URL:** `/api/explore/filter`
*   **Method:** `GET`
*   **Auth Required:** No
*   **Parameters:**
    *   `region` (string)
    *   `category` (string)
    *   `mood` (string)
    *   `budgetLevel` (string)
    *   `minRating` (double)
    *   `familyFriendly` (boolean)
*   **Success Response:** `200 OK` with a list of matching destinations.

### 5.2 Search Destinations
Executes query matches against names and tags.
*   **URL:** `/api/explore/search?query={search_term}`
*   **Method:** `GET`
*   **Auth Required:** No
*   **Success Response:** `200 OK` with matching results list.

---

## 6. Budgeting & Expenses Services (`/api/budget`)

### 6.1 Get Active Budget Sheet
Retrieves active budget and logged expenses.
*   **URL:** `/api/budget/current`
*   **Method:** `GET`
*   **Auth Required:** Yes
*   **Success Response:** `200 OK` with active BudgetPlan details.

### 6.2 Update Budget Sheet
Updates values and logs travel expenses.
*   **URL:** `/api/budget/save`
*   **Method:** `POST`
*   **Auth Required:** Yes
*   **Request Body:**
    ```json
    {
      "tripName": "Goa Itinerary",
      "totalBudget": 50000.0,
      "members": ["Riya", "Aman", "Pooja"],
      "expenses": [
        {
          "title": "Hotel Booking",
          "amount": 12000.0,
          "paidBy": "Riya"
        }
      ]
    }
    ```
*   **Success Response:** `200 OK`
