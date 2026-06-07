# Database Design Document - SmartTravel

This document details the relational database schema, tables, attributes, datatypes, mappings, and keys utilized in the SmartTravel application.

---

## 1. Entity-Relationship Diagram Description

The transactional database mappings are managed by Spring Data JPA/Hibernate. The database structure follows standard 3NF normalized relationships where appropriate, with element collections for simplicity of embedded entities.

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

---

## 2. Table Schemas & Definitions

### 2.1 Table: `users`
Stores user credential profiles.
- **`id`** (`BIGINT`, Primary Key, Auto-Increment)
- **`name`** (`VARCHAR(255)`, Not Null): Traveler display name.
- **`email`** (`VARCHAR(255)`, Not Null, Unique): Used for login session mapping.
- **`password_hash`** (`VARCHAR(255)`, Not Null): Salted BCrypt encoded password hash.
- **`created_at`** (`TIMESTAMP`, Not Null): Profile creation timestamp.

### 2.2 Table: `saved_trips`
Stores generated day-wise itineraries in structured JSON format.
- **`id`** (`BIGINT`, Primary Key, Auto-Increment)
- **`user_id`** (`BIGINT`, Not Null): Refers to the owning user ID.
- **`user_email`** (`VARCHAR(255)`, Not Null): Mapped for fast email lookup.
- **`trip_name`** (`VARCHAR(255)`, Not Null)
- **`destination`** (`VARCHAR(255)`, Not Null)
- **`planner_request_json`** (`LONGTEXT`, Not Null): Input variables structure (budget, days, category preferences, mood).
- **`planner_response_json`** (`LONGTEXT`, Not Null): Generated day-wise plan details (places, times slots, weather details, maps coordinates).
- **`created_at`** (`TIMESTAMP`, Not Null)
- **`updated_at`** (`TIMESTAMP`, Not Null)

### 2.3 Table: `places`
Curated RAG travel spots database loaded from the CSV dataset.
- **`place_id`** (`VARCHAR(20)`, Primary Key)
- **`place_name`** (`VARCHAR(255)`, Not Null)
- **`city`** (`VARCHAR(120)`)
- **`state`** (`VARCHAR(120)`)
- **`region`** (`VARCHAR(50)`)
- **`place_type`** (`VARCHAR(100)`)
- **`category`** (`VARCHAR(100)`)
- **`significance`** (`VARCHAR(255)`)
- **`description`** (`TEXT`)
- **`mood_tags`** (`VARCHAR(255)`)
- **`best_time_to_visit`** (`VARCHAR(100)`)
- **`ideal_visit_time`** (`VARCHAR(100)`)
- **`recommended_duration_hours`** (`DOUBLE`)
- **`entry_fee`** (`DOUBLE`)
- **`rating`** (`DOUBLE`)
- **`crowd_level`** (`VARCHAR(50)`)
- **`family_friendly`** (`BOOLEAN`)
- **`adventure_level`** (`VARCHAR(50)`)
- **`cultural_value`** (`VARCHAR(50)`)
- **`nature_value`** (`VARCHAR(50)`)
- **`budget_level`** (`VARCHAR(50)`)
- **`latitude`** (`DOUBLE`)
- **`longitude`** (`DOUBLE`)
- **`nearest_airport`** (`VARCHAR(255)`)
- **`nearest_railway`** (`VARCHAR(255)`)
- **`local_tips`** (`TEXT`)
- **`food_specialty`** (`VARCHAR(255)`)
- **`safety_score`** (`DOUBLE`)
- **`cleanliness_score`** (`DOUBLE`)
- **`photography_spots`** (`VARCHAR(50)`)
- **`weather_type`** (`VARCHAR(50)`)
- **`seasonal_highlight`** (`VARCHAR(255)`)
- **`priority`** (`VARCHAR(50)`)

### 2.4 Table: `budget_plan`
Stores shared companion budgets.
- **`id`** (`BIGINT`, Primary Key, Auto-Increment)
- **`user_id`** (`BIGINT`, Not Null, Foreign Key pointing to `users.id`)
- **`total_amount`** (`DOUBLE`)
- **`participants`** (`INT`)
- **`created_at`** (`TIMESTAMP`)
- **`updated_at`** (`TIMESTAMP`)

### 2.5 Element Collection Tables (One-to-Many Mapped)

#### Table: `budget_members`
- **`budget_id`** (`BIGINT`, Foreign Key pointing to `budget_plan.id`)
- **`member_name`** (`VARCHAR(255)`)

#### Table: `budget_expenses`
- **`budget_id`** (`BIGINT`, Foreign Key pointing to `budget_plan.id`)
- **`id`** (`BIGINT`)
- **`name`** (`VARCHAR(255)`): Member name who paid the expense.
- **`amount`** (`DOUBLE`)
- **`date`** (`VARCHAR(255)`)
- **`description`** (`VARCHAR(255)`)
