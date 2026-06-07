# API Reference

All endpoints are prefixed with `http://localhost:9090/api` (adjust the host/port for production).

## Authentication
| Method | Endpoint | Request Body | Response | Auth Required |
|--------|----------|--------------|----------|---------------|
| POST | `/auth/register` | `{"email":"user@example.com","password":"secret","name":"User Name"}` | `201 Created` with user data | No |
| POST | `/auth/login` | `{"email":"user@example.com","password":"secret"}` | `200 OK` with `{ "token": "<JWT>" }` | No |
| POST | `/auth/logout` | – | `200 OK` | Yes (JWT in `Authorization: Bearer <token>` header) |

## Planner
| Method | Endpoint | Request Body | Response | Auth Required |
|--------|----------|--------------|----------|---------------|
| POST | `/planner/generate` | ```json
{"destination":"Jaipur","days":3,"travellerType":"FAMILY","budgetLevel":"MIDRANGE","season":"WINTER","preferences":"heritage","enhanceWithAi":true}
``` | `200 OK` with itinerary JSON (see example below) | Yes |

### Sample Itinerary Response
```json
{
  "success": true,
  "message": "Itinerary generated successfully",
  "data": {
    "generatedDays": 3,
    "totalPlaces": 6,
    "itinerary": [
      {"day":1,"places":[{"name":"Jaipur Fort","time":"09:00","duration":"2h"}]},
      {"day":2,"places":[{"name":"Hawa Mahal","time":"10:00","duration":"1.5h"}]},
      {"day":3,"places":[{"name":"Amber Palace","time":"09:30","duration":"3h"}]}
    ],
    "budgetAdvice":"Mid‑range budget approx. $1500",
    "aiSummary":"A heritage‑focused 3‑day tour of Jaipur..."
  }
}
```

## Trips (Saved Trips)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/trips/users/{email}` | List all saved trips for a user. | Yes |
| POST | `/trips/users/{email}` | Save a new trip (body = itinerary JSON). | Yes |
| GET | `/trips/{id}` | Retrieve a specific saved trip. | Yes |
| DELETE | `/trips/{id}` | Delete a saved trip. | Yes |

## Profile
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/users/{email}/profile` | Returns user profile and summary of saved trips. | Yes |
| PUT | `/users/{email}/profile` | Update user details (name, password, etc.). | Yes |

## Miscellaneous
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/places` | List all available places (used by the planner). |
| GET | `/places/{id}` | Get detailed info for a single place (images, description, etc.). |

> **Note**: All protected endpoints expect the JWT token in the `Authorization` header:
> `Authorization: Bearer <your‑token>`.
