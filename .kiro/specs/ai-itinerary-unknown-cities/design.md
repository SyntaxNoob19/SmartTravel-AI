# AI Itinerary Unknown Cities & Budget Estimation — Bugfix Design

## Overview

When a user requests a trip plan for a city not present in the local database (e.g. Shimla, Udaipur, Coorg, Ooty, Munnar, Darjeeling, Leh, Gangtok), the backend should call OpenRouter (gpt-4o-mini) to generate a full AI-powered itinerary. Instead, the AI fallback is silently skipped because `PlannerAiService.isAiConfigured()` returns `false` due to two compounding bugs: the `@Value` default for `planner.ai.enabled` is `false` (overriding the `application.properties` default of `true`), and the `@Value` default for `planner.ai.max-tokens` is `1200` (overriding the `application.properties` value of `4000`). When the key is blank, no warning is logged, so the failure is invisible to operators. Additionally, when AI is unavailable for a completely unknown city, `PlannerService` silently falls back to sparse or rule-based results rather than surfacing a meaningful error.

The fix is targeted and minimal: correct the two `@Value` defaults, add a startup warning when the API key is missing, distinguish "not configured" from "call failed" so `PlannerService` can respond appropriately, and throw a `ResourceNotFoundException` for zero-coverage cities when AI is unavailable.

## Glossary

- **Bug_Condition (C)**: The set of inputs that trigger the defective behavior — a city request where the DB has fewer places than `days * 2`, combined with `isAiConfigured()` returning `false` due to misconfigured defaults.
- **Property (P)**: The desired correct behavior for bug-condition inputs — the system calls OpenRouter and returns a `PlannerResponseDto` with `dataSource = "AI_GENERATED"` and a fully populated `itinerary`.
- **Preservation**: Existing behaviors that must remain unchanged — DB-sourced itineraries for well-covered cities, `enhanceWithAi` flow, filter-only requests, validation errors, and `dataSource = "DATABASE"` responses.
- **`PlannerAiService`**: The service in `Backend/src/main/java/com/riya/smarttravel/service/PlannerAiService.java` that calls OpenRouter and parses the AI response.
- **`PlannerService`**: The service in `Backend/src/main/java/com/riya/smarttravel/service/PlannerService.java` that orchestrates DB lookup, AI fallback, and response building.
- **`isAiConfigured()`**: The guard method in `PlannerAiService` that returns `true` only when `aiEnabled == true` AND `aiApiKey` is non-blank.
- **`isCityUnknownOrSparse`**: The boolean in `PlannerService.generate()` that is `true` when a city was specified but the DB has fewer than `days * 2` matching places.
- **`minimumDbCoverage`**: `request.getDays() * 2` — the threshold below which AI generation is preferred over DB results.
- **`OPENROUTER_API_KEY`**: The environment variable that must be set for AI calls to succeed.

## Bug Details

### Bug Condition

The bug manifests when a user submits a city name to `/api/planner/generate` and the city has zero or sparse matching places in the local database. `PlannerService` correctly identifies the city as unknown/sparse and calls `plannerAiService.generateFallbackItinerary(...)`, but `isAiConfigured()` returns `false` because the `@Value` annotation on `aiEnabled` has a hardcoded default of `false`, which takes precedence over the `application.properties` value of `${PLANNER_AI_ENABLED:true}` when the `PLANNER_AI_ENABLED` environment variable is not explicitly set. The result is a silent `Optional.empty()` return, causing `PlannerService` to fall back to sparse DB results or a rule-based fallback — neither of which is useful for an unknown city.

**Formal Specification:**

```
FUNCTION isBugCondition(request, candidates)
  INPUT: request of type PlannerRequest, candidates of type List<Place>
  OUTPUT: boolean

  normalizedCity   := normalize(request.city)
  minimumCoverage  := request.days * 2
  isSparseOrEmpty  := normalizedCity != null AND candidates.size() < minimumCoverage

  aiEnabledDefault := "@Value default is false"   // BUG: should be true
  keyIsBlank       := OPENROUTER_API_KEY is not set in environment

  RETURN isSparseOrEmpty
         AND (aiEnabledDefault == false OR keyIsBlank)
         AND isAiConfigured() == false
END FUNCTION
```

### Examples

- **Shimla, 3 days**: DB returns 0 places → `isCityUnknownOrSparse = true` → `generateFallbackItinerary` called → `isAiConfigured()` returns `false` (aiEnabled default is `false`) → `Optional.empty()` returned → `generateRuleBasedFallback` throws `ResourceNotFoundException` with a confusing OpenRouter billing message.
- **Udaipur, 5 days**: DB returns 1 place → `isCityUnknownOrSparse = true` (1 < 10) → same silent skip → `buildDbResponse` returns a 1-place itinerary for a 5-day trip.
- **Darjeeling, 2 days**: DB returns 0 places, `OPENROUTER_API_KEY` is set but `aiEnabled` defaults to `false` → AI is never called even though the key is valid.
- **Goa, 3 days** (well-covered): DB returns 20 places → `isCityUnknownOrSparse = false` → AI not called → `dataSource = "DATABASE"` returned correctly (this is the preserved behavior).

## Expected Behavior

### Preservation Requirements

**Unchanged Behaviors:**
- When a city has `places >= days * 2` in the DB, the system must continue generating the itinerary from the database and return `dataSource = "DATABASE"`.
- When `enhanceWithAi` is requested on a DB-sourced itinerary, `enhanceItinerary` must continue to be called to add AI tips and `aiSummary`.
- When no city is specified but other filters (region, category, mood, etc.) are provided, the system must continue querying the DB using those filters.
- When the planner request is invalid (missing days, out-of-range values, missing preferences), the system must continue returning `BadRequestException` with the existing validation messages.
- Mouse clicks, non-city filter requests, and all other non-buggy inputs must be completely unaffected by this fix.

**Scope:**
All requests where `isCityUnknownOrSparse` is `false` — i.e., well-covered cities, filter-only requests, and invalid requests — must be completely unaffected by this fix.

**Note:** The expected correct behavior for bug-condition inputs is defined in the Correctness Properties section (Property 1 and Property 2).

## Hypothesized Root Cause

Based on the source code analysis, the root causes are:

1. **Wrong `@Value` default for `aiEnabled`**: `@Value("${planner.ai.enabled:false}")` in `PlannerAiService` uses `false` as the in-code default. When `PLANNER_AI_ENABLED` is not set in the environment, Spring resolves `${PLANNER_AI_ENABLED:true}` in `application.properties` to `true`, but the `@Value` annotation's own default (`false`) takes precedence when the property key itself is not found in the environment. This means `aiEnabled` is `false` unless `PLANNER_AI_ENABLED=true` is explicitly set.

2. **Wrong `@Value` default for `aiMaxTokens`**: `@Value("${planner.ai.max-tokens:1200}")` uses `1200` as the in-code default, while `application.properties` sets `4000`. A 7-day itinerary requires ~4000 tokens; 1200 tokens causes truncated or incomplete AI responses.

3. **No warning when key is blank**: `isAiConfigured()` silently returns `false` when `aiApiKey` is blank. There is no log statement, so operators have no visibility into why AI is disabled.

4. **No distinction between "not configured" and "call failed"**: `generateFallbackItinerary` returns `Optional.empty()` for both cases. `PlannerService` cannot tell whether AI was skipped due to missing config or a runtime failure, so it cannot respond differently for unknown cities.

5. **Silent fallback for unknown cities**: When `generateFallbackItinerary` returns `Optional.empty()` and `candidates.isEmpty()`, `PlannerService` calls `generateRuleBasedFallback`, which throws a `ResourceNotFoundException` with a message referencing OpenRouter billing — confusing and unhelpful for the user.

## Correctness Properties

Property 1: Bug Condition — AI Called for Unknown/Sparse Cities

_For any_ request where `isBugCondition(request, candidates)` returns `true` (city is specified, DB has fewer than `days * 2` places, and AI is configured with a valid key), the fixed `PlannerService.generate(request)` SHALL call `PlannerAiService.generateFallbackItinerary(...)` and return a `PlannerResponseDto` with `dataSource == "AI_GENERATED"`, a non-empty `itinerary` list, and `generatedDays >= 1`.

**Validates: Requirements 2.1, 2.2, 2.4**

Property 2: Preservation — DB Response Unchanged for Well-Covered Cities

_For any_ request where `isBugCondition(request, candidates)` returns `false` (city has `places >= days * 2` in the DB, or no city is specified), the fixed `PlannerService.generate(request)` SHALL return a `PlannerResponseDto` with `dataSource == "DATABASE"`, producing the same result as the original code for those inputs.

**Validates: Requirements 3.1, 3.2, 3.3, 3.5**

Property 3: Warning Logged When Key Missing

_For any_ application startup or call to `isAiConfigured()` where `OPENROUTER_API_KEY` is blank, the fixed `PlannerAiService` SHALL emit a `WARN`-level log message containing "OPENROUTER_API_KEY" and return `false` from `isAiConfigured()`.

**Validates: Requirements 2.3**

Property 4: No Silent Empty for Unknown City

_For any_ request where the city has zero DB places AND AI is not configured (key missing), the fixed `PlannerService.generate(request)` SHALL throw a `ResourceNotFoundException` with a clear, user-facing message — never returning a response with an empty `itinerary` list silently.

**Validates: Requirements 2.5**

Property 5: Response Structure Completeness

_For any_ AI-generated response returned by the fixed `PlannerAiService.generateFallbackItinerary(...)`, the `PlannerResponseDto` SHALL have non-null, non-empty `itinerary`, non-blank `summary`, non-blank `travellerType`, `dataSource == "AI_GENERATED"`, and `generatedDays >= 1`.

**Validates: Requirements 2.4**

## Fix Implementation

### Changes Required

Assuming the root cause analysis is correct, the following targeted changes are needed:

---

**File**: `Backend/src/main/java/com/riya/smarttravel/service/PlannerAiService.java`

**Fix 1 — Correct `@Value` default for `aiEnabled`**

Change:
```java
@Value("${planner.ai.enabled:false}")
private boolean aiEnabled;
```
To:
```java
@Value("${planner.ai.enabled:true}")
private boolean aiEnabled;
```
This aligns the in-code default with `application.properties` (`${PLANNER_AI_ENABLED:true}`).

---

**Fix 2 — Correct `@Value` default for `aiMaxTokens`**

Change:
```java
@Value("${planner.ai.max-tokens:1200}")
private int aiMaxTokens;
```
To:
```java
@Value("${planner.ai.max-tokens:4000}")
private int aiMaxTokens;
```
This aligns the in-code default with `application.properties` (`${PLANNER_AI_MAX_TOKENS:4000}`) and ensures multi-day itineraries are not truncated.

---

**Fix 3 — Log a WARN when API key is missing**

Add a `@PostConstruct` method (or inline check in `isAiConfigured()`) that logs a warning when the key is blank:

```java
@PostConstruct
public void validateAiConfiguration() {
    if (aiEnabled && (aiApiKey == null || aiApiKey.isBlank())) {
        log.warn("AI itinerary generation is DISABLED: OPENROUTER_API_KEY is not set. "
               + "Set the environment variable to enable AI-powered itineraries.");
    }
}
```

The `isAiConfigured()` method itself remains unchanged in logic but the warning is now emitted at startup so operators see it immediately in logs.

---

**Fix 4 — Distinguish "not configured" from "call failed" in `generateFallbackItinerary`**

Introduce a simple sealed result type or use a two-value enum to communicate the outcome back to `PlannerService`:

```java
public enum AiFallbackStatus { NOT_CONFIGURED, CALL_FAILED, SUCCESS }

public record AiFallbackResult(AiFallbackStatus status, PlannerResponseDto response) {
    public static AiFallbackResult notConfigured() {
        return new AiFallbackResult(AiFallbackStatus.NOT_CONFIGURED, null);
    }
    public static AiFallbackResult failed() {
        return new AiFallbackResult(AiFallbackStatus.FAILED, null);
    }
    public static AiFallbackResult success(PlannerResponseDto dto) {
        return new AiFallbackResult(AiFallbackStatus.SUCCESS, dto);
    }
    public boolean isSuccess() { return status == AiFallbackStatus.SUCCESS; }
}
```

`generateFallbackItinerary` returns `AiFallbackResult` instead of `Optional<PlannerResponseDto>`.

---

**File**: `Backend/src/main/java/com/riya/smarttravel/service/PlannerService.java`

**Fix 5 — Error response for unknown city when AI unavailable**

Replace the current `orElseGet` lambda with explicit handling based on `AiFallbackResult.status`:

```java
AiFallbackResult aiResult = plannerAiService.generateFallbackItinerary(...);

if (aiResult.isSuccess()) {
    return aiResult.response();
}

// AI was not available or failed
if (candidates.isEmpty()) {
    // Zero DB coverage — no useful fallback exists
    if (aiResult.status() == AiFallbackStatus.NOT_CONFIGURED) {
        log.warn("AI itinerary generation unavailable for city={}. Using local fallback.", normalizedCity);
        throw new ResourceNotFoundException(
            "No places found for city '" + request.getCity() + "'. "
            + "AI generation is disabled — set OPENROUTER_API_KEY to enable it.");
    } else {
        // AI was configured but the call failed
        throw new ResourceNotFoundException(
            "No places found for city '" + request.getCity() + "' and AI generation failed. "
            + "Please try again later.");
    }
} else {
    // Sparse DB coverage — return what we have with a clear dataSource
    log.warn("AI itinerary generation unavailable for city={}. Using local fallback.", normalizedCity);
    return buildDbResponse(candidates, request, travellerType, maxHoursPerDay);
}
```

---

**Fix 6 — Add structured log statements**

Add the following log calls at the appropriate points in `PlannerService.generate()` and `PlannerAiService`:

In `PlannerService.generate()`:
```java
log.info("Trip request received for city={}, days={}, travellerType={}", normalizedCity, request.getDays(), travellerType);
// after candidates are resolved:
log.info("Destination detected: city={}, dbCandidates={}, minimumRequired={}, usingAI={}",
         normalizedCity, candidates.size(), minimumDbCoverage, isCityUnknownOrSparse);
```

In `PlannerAiService.generateFallbackItinerary()`:
```java
log.info("OpenRouter request started for city={}, model={}", city, aiModel);
// after successful response:
log.info("OpenRouter response received, parsing itinerary for city={}", city);
// after successful parse:
log.info("AI itinerary parsed successfully: days={}, places={}", response.getGeneratedDays(), response.getTotalPlaces());
```

---

**Fix 7 — Frontend: "AI Generated" badge (non-breaking)**

The `renderItinerary()` function in `itinerary.js` already sets `sourceText = 'AI Generated'` when `dataSource === 'AI_GENERATED'` and renders it in the source chip area:

```javascript
<span><i class="fas fa-signal"></i> ${escapeHtml(sourceText)}</span>
```

No structural HTML changes are needed. The badge is already present. This fix is a no-op — the frontend already handles `AI_GENERATED` correctly.

## Testing Strategy

### Validation Approach

The testing strategy follows a two-phase approach: first, surface counterexamples that demonstrate the bug on unfixed code (exploratory), then verify the fix works correctly and preserves existing behavior (fix checking + preservation checking).

### Exploratory Bug Condition Checking

**Goal**: Surface counterexamples that demonstrate the bug BEFORE implementing the fix. Confirm or refute the root cause analysis. If we refute, we will need to re-hypothesize.

**Test Plan**: Write unit tests that call `PlannerAiService.isAiConfigured()` and `PlannerService.generate()` with a city that has zero DB places, using a valid `OPENROUTER_API_KEY` but without setting `PLANNER_AI_ENABLED`. Run these tests on the UNFIXED code to observe that `isAiConfigured()` returns `false` even with a valid key, confirming the `@Value` default bug.

**Test Cases**:
1. **`isAiConfigured()` with valid key, default `aiEnabled`**: Inject `aiEnabled = false` (the current default), set a non-blank `aiApiKey` → assert `isAiConfigured()` returns `false`. (Will demonstrate the bug on unfixed code.)
2. **`generate()` for Shimla with 0 DB places**: Mock `PlaceRepository` to return empty list, mock `PlannerAiService` to return `Optional.empty()` → assert the response is a rule-based fallback or exception, not `AI_GENERATED`. (Will demonstrate the bug on unfixed code.)
3. **`generate()` for Udaipur with 1 DB place, 5 days**: Mock repository to return 1 place → assert `dataSource != "AI_GENERATED"`. (Will demonstrate sparse fallback bug.)
4. **No WARN log when key is blank**: Capture log output with `aiApiKey = ""` → assert no WARN is emitted. (Will demonstrate the missing warning bug.)

**Expected Counterexamples**:
- `isAiConfigured()` returns `false` even when `aiApiKey` is non-blank, because `aiEnabled` defaults to `false`.
- `generate()` for an unknown city returns a rule-based or sparse DB response instead of `AI_GENERATED`.
- No warning is logged when the key is blank.

### Fix Checking

**Goal**: Verify that for all inputs where the bug condition holds, the fixed function produces the expected behavior.

**Pseudocode:**
```
FOR ALL request WHERE isBugCondition(request, candidates) DO
  result := PlannerService_fixed.generate(request)
  ASSERT result.dataSource == "AI_GENERATED"
  ASSERT result.itinerary != null AND result.itinerary.size() >= 1
  ASSERT result.generatedDays >= 1
  ASSERT result.summary != null AND NOT result.summary.isBlank()
END FOR
```

### Preservation Checking

**Goal**: Verify that for all inputs where the bug condition does NOT hold, the fixed function produces the same result as the original function.

**Pseudocode:**
```
FOR ALL request WHERE NOT isBugCondition(request, candidates) DO
  ASSERT PlannerService_original.generate(request).dataSource
      == PlannerService_fixed.generate(request).dataSource
  ASSERT PlannerService_original.generate(request).itinerary.size()
      == PlannerService_fixed.generate(request).itinerary.size()
END FOR
```

**Testing Approach**: Property-based testing is recommended for preservation checking because:
- It generates many test cases automatically across the input domain (varying cities, days, traveller types).
- It catches edge cases that manual unit tests might miss (e.g., cities with exactly `days * 2` places).
- It provides strong guarantees that DB-sourced behavior is unchanged for all non-buggy inputs.

**Test Plan**: Observe behavior on UNFIXED code for well-covered cities (Goa, Mumbai, Delhi), then write property-based tests capturing that behavior.

**Test Cases**:
1. **Well-covered city preservation**: For any city with `places >= days * 2`, verify `dataSource == "DATABASE"` before and after fix.
2. **`enhanceWithAi` preservation**: Verify that `enhanceItinerary` is still called on DB responses when `enhanceWithAi = true`.
3. **Filter-only request preservation**: Verify that requests with no city but with region/category/mood filters still return DB results.
4. **Validation error preservation**: Verify that invalid requests (days = 0, days = 15, no preferences) still throw `BadRequestException`.

### Unit Tests

**`PlannerAiServiceTest`**:
- `isAiConfigured()` returns `false` when `aiEnabled = false`, regardless of key value.
- `isAiConfigured()` returns `false` when `aiEnabled = true` but `aiApiKey` is blank.
- `isAiConfigured()` returns `true` when `aiEnabled = true` and `aiApiKey` is non-blank.
- After fix: `isAiConfigured()` returns `true` with default `aiEnabled` and a non-blank key.
- After fix: a `WARN` log is emitted at `@PostConstruct` when `aiEnabled = true` and key is blank.
- `generateFallbackItinerary()` returns `AiFallbackResult.notConfigured()` when not configured.
- `generateFallbackItinerary()` returns `AiFallbackResult.failed()` on network/parse error.
- `generateFallbackItinerary()` returns `AiFallbackResult.success(dto)` on valid AI response.

**`PlannerServiceTest`**:
- Unknown city (0 DB places) + AI configured + AI returns success → `dataSource == "AI_GENERATED"`.
- Sparse city (1 DB place, 5 days) + AI configured + AI returns success → `dataSource == "AI_GENERATED"`.
- Well-covered city (20 DB places, 3 days) → `dataSource == "DATABASE"` (regression test).
- Unknown city (0 DB places) + AI not configured → throws `ResourceNotFoundException` with clear message.
- Unknown city (0 DB places) + AI configured but call fails → throws `ResourceNotFoundException`.
- Sparse city + AI not configured → returns sparse DB result with `dataSource == "DATABASE"` and WARN log.
- `INFO` log emitted with city, days, travellerType at start of `generate()`.
- `INFO` log emitted with dbCandidates count and `usingAI` flag after candidate resolution.

### Property-Based Tests

- **P1 (AI-first for unknown cities)**: Generate random city names not in DB with random `days` (1–14) and random `travellerType` → assert `dataSource == "AI_GENERATED"` when AI is configured.
- **P2 (Sparse city triggers AI)**: Generate cities with `0 < places < days * 2` in DB → assert `dataSource == "AI_GENERATED"` when AI is configured.
- **P3 (DB preserved for well-covered cities)**: Generate cities with `places >= days * 2` in DB → assert `dataSource == "DATABASE"`.
- **P4 (No silent empty for unknown city)**: Generate cities with 0 DB places and AI not configured → assert either `ResourceNotFoundException` is thrown or `dataSource != null` (never empty itinerary silently).
- **P5 (Warning logged when key missing)**: For any call with blank `aiApiKey` → assert WARN log is emitted and `isAiConfigured()` returns `false`.
- **P6 (Response structure completeness)**: For any successful AI response → assert `itinerary != null`, `itinerary.size() >= 1`, `summary` non-blank, `travellerType` non-blank, `dataSource == "AI_GENERATED"`, `generatedDays >= 1`.

### Integration Tests

- **POST `/api/planner/generate` with `city=Shimla`, `days=3`**: With `OPENROUTER_API_KEY` set → expect HTTP 200 with `dataSource == "AI_GENERATED"` and `itinerary.length >= 1`.
- **POST `/api/planner/generate` with `city=Shimla`, `days=3`**: Without `OPENROUTER_API_KEY` → expect HTTP 404 with a clear error message (not a silent empty response).
- **POST `/api/planner/generate` with `city=Goa`, `days=3`**: With or without key → expect HTTP 200 with `dataSource == "DATABASE"` (regression guard).
- **Startup log check**: On application startup with blank `OPENROUTER_API_KEY` → verify WARN log appears in `app-startup.log`.


---

## Issue 2: Budget Estimation Accuracy and Consistency — Design

### Overview

The budget calculator provides rigid, exact cost estimates with destination-specific hardcoded multipliers that create inconsistent and unrealistic per-traveler daily costs. Budget tiers are not calibrated to realistic price ranges. The fix removes hardcoded destination multipliers, recalibrates budget tiers to realistic ranges, and implements deterministic variance using destination name hashing so estimates are realistic, consistent, and not artificially rigid.

### Glossary

- **Bug_Condition (C)**: The set of inputs that trigger the defective behavior — any budget tier selection where the calculated daily cost uses destination-specific multipliers or falls outside realistic tier ranges.
- **Property (P)**: The desired correct behavior for bug-condition inputs — the system calculates a daily per-traveler cost deterministically within the tier's realistic range, varying by destination name (via hash) without using hardcoded multipliers.
- **Preservation**: Existing budget UI and workflow remain unchanged; only the calculation and tier ranges change. Budget display format, calculation flow, and frontend integration remain the same.
- **`getDestinationSeed(destination)`**: A new hash function that returns a stable numeric seed in the range [0, 1) based on the destination name.
- **`getBudgetPreferenceRange(tier)`**: Returns the global min/max daily cost for a tier (e.g., Budget: ₹1,000–₹2,000).
- **`mapBudgetLevelToDaily(tier, destination)`**: Calculates the daily per-traveler cost deterministically within the tier's range, using `getDestinationSeed(destination)` to vary the cost.
- **`calculateCategoryBreakdown(totalBudget)`**: Distributes the total budget across categories (Hotel 40%, Food 18%, Transport 20%, Activities 22%), ensuring the sum equals the total exactly.

### Bug Condition

The budget estimation bug manifests when a user selects a budget tier. The system calculates the daily per-traveler cost using destination-specific hardcoded multipliers (stored in `DESTINATION_OVERRIDES`), leading to:

1. **Inconsistency**: Different destinations for the same tier get different costs without user visibility into why.
2. **Unrealism**: The calculated costs fall outside realistic ranges (e.g., Budget Traveler ₹2,500/day instead of ₹1,000–₹2,000/day).
3. **Rigidity**: For a given destination and tier, the cost is always the same, appearing artificially fixed rather than an estimate.
4. **Arithmetic mismatch**: Budget category breakdown does not sum to exactly the total, causing rounding discrepancies.

### Expected Behavior

#### Budget Tier Ranges (Calibrated to Realism)

- **Budget Traveler**: ₹1,000–₹2,000 per traveler/day (average target: ₹1,500)
  - Deterministic variance: ₹1,200–₹1,800 in ₹100 increments
- **Comfortable Explorer**: ₹2,000–₹3,500 per traveler/day (average target: ₹3,000)
  - Deterministic variance: ₹2,500–₹3,200 in ₹100 increments
- **Premium Experience**: ₹4,000–₹8,000 per traveler/day (average target: ₹6,000)
  - Deterministic variance: ₹4,500–₹7,500 in ₹100 increments
- **Luxury Adventure**: ₹10,000+ per traveler/day (average target: ₹10,000)
  - Deterministic variance: ₹10,000–₹15,000 in ₹100 increments

#### Deterministic Variance Calculation

**Algorithm:**

1. Compute a stable seed using the destination name: `seed = getDestinationSeed(destination)` (returns value in [0, 1))
2. For the selected tier, compute the variance range within the tier's global bounds
3. Map the seed to an index within the variance range: `index = Math.floor(seed * rangeStepCount)` (e.g., 8 steps for Budget: ₹1,200, ₹1,300, ..., ₹1,800)
4. Calculate daily cost: `dailyCost = minVariance + (index * ₹100)`
5. Clamp to global tier bounds: `dailyCost = Math.max(tierMin, Math.min(tierMax, dailyCost))`

**Example: Budget tier, Shimla**
- `seed = getDestinationSeed("Shimla") ≈ 0.42`
- Variance range: ₹1,200–₹1,800 (7 steps of ₹100 = indices 0–7)
- `index = Math.floor(0.42 * 7) = 2`
- `dailyCost = ₹1,200 + (2 * ₹100) = ₹1,400`
- Clamp: `Math.max(₹1,000, Math.min(₹2,000, ₹1,400)) = ₹1,400` ✓

**Example: Budget tier, Delhi (different seed)**
- `seed = getDestinationSeed("Delhi") ≈ 0.73`
- `index = Math.floor(0.73 * 7) = 5`
- `dailyCost = ₹1,200 + (5 * ₹100) = ₹1,700`
- Clamp: ₹1,700 ✓

#### Budget Category Breakdown (Exact Sum)

**Formula:**

- Hotel = `Math.round(Total * 0.40)`
- Food = `Math.round(Total * 0.18)`
- Transport = `Math.round(Total * 0.20)`
- Activities = `Total - Hotel - Food - Transport` (computed last to guarantee sum = Total)

**Example: Total = ₹3,000**
- Hotel = `Math.round(3000 * 0.40) = ₹1,200`
- Food = `Math.round(3000 * 0.18) = ₹540`
- Transport = `Math.round(3000 * 0.20) = ₹600`
- Activities = `₹3,000 - ₹1,200 - ₹540 - ₹600 = ₹660`
- Sum = ₹1,200 + ₹540 + ₹600 + ₹660 = ₹3,000 ✓

#### Traveler Count Defaults

- Solo: 1 traveler
- Couple: 2 travelers
- Family: 4 travelers
- Friend Group: 4 travelers
- (Or use actual counts if provided in the request)

#### Frontend Label and Disclaimer

- Card header: `[Destination] • [Budget Type] Budget ₹[Daily Rate] per traveler/day`
- Subtitle: "Estimated Total: ₹[Total]"
- Disclaimer: "Estimated costs are approximate and may vary by season, hotel choice, and activities."

### Preservation Requirements

**Unchanged Behaviors:**
- Budget display location, UI structure, and rendering remain unchanged.
- Budget is still used to populate the cost estimate on the trip details.
- The budget request/response flow in `PlannerController` and `PlannerService` is unchanged.
- Calculation is still triggered when the user selects a budget tier and trip details.
- Mouse clicks, filter requests, and all non-budget-related functionality are unaffected.

**Scope:**
All requests that do not involve budget calculation are completely unaffected. Budget calculations for existing well-known cities (e.g., Delhi, Goa) should not materially change (within ±5% variance).

### Correctness Properties

**Property 6: Deterministic Variance — Budget Daily Cost Within Tier Range**

_For any_ request where a user selects a budget tier and specifies a destination, the fixed `mapBudgetLevelToDaily(tier, destination)` SHALL return a daily per-traveler cost within the tier's variance range, computed deterministically using `getDestinationSeed(destination)`, without referencing `DESTINATION_OVERRIDES`.

**Validates: Requirements 4.2, 4.3, 4.4, 4.5**

**Property 7: Exact Category Sum**

_For any_ calculated total trip budget, the fixed `calculateCategoryBreakdown(total)` SHALL allocate categories (Hotel 40%, Food 18%, Transport 20%, Activities 22%) such that `Hotel + Food + Transport + Activities == Total` (no rounding discrepancies).

**Validates: Requirement 4.7**

**Property 8: Preservation — Well-Known City Costs Within ±5%**

_For any_ request where a well-known city (Goa, Delhi, Mumbai) is selected with a given budget tier, the fixed `mapBudgetLevelToDaily(tier, destination)` SHALL return a daily cost within ±5% of the current code's return value, ensuring existing user expectations are preserved.

**Validates: Requirement 5.6**

### Fix Implementation

#### Changes Required

---

**File**: `Frontend/js/utils.js`

**Fix 8 — Remove `DESTINATION_OVERRIDES` and add `getDestinationSeed(destination)` function**

Remove or empty the `DESTINATION_OVERRIDES` object:
```javascript
// BEFORE:
const DESTINATION_OVERRIDES = {
    "Delhi": 1.2,
    "Goa": 0.9,
    "Mumbai": 1.1,
    // ... other overrides
};

// AFTER: (removed or empty)
const DESTINATION_OVERRIDES = {};
```

Add a new `getDestinationSeed(destination)` function:
```javascript
function getDestinationSeed(destination) {
    // Returns a stable numeric seed in [0, 1) based on destination name
    if (!destination || destination.trim() === '') return 0.5;
    
    let hash = 0;
    const str = destination.toLowerCase().trim();
    for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32-bit integer
    }
    
    // Normalize to [0, 1)
    return (Math.abs(hash) % 1000) / 1000;
}
```

---

**Fix 9 — Recalibrate `getBudgetPreferenceRange(tier)` function**

Update the function to return calibrated ranges:
```javascript
function getBudgetPreferenceRange(tier) {
    const ranges = {
        "BUDGET": { minDaily: 1000, maxDaily: 2000, minTrip: 3000, maxTrip: 40000, label: "Budget Traveler" },
        "COMFORT": { minDaily: 2000, maxDaily: 3500, minTrip: 6000, maxTrip: 105000, label: "Comfortable Explorer" },
        "PREMIUM": { minDaily: 4000, maxDaily: 8000, minTrip: 12000, maxTrip: 240000, label: "Premium Experience" },
        "LUXURY": { minDaily: 10000, maxDaily: 15000, minTrip: 30000, maxTrip: 450000, label: "Luxury Adventure" }
    };
    return ranges[tier] || ranges["COMFORT"];
}
```

---

**Fix 10 — Update `mapBudgetLevelToDaily(tier, destination)` function**

Replace the destination multiplier logic with deterministic variance:
```javascript
function mapBudgetLevelToDaily(tier, destination) {
    const tierRange = getBudgetPreferenceRange(tier);
    const minDaily = tierRange.minDaily;
    const maxDaily = tierRange.maxDaily;
    
    // Determine variance range (tighter than global range)
    const variances = {
        "BUDGET": { min: 1200, max: 1800, step: 100 },      // 7 steps
        "COMFORT": { min: 2500, max: 3200, step: 100 },    // 8 steps
        "PREMIUM": { min: 4500, max: 7500, step: 100 },    // 31 steps
        "LUXURY": { min: 10000, max: 15000, step: 100 }    // 51 steps
    };
    
    const variance = variances[tier] || variances["COMFORT"];
    const stepCount = Math.floor((variance.max - variance.min) / variance.step) + 1;
    const seed = getDestinationSeed(destination);
    
    // Map seed to step index
    const stepIndex = Math.floor(seed * (stepCount - 1));
    let dailyCost = variance.min + (stepIndex * variance.step);
    
    // Clamp to global tier bounds
    dailyCost = Math.max(minDaily, Math.min(maxDaily, dailyCost));
    
    return dailyCost;
}
```

---

**Fix 11 — Add `calculateCategoryBreakdown(totalBudget)` function**

Add a new function that ensures exact category sum:
```javascript
function calculateCategoryBreakdown(totalBudget) {
    const hotelPercent = 0.40;
    const foodPercent = 0.18;
    const transportPercent = 0.20;
    const activitiesPercent = 0.22;
    
    const hotel = Math.round(totalBudget * hotelPercent);
    const food = Math.round(totalBudget * foodPercent);
    const transport = Math.round(totalBudget * transportPercent);
    // Activities computed last to guarantee exact sum
    const activities = totalBudget - hotel - food - transport;
    
    return {
        hotel: Math.max(0, hotel),
        food: Math.max(0, food),
        transport: Math.max(0, transport),
        activities: Math.max(0, activities),
        total: totalBudget
    };
}
```

---

**Fix 12 — Update `getTripDestinationName(data)` to resolve destination strictly**

Ensure destination is resolved correctly:
```javascript
function getTripDestinationName(data) {
    // Try to resolve destination in order of preference
    const destination = data?.selectedDestination 
                     || data?.plannerRequest?.city 
                     || data?.destination 
                     || data?.city 
                     || 'Destination';
    return String(destination).trim();
}
```

---

**File**: `Frontend/js/itinerary.js`

**Fix 13 — Update `preferenceLabels` to reflect clarified ranges**

Update the budget tier labels in the `preferenceLabels` object:
```javascript
const preferenceLabels = {
    // ... other preferences ...
    budget: {
        BUDGET: "Budget Traveler (₹1,000–₹2,000/day)",
        COMFORT: "Comfortable Explorer (₹2,000–₹3,500/day)",
        PREMIUM: "Premium Experience (₹4,000–₹8,000/day)",
        LUXURY: "Luxury Adventure (₹10,000+/day)"
    }
};
```

---

**Fix 14 — Redesign `renderBudgetPanel` to clarify "Estimated"**

Update the budget card rendering:
```javascript
function renderBudgetPanel(container, data, preferences) {
    const destination = getTripDestinationName(data);
    const budgetType = preferenceLabels.budget[preferences.budget] || "Comfortable Explorer";
    const dailyRate = mapBudgetLevelToDaily(preferences.budget, destination);
    const totalBudget = dailyRate * getTravelerCount(preferences) * data.plannerRequest.days;
    const breakdown = calculateCategoryBreakdown(totalBudget);
    
    const budgetCard = document.createElement('div');
    budgetCard.className = 'budget-card';
    
    // Clear header showing budget context
    const header = document.createElement('h3');
    header.textContent = `Estimated Budget`;
    header.className = 'budget-header';
    
    // Subtitle with destination, type, and daily rate
    const subtitle = document.createElement('p');
    subtitle.className = 'budget-subtitle';
    subtitle.innerHTML = `<strong>${escapeHtml(destination)}</strong> • ${escapeHtml(budgetType)} <strong>₹${dailyRate}</strong> per traveler/day`;
    
    // Budget context block
    const contextBlock = document.createElement('div');
    contextBlock.className = 'budget-context';
    contextBlock.innerHTML = `
        <div class="context-item">
            <span class="label">Duration:</span>
            <span class="value">${data.plannerRequest.days} days</span>
        </div>
        <div class="context-item">
            <span class="label">Travelers:</span>
            <span class="value">${getTravelerCount(preferences)} ${getTravelerCount(preferences) === 1 ? 'person' : 'people'}</span>
        </div>
        <div class="context-item">
            <span class="label">Budget Type:</span>
            <span class="value">${escapeHtml(budgetType)}</span>
        </div>
    `;
    
    // Budget breakdown
    const breakdownDiv = document.createElement('div');
    breakdownDiv.className = 'budget-breakdown';
    breakdownDiv.innerHTML = `
        <h4>Estimated Total: ₹${totalBudget.toLocaleString('en-IN')}</h4>
        <div class="budget-categories">
            <div class="category">
                <span>Hotel (40%)</span>
                <strong>₹${breakdown.hotel.toLocaleString('en-IN')}</strong>
            </div>
            <div class="category">
                <span>Food (18%)</span>
                <strong>₹${breakdown.food.toLocaleString('en-IN')}</strong>
            </div>
            <div class="category">
                <span>Transport (20%)</span>
                <strong>₹${breakdown.transport.toLocaleString('en-IN')}</strong>
            </div>
            <div class="category">
                <span>Activities (22%)</span>
                <strong>₹${breakdown.activities.toLocaleString('en-IN')}</strong>
            </div>
        </div>
    `;
    
    // Disclaimer
    const disclaimer = document.createElement('p');
    disclaimer.className = 'budget-disclaimer';
    disclaimer.textContent = "Estimated costs are approximate and may vary by season, hotel choice, and activities.";
    
    budgetCard.appendChild(header);
    budgetCard.appendChild(subtitle);
    budgetCard.appendChild(contextBlock);
    budgetCard.appendChild(breakdownDiv);
    budgetCard.appendChild(disclaimer);
    
    container.appendChild(budgetCard);
}
```

---

**File**: `Frontend/html/planner.html`

**Fix 15 — Update budget dropdown options**

Update the `<select id="budgetLevel">` dropdown to reflect clarified ranges:
```html
<select id="budgetLevel" class="form-control">
    <option value="BUDGET">Budget Traveler (₹1,000–₹2,000/day)</option>
    <option value="COMFORT" selected>Comfortable Explorer (₹2,000–₹3,500/day)</option>
    <option value="PREMIUM">Premium Experience (₹4,000–₹8,000/day)</option>
    <option value="LUXURY">Luxury Adventure (₹10,000+/day)</option>
</select>
```

---

**File**: `Frontend/css/itinerary.css`

**Fix 16 — Add CSS styles for budget display**

Add the following CSS rules:
```css
.budget-card {
    background: linear-gradient(135deg, #f5f9fa 0%, #e8f4f8 100%);
    border-left: 4px solid #00897b;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.budget-header {
    margin: 0 0 8px 0;
    font-size: 18px;
    font-weight: 600;
    color: #00695c;
}

.budget-subtitle {
    margin: 0 0 12px 0;
    font-size: 14px;
    color: #455a64;
    line-height: 1.5;
}

.budget-context {
    background: rgba(255, 255, 255, 0.6);
    border-radius: 6px;
    padding: 12px;
    margin-bottom: 12px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 12px;
}

.context-item {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
}

.context-item .label {
    color: #666;
    font-weight: 500;
}

.context-item .value {
    color: #00695c;
    font-weight: 600;
}

.budget-breakdown {
    background: rgba(255, 255, 255, 0.8);
    border-radius: 6px;
    padding: 12px;
    margin-bottom: 12px;
}

.budget-breakdown h4 {
    margin: 0 0 12px 0;
    font-size: 16px;
    font-weight: 600;
    color: #00695c;
}

.budget-categories {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 8px;
}

.category {
    background: #f0f7f6;
    border-radius: 4px;
    padding: 8px;
    font-size: 12px;
    border-left: 3px solid #00897b;
}

.category span {
    display: block;
    color: #555;
    margin-bottom: 4px;
}

.category strong {
    display: block;
    color: #00695c;
    font-size: 13px;
}

.budget-disclaimer {
    margin: 12px 0 0 0;
    font-size: 12px;
    color: #666;
    font-style: italic;
    line-height: 1.4;
    padding-top: 8px;
    border-top: 1px solid rgba(0, 137, 123, 0.1);
}
```

---

### Testing Strategy (Budget)

#### Exploratory Bug Condition Checking

**Goal**: Surface counterexamples that demonstrate the budget bug on unfixed code.

**Test Cases**:
1. **`mapBudgetLevelToDaily()` with destination multiplier**: Call with tier="BUDGET", destination="Delhi" → assert return value uses the "Delhi" multiplier (e.g., 1.2x).
2. **`mapBudgetLevelToDaily()` outside tier range**: Call with tier="BUDGET", destination="SomeCity" → assert return value is outside the realistic ₹1,000–₹2,000 range.
3. **Category breakdown sum mismatch**: Call `calculateCategoryBreakdown(₹3,001)` → assert Hotel + Food + Transport + Activities ≠ ₹3,001 (rounding error).
4. **UI label ambiguity**: Check that the budget card header does not say "Estimated" — it says "Budget" instead.

**Expected Counterexamples**:
- `mapBudgetLevelToDaily("BUDGET", "Delhi")` returns ₹1,800 (1.2x base), outside the "Budget Traveler" label.
- `mapBudgetLevelToDaily("BUDGET", "Goa")` returns ₹1,350 (0.9x base), inconsistent with Delhi.
- Category sum ≠ total budget due to rounding.

#### Fix Checking

**Goal**: Verify that for all budget tier selections, the fixed function produces the expected behavior.

**Pseudocode:**
```
FOR ALL tier IN ["BUDGET", "COMFORT", "PREMIUM", "LUXURY"]
FOR ALL destination IN test destinations:
  dailyCost := mapBudgetLevelToDaily_fixed(tier, destination)
  tierRange := getBudgetPreferenceRange(tier)
  ASSERT dailyCost >= tierRange.varMin AND dailyCost <= tierRange.varMax
  ASSERT dailyCost is a multiple of 100 (consistent step)
  ASSERT getDestinationSeed(destination) is consistent across calls
END FOR
```

#### Preservation Checking

**Goal**: Verify that for well-known cities, budget estimates remain within ±5% of current values.

**Test Cases**:
1. **Goa + Comfort tier**: Call `mapBudgetLevelToDaily_original("COMFORT", "Goa")` and `mapBudgetLevelToDaily_fixed("COMFORT", "Goa")` → assert the difference is ≤ ±5%.
2. **Delhi + Budget tier**: Similar check.
3. **Mumbai + Premium tier**: Similar check.

#### Unit Tests

**`BudgetUtilsTest`**:
- `getDestinationSeed()` returns the same value for the same destination across calls.
- `getDestinationSeed("Shimla") < getDestinationSeed("Delhi")` or similar order (consistency check).
- `mapBudgetLevelToDaily()` returns a value within the variance range for each tier.
- `mapBudgetLevelToDaily()` returns a multiple of 100.
- `mapBudgetLevelToDaily()` does not reference `DESTINATION_OVERRIDES`.
- `calculateCategoryBreakdown()` ensures `Hotel + Food + Transport + Activities == Total` for various totals.
- Budget tier dropdown options match the clarified ranges.

#### Property-Based Tests

- **P6 (Deterministic Variance)**: For any tier and any destination, `mapBudgetLevelToDaily(tier, destination)` returns a value within the tier's variance range.
- **P7 (Exact Category Sum)**: For any total budget, `calculateCategoryBreakdown(total)` ensures the category sum equals the total exactly.
- **P8 (Preservation)**: For well-known cities, the fixed daily cost is within ±5% of the original.

#### Integration Tests

- POST `/api/planner/generate` with `budgetLevel=COMFORT`, `destination=Shimla` → expect budget breakdown with exact category sum.
- Frontend: Select "Budget Traveler" → verify daily rate is within ₹1,200–₹1,800.
- Frontend: Select "Luxury Adventure" → verify daily rate is within ₹10,000–₹15,000.
- Frontend: Check that disclaimer appears on the budget card.
