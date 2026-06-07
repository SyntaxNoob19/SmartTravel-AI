# Implementation Plan

## Overview

This plan follows the exploratory bugfix workflow for the `ai-itinerary-unknown-cities` bug. The two root-cause defects in `PlannerAiService` (`@Value` defaults of `false`/`1200`) cause `isAiConfigured()` to silently return `false`, skipping AI generation for unknown and sparse cities. The fix is applied in four phases: explore the bug with property-based tests on unfixed code, capture preservation baselines, apply the targeted fix, then validate both properties pass.

## Task Dependency Graph

```json
{
  "waves": [
    { "wave": 1, "tasks": ["1"] },
    { "wave": 2, "tasks": ["2"] },
    { "wave": 3, "tasks": ["3.1", "3.2", "3.3"] },
    { "wave": 4, "tasks": ["3.4"] },
    { "wave": 5, "tasks": ["3.5"] },
    { "wave": 6, "tasks": ["3.6", "3.7"] },
    { "wave": 7, "tasks": ["4", "5.1", "5.2", "5.3"] }
  ]
}
```

## Tasks

- [x] 1. Write bug condition exploration test
  - **Property 1: Bug Condition** - AI Silently Skipped for Unknown/Sparse Cities
  - **CRITICAL**: This test MUST FAIL on unfixed code — failure confirms the bug exists
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior — it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate that `isAiConfigured()` returns `false` even when `OPENROUTER_API_KEY` is set, because the `@Value` default for `aiEnabled` is `false`
  - **Scoped PBT Approach**: Scope the property to the concrete failing cases — city with 0 DB places, valid non-blank `aiApiKey`, `aiEnabled` left at its in-code default (`false`)
  - Create `PlannerAiServiceBugConditionTest` in `Backend/src/test/java/com/riya/smarttravel/service/`
  - Use reflection or a test-specific constructor to inject `aiEnabled = false` (the current default) and a non-blank `aiApiKey` (e.g. `"sk-test-key"`)
  - Assert that `isAiConfigured()` returns `false` — this is the bug: a valid key is ignored because `aiEnabled` defaults to `false`
  - Create `PlannerServiceBugConditionTest`: mock `PlaceRepository` to return an empty list for city "Shimla", mock `PlannerAiService.generateFallbackItinerary` to return `Optional.empty()` (simulating the current broken behavior), call `generate()` with `city=Shimla, days=3, travellerType=SOLO`
  - Assert the response does NOT have `dataSource == "AI_GENERATED"` (it will be a rule-based fallback or exception)
  - Also assert no WARN log is emitted when `aiApiKey` is blank (demonstrates the missing warning bug)
  - Run tests on UNFIXED code
  - **EXPECTED OUTCOME**: Tests FAIL — `isAiConfigured()` returns `false` with a valid key, and no warning is logged for blank key
  - Document counterexamples found (e.g. `isAiConfigured()` returns `false` even with `aiApiKey = "sk-test-key"` because `aiEnabled = false`)
  - Mark task complete when tests are written, run, and failure is documented
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 2. Write preservation property tests (BEFORE implementing fix)
  - **Property 2: Preservation** - DB Response Unchanged for Well-Covered Cities
  - **IMPORTANT**: Follow observation-first methodology
  - Observe behavior on UNFIXED code for well-covered cities: mock `PlaceRepository` to return 20 places for "Goa" with `days=3` (20 >= 6 = days*2), call `generate()`, observe `dataSource == "DATABASE"`
  - Observe: filter-only requests (no city, with region/category/mood) return `dataSource == "DATABASE"` on unfixed code
  - Observe: invalid requests (days=0, days=15, no preferences) throw `BadRequestException` on unfixed code
  - Create `PlannerServicePreservationTest` in `Backend/src/test/java/com/riya/smarttravel/service/`
  - Write property-based test: for any city where `places.size() >= days * 2` (generate random `days` 1–7, populate mock repository with `days * 2` or more places), assert `dataSource == "DATABASE"` — this must hold on both unfixed and fixed code
  - Write property-based test: for any request with no city but with at least one filter (region, category, mood), assert `dataSource == "DATABASE"` and no exception is thrown
  - Write example test: `enhanceWithAi = true` on a DB-sourced response still calls `enhanceItinerary` (mock `PlannerAiService.enhanceItinerary` and verify it is invoked)
  - Write example test: invalid request (days=0) throws `BadRequestException` with message "Days must be between 1 and 14"
  - Run all preservation tests on UNFIXED code
  - **EXPECTED OUTCOME**: All preservation tests PASS on unfixed code — confirms baseline behavior to preserve
  - Mark task complete when tests are written, run, and passing on unfixed code
  - _Requirements: 3.1, 3.2, 3.3, 3.5_

- [ ] 3. Fix: correct @Value defaults, add startup warning, introduce AiFallbackResult, update PlannerService

  - [x] 3.1 Correct `@Value` defaults in `PlannerAiService`
    - In `Backend/src/main/java/com/riya/smarttravel/service/PlannerAiService.java`, change `@Value("${planner.ai.enabled:false}")` to `@Value("${planner.ai.enabled:true}")`
    - Change `@Value("${planner.ai.max-tokens:1200}")` to `@Value("${planner.ai.max-tokens:4000}")`
    - These align the in-code defaults with `application.properties` (`${PLANNER_AI_ENABLED:true}` and `${PLANNER_AI_MAX_TOKENS:4000}`)
    - _Bug_Condition: isBugCondition(request, candidates) where aiEnabled defaults to false and aiMaxTokens defaults to 1200_
    - _Expected_Behavior: isAiConfigured() returns true when aiApiKey is non-blank and aiEnabled defaults to true_
    - _Preservation: DB-sourced responses for well-covered cities are unaffected by this change_
    - _Requirements: 2.1, 2.2_

  - [x] 3.2 Add `@PostConstruct` warning log when API key is missing
    - Add `import jakarta.annotation.PostConstruct;` to `PlannerAiService` (Lombok `@Slf4j` is already present)
    - Add a `@PostConstruct` method `validateAiConfiguration()` that logs a WARN when `aiEnabled == true` and `aiApiKey` is null or blank: `log.warn("AI itinerary generation is DISABLED: OPENROUTER_API_KEY is not set. Set the environment variable to enable AI-powered itineraries.")`
    - _Bug_Condition: aiApiKey is blank and no warning is emitted_
    - _Expected_Behavior: WARN log containing "OPENROUTER_API_KEY" is emitted at startup_
    - _Requirements: 2.3_

  - [x] 3.3 Introduce `AiFallbackStatus` enum and `AiFallbackResult` record
    - Create `AiFallbackStatus` enum with values `NOT_CONFIGURED`, `CALL_FAILED`, `SUCCESS` in the `service` package (as a top-level file or nested type in `PlannerAiService`)
    - Create `AiFallbackResult` record with fields `AiFallbackStatus status` and `PlannerResponseDto response`, plus static factory methods: `notConfigured()`, `failed()`, `success(PlannerResponseDto dto)`, and a convenience method `isSuccess()`
    - _Bug_Condition: generateFallbackItinerary returns Optional.empty() for both NOT_CONFIGURED and CALL_FAILED, making them indistinguishable_
    - _Expected_Behavior: PlannerService can distinguish NOT_CONFIGURED from CALL_FAILED and respond appropriately_
    - _Requirements: 2.5_

  - [ ] 3.4 Update `generateFallbackItinerary` to return `AiFallbackResult`
    - Change the return type of `generateFallbackItinerary` in `PlannerAiService` from `Optional<PlannerResponseDto>` to `AiFallbackResult`
    - Return `AiFallbackResult.notConfigured()` when `!isAiConfigured()`
    - Return `AiFallbackResult.failed()` in the `catch` block (after logging the error)
    - Return `AiFallbackResult.success(response)` on successful parse
    - _Bug_Condition: Optional.empty() returned for both missing config and call failure_
    - _Expected_Behavior: AiFallbackResult.status distinguishes NOT_CONFIGURED from CALL_FAILED_
    - _Preservation: enhanceItinerary still returns Optional<AiEnhancementDto> — only generateFallbackItinerary signature changes_
    - _Requirements: 2.5_

  - [ ] 3.5 Update `PlannerService` to handle `AiFallbackResult` and throw for unknown cities
    - In `Backend/src/main/java/com/riya/smarttravel/service/PlannerService.java`, update the call site of `generateFallbackItinerary` to use `AiFallbackResult` instead of `Optional`
    - Add `import lombok.extern.slf4j.Slf4j;` and `@Slf4j` to `PlannerService` if not already present
    - Add structured INFO log at start of `generate()`: `log.info("Trip request received for city={}, days={}, travellerType={}", normalizedCity, request.getDays(), travellerType)`
    - After candidate resolution, add: `log.info("Destination detected: city={}, dbCandidates={}, minimumRequired={}, usingAI={}", normalizedCity, candidates.size(), minimumDbCoverage, isCityUnknownOrSparse)`
    - Replace the `.orElseGet(...)` lambda with explicit `AiFallbackResult` status handling:
      - If `aiResult.isSuccess()`: return `aiResult.response()`
      - If `candidates.isEmpty()` and `aiResult.status() == NOT_CONFIGURED`: log WARN and throw `ResourceNotFoundException("No places found for city '" + request.getCity() + "'. AI generation is disabled — set OPENROUTER_API_KEY to enable it.")`
      - If `candidates.isEmpty()` and `aiResult.status() == CALL_FAILED`: throw `ResourceNotFoundException("No places found for city '" + request.getCity() + "' and AI generation failed. Please try again later.")`
      - If `!candidates.isEmpty()` (sparse): log WARN and return `buildDbResponse(candidates, request, travellerType, maxHoursPerDay)`
    - _Bug_Condition: isCityUnknownOrSparse=true AND isAiConfigured()=false → silent sparse/rule-based fallback_
    - _Expected_Behavior: unknown city with AI configured returns AI_GENERATED; unknown city without AI throws ResourceNotFoundException with clear message_
    - _Preservation: well-covered cities (candidates.size() >= minimumDbCoverage) are unaffected — the new logic only runs inside the existing `if (candidates.isEmpty() || isCityUnknownOrSparse)` branch_
    - _Requirements: 2.1, 2.2, 2.4, 2.5, 3.1, 3.2, 3.3, 3.5_

  - [ ] 3.6 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - AI Called for Unknown/Sparse Cities
    - **IMPORTANT**: Re-run the SAME test from task 1 — do NOT write a new test
    - The test from task 1 encodes the expected behavior: `isAiConfigured()` returns `true` when `aiEnabled` defaults to `true` and `aiApiKey` is non-blank
    - Run `PlannerAiServiceBugConditionTest` and `PlannerServiceBugConditionTest` from step 1
    - **EXPECTED OUTCOME**: Tests PASS — `isAiConfigured()` now returns `true` with a valid key, and the WARN log is emitted when key is blank
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

  - [ ] 3.7 Verify preservation tests still pass
    - **Property 2: Preservation** - DB Response Unchanged for Well-Covered Cities
    - **IMPORTANT**: Re-run the SAME tests from task 2 — do NOT write new tests
    - Run `PlannerServicePreservationTest` from step 2
    - **EXPECTED OUTCOME**: All preservation tests PASS — well-covered cities still return `dataSource == "DATABASE"`, filter-only requests still work, validation errors still throw `BadRequestException`, `enhanceWithAi` still calls `enhanceItinerary`
    - Confirm no regressions introduced by the fix

- [ ] 4. Checkpoint — Ensure all tests pass
  - Run the full test suite: `./mvnw test` from the `Backend/` directory
  - Ensure `PlannerAiServiceBugConditionTest` passes (Property 1: Bug Condition → Expected Behavior)
  - Ensure `PlannerServiceBugConditionTest` passes (Property 1: Bug Condition → Expected Behavior)
  - Ensure `PlannerServicePreservationTest` passes (Property 2: Preservation)
  - Ensure all pre-existing tests continue to pass (no regressions)
  - If any test fails, diagnose and fix before marking complete
  - Ask the user if questions arise about ambiguous behavior

- [ ] 5. Frontend: surface AI vs DB source clearly and handle error responses

  - [ ] 5.1 Fix `fetchItinerary` error handling in `Frontend/js/api.js`
    - The current `catch` block calls `buildFallbackItinerary(requestBody)` which does not exist in the codebase — this silently fails and shows a broken fallback for unknown cities
    - Remove the `buildFallbackItinerary` call and replace with a user-facing error message that distinguishes the two backend error cases:
      - If `response.status === 404` and the error message contains "AI generation is disabled": show `alert('No places found for "${city}". AI itinerary generation is disabled on the server — please contact the administrator.')` and return early (do not attempt a fallback)
      - If `response.status === 404` and the message contains "AI generation failed": show `alert('No places found for "${city}" and the AI service is temporarily unavailable. Please try again later.')` and return early
      - For all other non-OK responses: keep the existing generic `alert('Sorry, we could not generate your itinerary right now. Please try again in a moment.')`
    - Update the loading overlay message in `showPlannerLoading()` to be neutral: change `"AI is crafting your perfect trip to"` to `"Generating your itinerary for"` so it is accurate for both AI and DB responses
    - _Requirements: 2.5_

  - [ ] 5.2 Fix `sourceText` label and badge styling in `Frontend/js/itinerary.js`
    - In `renderItinerary()`, the `sourceText` fallback is `'Saved Plan'` for `dataSource === 'DATABASE'` — change it to `'Database'` so the source chip accurately reflects the data origin:
      ```javascript
      const sourceText = data.dataSource === 'AI_GENERATED'
          ? 'AI Generated'
          : data.dataSource === 'HYBRID'
              ? 'AI Enhanced'
              : data.dataSource === 'DATABASE'
                  ? 'Database'
                  : 'Saved Plan';
      ```
    - Apply the same fix to the identical `sourceText` block in `renderLegacyItineraryShell()` (same file, same pattern)
    - In `Frontend/css/itinerary.css`, add a distinct style for the AI Generated badge so it stands out visually from the Database badge. Add a rule targeting `.featured-pill` or the source chip `<span>` when it contains "AI Generated" — use a teal/green accent (`background: #e0f7f4; color: #00796b; border: 1px solid #80cbc4;`) to match the existing teal brand color used in the loading spinner
    - _Requirements: 2.4, 3.5_

  - [ ] 5.3 Add an "AI Generated" notice banner on the itinerary page for AI-sourced results
    - In `renderItinerary()` in `Frontend/js/itinerary.js`, after the hero section is rendered, check `data.dataSource === 'AI_GENERATED'` and inject a non-intrusive info banner immediately above the day-by-day section:
      ```javascript
      if (data.dataSource === 'AI_GENERATED') {
          const banner = document.createElement('div');
          banner.className = 'ai-generated-banner';
          banner.innerHTML = `<i class="fas fa-wand-magic-sparkles"></i> This itinerary was generated by AI because <strong>${escapeHtml(tripCity)}</strong> is not yet in our local database. Places and timings are AI suggestions — verify locally before travel.`;
          // Insert before the day-by-day section
          const daySection = container.querySelector('.trip-day-list')?.closest('section');
          if (daySection) daySection.insertAdjacentElement('beforebegin', banner);
      }
      ```
    - Add the `.ai-generated-banner` CSS rule to `Frontend/css/itinerary.css`:
      ```css
      .ai-generated-banner {
          background: #e0f7f4;
          border-left: 4px solid #14B8A6;
          border-radius: 8px;
          padding: 12px 16px;
          margin-bottom: 16px;
          font-size: 14px;
          color: #004d40;
          line-height: 1.5;
      }
      .ai-generated-banner i {
          margin-right: 6px;
          color: #14B8A6;
      }
      ```
    - _Requirements: 2.4_

- [ ] 6. Write budget bug condition exploration test
  - **Property 6: Deterministic Variance** — Budget Daily Cost Within Tier Range
  - **CRITICAL**: This test MUST FAIL on unfixed code — failure confirms the bug exists
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior — it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate that `mapBudgetLevelToDaily()` uses destination multipliers and returns costs outside realistic tier ranges
  - Create `BudgetUtilsBugConditionTest` in `Frontend/test/` (or as a Jest test file)
  - Test that `mapBudgetLevelToDaily("BUDGET", "Delhi")` returns ₹1,800 (using 1.2x multiplier), demonstrating the bug
  - Test that `mapBudgetLevelToDaily("BUDGET", "Goa")` returns ₹1,350 (using 0.9x multiplier), showing inconsistency
  - Test that `calculateCategoryBreakdown(₹3,001)` results in category sum ≠ ₹3,001, demonstrating rounding error
  - Test that `DESTINATION_OVERRIDES` contains hardcoded multipliers
  - Run tests on UNFIXED code
  - **EXPECTED OUTCOME**: Tests FAIL — destination multipliers are used, costs fall outside tier ranges, category sum has rounding errors
  - Document counterexamples found (e.g., `mapBudgetLevelToDaily("BUDGET", "Delhi")` returns ₹1,800 instead of ₹1,200–₹1,800 range)
  - Mark task complete when tests are written, run, and failure is documented
  - _Requirements: 3.1, 3.2, 3.4, 3.5_

- [ ] 7. Write budget preservation property tests (BEFORE implementing fix)
  - **Property 8: Preservation** — Well-Known City Costs Within ±5%
  - **IMPORTANT**: Follow observation-first methodology
  - Observe behavior on UNFIXED code for well-known cities: call `mapBudgetLevelToDaily("COMFORT", "Goa")` and record the result
  - Observe: call for "Delhi" and "Mumbai" with "BUDGET" tier and record results
  - Create `BudgetUtilsPreservationTest` in `Frontend/test/`
  - Write property-based test: for any well-known city (Goa, Delhi, Mumbai) and tier, assert the fixed daily cost is within ±5% of the original
  - Write example test: for a city not in database (e.g., "Shimla"), `getDestinationSeed("Shimla")` returns a consistent value across calls
  - Run all preservation tests on UNFIXED code, recording baseline values
  - **EXPECTED OUTCOME**: All preservation tests PASS on unfixed code — confirms baseline values to preserve
  - Mark task complete when tests are written, run, and passing on unfixed code, with baseline values documented
  - _Requirements: 5.6_

- [ ] 8. Fix: remove DESTINATION_OVERRIDES, add getDestinationSeed, recalibrate tier ranges, implement deterministic variance

  - [ ] 8.1 Remove destination multipliers and add `getDestinationSeed` in `Frontend/js/utils.js`
    - Empty or remove `DESTINATION_OVERRIDES` object (set to `{}`)
    - Add `getDestinationSeed(destination)` function that returns a stable numeric seed in [0, 1) based on destination name hash
    - Use a simple hash algorithm (e.g., sum of char codes, modulo 1000, divide by 1000)
    - _Bug_Condition: mapBudgetLevelToDaily uses destination multipliers and returns costs outside tier ranges_
    - _Expected_Behavior: mapBudgetLevelToDaily returns costs within tier variance range using destination seed_
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

  - [ ] 8.2 Recalibrate `getBudgetPreferenceRange` in `Frontend/js/utils.js`
    - Update the function to return calibrated global ranges:
      - Budget: ₹1,000–₹2,000
      - Comfort: ₹2,000–₹3,500
      - Premium: ₹4,000–₹8,000
      - Luxury: ₹10,000–₹15,000
    - _Bug_Condition: budget tiers use unrealistic ranges_
    - _Expected_Behavior: tier ranges are realistic and documented_
    - _Requirements: 4.2, 4.3, 4.4, 4.5_

  - [ ] 8.3 Update `mapBudgetLevelToDaily` to use deterministic variance in `Frontend/js/utils.js`
    - Replace destination multiplier logic with:
      - Define variance range for each tier (tighter than global range, e.g., Budget: ₹1,200–₹1,800)
      - Compute step count: `(variance.max - variance.min) / variance.step + 1`
      - Get destination seed: `getDestinationSeed(destination)`
      - Map seed to step index: `Math.floor(seed * (stepCount - 1))`
      - Calculate daily cost: `variance.min + (stepIndex * variance.step)`
      - Clamp to global tier bounds
    - _Bug_Condition: daily cost uses destination multipliers_
    - _Expected_Behavior: daily cost is deterministic within tier variance range_
    - _Preservation: well-known city costs remain within ±5% of original_
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

  - [ ] 8.4 Add `calculateCategoryBreakdown` in `Frontend/js/utils.js`
    - Create new function that allocates budget categories:
      - Hotel = `Math.round(Total * 0.40)`
      - Food = `Math.round(Total * 0.18)`
      - Transport = `Math.round(Total * 0.20)`
      - Activities = `Total - Hotel - Food - Transport` (computed last to guarantee exact sum)
    - Return object with `{ hotel, food, transport, activities, total }`
    - _Bug_Condition: category breakdown does not sum to total_
    - _Expected_Behavior: category breakdown sums to total exactly_
    - _Requirements: 4.7_

  - [ ] 8.5 Update `getTripDestinationName` in `Frontend/js/utils.js`
    - Ensure destination is resolved strictly from data in order:
      - `data?.selectedDestination` → `data?.plannerRequest?.city` → `data?.destination` → `data?.city` → `'Destination'`
    - _Bug_Condition: destination not resolved correctly_
    - _Expected_Behavior: destination is reliably extracted_
    - _Requirements: (foundational for budget calculation)_

  - [ ] 8.6 Update `preferenceLabels` in `Frontend/js/itinerary.js`
    - Update budget tier labels to reflect clarified ranges:
      - "Budget Traveler (₹1,000–₹2,000/day)"
      - "Comfortable Explorer (₹2,000–₹3,500/day)"
      - "Premium Experience (₹4,000–₹8,000/day)"
      - "Luxury Adventure (₹10,000+/day)"
    - _Requirements: 4.2, 4.3, 4.4, 4.5_

  - [ ] 8.7 Redesign `renderBudgetPanel` in `Frontend/js/itinerary.js`
    - Update budget card to clarify "Estimated" and show full context:
      - Header: "Estimated Budget"
      - Subtitle: "[Destination] • [Budget Type] Budget ₹[Daily Rate] per traveler/day"
      - Context block: Duration, Travelers, Budget Type
      - Breakdown: Hotel, Food, Transport, Activities with category sum validation
      - Disclaimer: "Estimated costs are approximate and may vary by season, hotel choice, and activities."
    - Call `getTravelerCount(preferences)` to determine number of travelers
    - Call `calculateCategoryBreakdown(totalBudget)` to get category allocation
    - _Requirements: 4.6, 4.8_

  - [ ] 8.8 Update budget dropdown in `Frontend/html/planner.html`
    - Update `<select id="budgetLevel">` options to match clarified ranges
    - _Requirements: 4.2, 4.3, 4.4, 4.5_

  - [ ] 8.9 Add CSS styles in `Frontend/css/itinerary.css`
    - Add `.budget-card`, `.budget-header`, `.budget-subtitle`, `.budget-context`, `.budget-breakdown`, `.budget-categories`, `.category`, `.budget-disclaimer` CSS rules
    - Use teal accent color (#00897b) consistent with the brand
    - Ensure responsive layout for category grid
    - _Requirements: 4.8_

  - [ ] 8.10 Verify budget bug condition exploration test now passes
    - **Property 6: Expected Behavior** — Deterministic Variance
    - **IMPORTANT**: Re-run the SAME test from task 6 — do NOT write a new test
    - Run `BudgetUtilsBugConditionTest` from step 6
    - **EXPECTED OUTCOME**: Tests PASS — daily costs are within tier variance ranges, no destination multipliers used, category sum equals total
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.7_

  - [ ] 8.11 Verify budget preservation tests still pass
    - **Property 8: Preservation** — Well-Known City Costs Within ±5%
    - **IMPORTANT**: Re-run the SAME tests from task 7 — do NOT write new tests
    - Run `BudgetUtilsPreservationTest` from step 7
    - **EXPECTED OUTCOME**: All preservation tests PASS — well-known city daily costs are within ±5% of original baseline values
    - Confirm no regressions introduced by the fix

- [ ] 9. Checkpoint — Ensure all tests pass
  - Run frontend test suite (Jest or your test runner: `npm test` or `yarn test` from `Frontend/`)
  - Ensure `BudgetUtilsBugConditionTest` passes (Property 6: Bug Condition → Expected Behavior)
  - Ensure `BudgetUtilsPreservationTest` passes (Property 8: Preservation)
  - Ensure all pre-existing tests continue to pass (no regressions)
  - If any test fails, diagnose and fix before marking complete
  - Ask the user if questions arise about ambiguous behavior
  - Run the full Maven test suite: `./mvnw test` from `Backend/` to ensure backend fixes still pass

- [ ] 10. Manual verification — Budget estimation across test cases
  - Test Case 1: Delhi, 4 Days, 4 Travelers, Comfort tier
    - Expected daily rate: ₹2,500–₹3,200 (use `mapBudgetLevelToDaily("COMFORT", "Delhi")`)
    - Expected total: (daily rate × 4 travelers × 4 days)
    - Expected range: ₹40,000–₹51,200
    - Verify category breakdown sums to total exactly
  - Test Case 2: Mumbai, 2 Travelers, 5 Days, Comfort tier
    - Expected daily rate: ₹2,500–₹3,200
    - Expected total: (daily rate × 2 × 5)
    - Expected range: ₹25,000–₹32,000
    - Verify breakdown
  - Test Case 3: Goa, 2 Travelers, 5 Days, Comfort tier
    - Expected daily rate: ₹2,500–₹3,200
    - Expected total: ₹25,000–₹32,000
    - Verify within ±5% of original (preservation check)
  - Test Case 4: Jaipur, 2 Travelers, 3 Days, Budget tier
    - Expected daily rate: ₹1,200–₹1,800
    - Expected total: (daily rate × 2 × 3)
    - Expected range: ₹7,200–₹10,800
    - Verify breakdown
  - Verify UI:
    - Budget card header says "Estimated Budget"
    - Daily rate is labeled "per traveler/day"
    - Category breakdown displays correctly
    - Disclaimer appears below the breakdown
  - Verify destination seed:
    - Call `getDestinationSeed("Shimla")` multiple times → same value each time
    - Call `getDestinationSeed("Delhi")` multiple times → same value each time
    - Compare seeds to verify determinism (no randomness)
  - _Requirements: 4.1–4.8, 5.6_

## Notes

- **Issue 1 (AI Itinerary)**: The backend fix is intentionally minimal: only two `@Value` defaults change, one `@PostConstruct` method is added, one return type changes, and one call site in `PlannerService` is updated.
- **Issue 2 (Budget Estimation)**: The frontend fix removes hardcoded multipliers, adds deterministic variance using destination hashing, recalibrates tier ranges, and ensures exact category sum. No backend API changes are required.
- The `enhanceItinerary` method in `PlannerAiService` is NOT changed — it still returns `Optional<AiEnhancementDto>`.
- Frontend tasks (5.1–5.3 and 8.1–8.9) are independent of the backend fix and can be done in parallel or sequentially.
- Property-based tests should use Jest (JavaScript) for frontend or a library available in the project (e.g. jqwik via `net.jqwik:jqwik`) for Java.
- Run `./mvnw test` from `Backend/` to execute the Spring Boot test suite.
- Run `npm test` or `yarn test` from `Frontend/` to execute the frontend test suite.
