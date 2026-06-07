# Bugfix Requirements Document

## Introduction

This bugfix addresses two critical issues in SmartTravel affecting user experience for trip planning:

**Issue 1: AI Itinerary Fallback for Unknown Cities**
When a user enters a city that is not present in the local database (e.g. Shimla, Paris, Udaipur) on the trip planner page, the backend should immediately call OpenRouter (gpt-4o-mini) to generate a full AI-powered day-by-day itinerary. Currently, the AI fallback is silently skipped when the `OPENROUTER_API_KEY` environment variable is not set or when `planner.ai.enabled` is not explicitly configured, causing the planner to return a sparse, empty, or rule-based fallback response instead of a rich AI-generated itinerary. This degrades the user experience for any city not covered by the local database.

**Issue 2: Budget Estimation Accuracy and Consistency**
The budget calculator currently provides rigid, exact cost estimates with destination-specific hardcoded multipliers that create inconsistent and often unrealistic per-traveler daily costs. Budget tiers are not calibrated to realistic price ranges. The budget should represent a realistic estimate rather than an exact rigid calculation. To achieve consistency and realism, the daily budget rate will vary deterministically using a name hash of the selected destination within the tier's reasonable range, with all destination-specific hardcoded multipliers removed.

## Bug Analysis

### Current Behavior (Defect)

**Issue 1: AI Itinerary Fallback for Unknown Cities**

1.1 WHEN a city name is submitted to `/api/planner/generate` AND the city has zero matching places in the local database THEN the system returns an empty or rule-based fallback itinerary instead of calling OpenRouter

1.2 WHEN a city name is submitted AND the city has fewer places in the database than `days * 2` (sparse coverage) THEN the system attempts the AI fallback but silently skips it if `OPENROUTER_API_KEY` is blank, returning a sparse DB-sourced itinerary

1.3 WHEN `planner.ai.enabled` is `false` or `OPENROUTER_API_KEY` is an empty string THEN the system returns `Optional.empty()` from `generateFallbackItinerary` without logging a user-visible error or actionable message, making the failure invisible to the operator

1.4 WHEN the AI call is skipped due to missing configuration AND the city is completely unknown to the DB THEN the system throws a `ResourceNotFoundException` with a message referencing OpenRouter key/billing, which is surfaced as an error to the frontend rather than a graceful degraded response

**Issue 2: Budget Estimation Accuracy and Consistency**

1.5 WHEN a user selects a budget tier for trip planning THEN the system returns a rigid, inflexible daily per-traveler cost that uses destination-specific hardcoded multipliers (e.g. different costs for Delhi vs Goa for the same tier), creating inconsistent and unrealistic estimates

1.6 WHEN the backend computes budget categories (Hotel, Food, Transport, Activities) THEN the system uses approximate ratios that may not sum to the total budget exactly, causing discrepancies between per-category costs and the displayed total

1.7 WHEN the frontend displays budget information THEN it shows budget as an exact fixed cost instead of a realistic estimate, implying precision that the system cannot guarantee

1.8 WHEN a user selects Budget Traveler tier THEN the system applies costs outside the realistic range (e.g., ₹2,500 per day instead of ₹1,000–₹2,000), making estimates unrealistic for budget-conscious travelers

### Expected Behavior (Correct)

**Issue 1: AI Itinerary Fallback for Unknown Cities**

2.1 WHEN a city name is submitted AND the city has zero matching places in the local database THEN the system SHALL immediately call OpenRouter (gpt-4o-mini) to generate a full day-by-day itinerary for that city

2.2 WHEN a city name is submitted AND the city has fewer places than `days * 2` in the database (sparse coverage) THEN the system SHALL call OpenRouter to generate a complete AI-powered itinerary rather than returning the sparse DB results

2.3 WHEN `OPENROUTER_API_KEY` is not set or is blank THEN the system SHALL log a clear warning at startup or at call time indicating that AI itinerary generation is disabled, so operators can diagnose the issue

2.4 WHEN the AI call succeeds for an unknown city THEN the system SHALL return a `PlannerResponseDto` with `dataSource` set to `"AI_GENERATED"` and a fully populated `itinerary` list covering the requested number of days

2.5 WHEN the AI call fails (network error, quota exceeded, invalid key) for an unknown city THEN the system SHALL return a meaningful error response to the frontend rather than silently returning empty or sparse results

**Issue 2: Budget Estimation Accuracy and Consistency**

2.6 WHEN a user selects a budget tier THEN the system SHALL calculate a realistic daily per-traveler cost that falls deterministically within the tier's target range, varying by destination name (via name hash) to provide consistency without appearing artificially rigid

2.7 WHEN the budget tier is Budget Traveler THEN the system SHALL use the range ₹1,000–₹2,000 per traveler/day (with average target ₹1,500), varying deterministically within ₹1,200–₹1,800 in ₹100 increments

2.8 WHEN the budget tier is Comfortable Explorer (midrange) THEN the system SHALL use the range ₹2,000–₹3,500 per traveler/day (with average target ₹3,000), varying deterministically within ₹2,500–₹3,200 in ₹100 increments

2.9 WHEN the budget tier is Premium Experience THEN the system SHALL use the range ₹4,000–₹8,000 per traveler/day (with average target ₹6,000), varying deterministically within ₹4,500–₹7,500 in ₹100 increments

2.10 WHEN the budget tier is Luxury Adventure THEN the system SHALL use the range ₹10,000+ per traveler/day (with average target ₹10,000), varying deterministically within ₹10,000–₹15,000 in ₹100 increments

2.11 WHEN calculating the total trip budget THEN the system SHALL compute exactly: `Total = Per Traveler Daily Cost * Number of Travelers * Days`

2.12 WHEN distributing budget categories THEN the system SHALL allocate: Hotel 40%, Food 18%, Transport 20%, Activities 22%, and compute Activities as `Total - Hotel - Food - Transport` to guarantee the sum equals the total budget exactly (no rounding discrepancies)

2.13 WHEN displaying budget to the user THEN the system SHALL label it as "Estimated Budget" and show "per traveler/day" clearly, making it explicit that costs are approximate and may vary by season, hotel choice, and activities

### Unchanged Behavior (Regression Prevention)

3.1 WHEN a city is well-represented in the local database (places count >= `days * 2`) THEN the system SHALL CONTINUE TO generate the itinerary from the database and only use AI for optional enhancement (tips, aiSummary, additionalRecommendations)

3.2 WHEN `enhanceWithAi` is requested on a DB-sourced itinerary THEN the system SHALL CONTINUE TO call `enhanceItinerary` to add AI-generated tips and summary on top of the DB itinerary

3.3 WHEN no city is specified but other filters (region, category, mood, etc.) are provided THEN the system SHALL CONTINUE TO query the database using those filters and build the itinerary from DB results

3.4 WHEN the planner request is invalid (missing days, out-of-range values, missing preferences) THEN the system SHALL CONTINUE TO return a `BadRequestException` with the existing validation messages

3.5 WHEN a known city is requested with valid DB coverage THEN the system SHALL CONTINUE TO return `dataSource: "DATABASE"` in the response, preserving the existing data-source transparency for the frontend

3.6 WHEN calculating budget for existing well-known cities with strong database coverage THEN the results SHALL NOT materially change (within ±5% variance) so that user expectations for common destinations are preserved

## Bug Analysis Detail

### Issue 1: AI Itinerary Fallback for Unknown Cities


#### Bug Condition

The budget estimation bug manifests when a user selects a budget tier and the system calculates the daily per-traveler cost. The budget uses destination-specific hardcoded multipliers in `DESTINATION_OVERRIDES` (e.g., Delhi gets a 1.2x multiplier, Goa gets 0.9x), leading to inconsistent and unrealistic costs across destinations. Budget tiers are not calibrated to realistic ranges — for example, Budget Traveler may be estimated at ₹2,500/day instead of ₹1,000–₹2,000/day. Additionally, the budget category breakdown uses approximate ratios that do not sum to exactly the total budget, causing arithmetic discrepancies.

**Formal Specification:**

```
FUNCTION isBudgetBugCondition(tier, destination)
  INPUT: tier of type BudgetTier, destination of type String
  OUTPUT: boolean

  tierRange  := getBudgetPreferenceRange(tier)
  tierMin    := tierRange.minDaily
  tierMax    := tierRange.maxDaily

  dailyCost  := mapBudgetLevelToDaily(tier, destination)
  multiplier := DESTINATION_OVERRIDES.getOrDefault(destination, 1.0)

  isBuggy := dailyCost < tierMin 
             OR dailyCost > tierMax
             OR multiplier != 1.0
             OR categorySum(dailyCost) != dailyCost

  RETURN isBuggy
END FUNCTION
```

#### Examples

- **Budget tier, Delhi**: `DESTINATION_OVERRIDES["Delhi"] = 1.2` → daily cost = ₹1,500 * 1.2 = ₹1,800. But for Budget tier, realistic range is ₹1,000–₹2,000. The estimate is at the upper edge and not representative of actual Delhi budget travel.
- **Budget tier, small town**: No override → daily cost = default ₹1,500, but local costs may be ₹1,200. The estimate is rigid and not realistic.
- **Luxury tier, Goa**: `DESTINATION_OVERRIDES["Goa"] = 0.9` → daily cost = ₹10,000 * 0.9 = ₹9,000. For Luxury tier, realistic range is ₹10,000–₹15,000. The estimate is below the tier's floor.
- **Category breakdown**: Hotel (40%), Food (18%), Transport (20%), Activities (22%) = 100%. If total is ₹3,000, then Hotel = ₹1,200, Food = ₹540, Transport = ₹600, Activities = ₹660. Sum = ₹3,000 ✓. But if rounding is applied, the sum may be ₹3,001 or ₹2,999, creating confusion.

#### Root Cause Analysis

Based on source code analysis, the root causes are:

1. **Destination-specific multipliers**: `DESTINATION_OVERRIDES` in `utils.js` maps destination names to multipliers (e.g., `{ "Delhi": 1.2, "Goa": 0.9 }`). These multipliers create inconsistency because different destinations for the same tier get different costs, yet all are labeled with the same tier name.

2. **Misaligned budget tier ranges**: `getBudgetPreferenceRange(tier)` and `mapBudgetLevelToDaily(tier)` use static ranges that don't align with realistic costs. For example, Budget Traveler should be ₹1,000–₹2,000, but the current code may use ₹1,500–₹2,500.

3. **No destination-aware variance within tier**: The system does not vary the cost within a tier's range based on destination, so all Budget Traveler requests get the same daily cost (except for multipliers), appearing artificial and rigid.

4. **Approximate budget category ratios**: The category breakdown uses fixed percentages (40%, 18%, 20%, 22%) without ensuring the sum equals the total. Rounding errors or integer division may cause the allocated categories to not sum to the total.

5. **Unclear "Estimated" vs "Fixed"**: The frontend labels budget as "Budget" instead of "Estimated Budget", implying a fixed exact cost rather than an approximate estimate.

## Issue 1: AI Itinerary Fallback for Unknown Cities — Root Cause Analysis