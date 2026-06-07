package com.riya.smarttravel.service;

import com.riya.smarttravel.dto.AiEnhancementDto;
import com.riya.smarttravel.dto.PlannerRequest;
import com.riya.smarttravel.dto.PlannerResponseDto;
import com.riya.smarttravel.entity.Place;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Preservation Property Tests — Property 2: DB Response Unchanged for Well-Covered Cities
 *
 * These tests verify that the fix for the AI bug does NOT regress existing correct behavior:
 *   1. Cities with sufficient DB places (>= days*2) continue to return dataSource="DATABASE"
 *   2. Filter-only requests (no city) continue to return dataSource="DATABASE"
 *   3. DB-sourced responses still call enhanceItinerary when enhanceWithAi=true
 *   4. Invalid requests still throw BadRequestException with correct messages
 *
 * EXPECTED OUTCOME: ALL tests PASS on unfixed code — confirms baseline behavior to preserve.
 *
 * **Validates: Requirements 3.1, 3.2, 3.3, 3.5**
 */
@ExtendWith(MockitoExtension.class)
class PlannerServicePreservationTest {

    @Mock
    private PlaceRepository repository;

    @Mock
    private PlannerAiService plannerAiService;

    @InjectMocks
    private PlannerService service;

    @BeforeEach
    void setUp() {
        // Default stubs: AI returns notConfigured on generateFallbackItinerary (unfixed code behavior)
        lenient().when(plannerAiService.generateFallbackItinerary(any(), anyString(), anyInt(), anyDouble(), anyString()))
                .thenReturn(AiFallbackResult.notConfigured());
        lenient().when(plannerAiService.enhanceItinerary(any(), anyString(), nullable(String.class), nullable(String.class)))
                .thenReturn(Optional.empty());
    }

    // ==========================================================================
    // Property 2a: Well-Covered City → dataSource == "DATABASE"
    //
    // For any city where places.size() >= days * 2, PlannerService must build
    // the itinerary from the database and return dataSource="DATABASE".
    // This property must hold on BOTH unfixed and fixed code.
    //
    // Validates: Requirements 3.1, 3.5
    // ==========================================================================

    /**
     * Generates test cases: (days, placeCount) where placeCount >= days * 2.
     * days ranges from 1 to 7 (typical trip durations).
     * placeCount is set to exactly days*2, days*2+1, or days*3 to cover edge and interior.
     */
    static Stream<Arguments> wellCoveredCityCases() {
        List<Arguments> cases = new ArrayList<>();
        for (int days = 1; days <= 7; days++) {
            int minRequired = days * 2;
            // Exactly at threshold (days*2 places)
            cases.add(Arguments.of(days, minRequired));
            // One above threshold
            cases.add(Arguments.of(days, minRequired + 1));
            // Comfortably above threshold (days*3)
            cases.add(Arguments.of(days, days * 3));
        }
        return cases.stream();
    }

    /**
     * Property 2a: For any city with places.size() >= days*2, dataSource MUST be "DATABASE".
     *
     * This is a parameterized property test that exhaustively covers days 1–7
     * and placeCount at/above the well-covered threshold.
     *
     * **Validates: Requirements 3.1, 3.5**
     */
    @ParameterizedTest(name = "days={0}, placeCount={1} → dataSource=DATABASE")
    @MethodSource("wellCoveredCityCases")
    @DisplayName("Property 2a: Well-covered city (places >= days*2) always returns dataSource=DATABASE")
    void property_wellCoveredCity_alwaysReturnsDatabase(int days, int placeCount) {
        // Arrange
        PlannerRequest request = new PlannerRequest();
        request.setCity("Goa");
        request.setDays(days);
        request.setTravellerType("SOLO");

        // Populate mock with exactly placeCount places — all for "Goa"
        List<Place> places = buildPlaces("Goa", placeCount);

        // smartFilter is called with all nulls for non-city params
        when(repository.smartFilter(null, null, null, null, null, null, null, null, null, null))
                .thenReturn(places);

        // Act
        PlannerResponseDto response = service.generate(request);

        // Assert: well-covered city must use DATABASE source — AI fallback must NOT be triggered
        assertEquals("DATABASE", response.getDataSource(),
                String.format("PRESERVATION VIOLATED: days=%d, placeCount=%d (>= %d required). " +
                        "Expected dataSource=DATABASE but got %s. " +
                        "Well-covered cities must NOT be routed through AI fallback.",
                        days, placeCount, days * 2, response.getDataSource()));
    }

    /**
     * Concrete baseline observation: Goa with 20 places and days=3 must return DATABASE.
     * (20 >= 6 = days*2)
     *
     * **Validates: Requirements 3.1, 3.5**
     */
    @Test
    @DisplayName("Baseline: Goa 20 places days=3 → dataSource=DATABASE (20 >= 6)")
    void baseline_goa_20places_days3_returnsDatabase() {
        PlannerRequest request = new PlannerRequest();
        request.setCity("Goa");
        request.setDays(3);
        request.setTravellerType("SOLO");

        when(repository.smartFilter(null, null, null, null, null, null, null, null, null, null))
                .thenReturn(buildPlaces("Goa", 20));

        PlannerResponseDto response = service.generate(request);

        assertEquals("DATABASE", response.getDataSource(),
                "Goa with 20 places for 3 days (20 >= 6) must use DATABASE source.");
        assertEquals(3, response.getRequestedDays());
    }

    // ==========================================================================
    // Property 2b: Filter-Only Requests (no city) → dataSource == "DATABASE"
    //
    // When no city is specified but at least one filter (region/category/mood) is
    // provided, the system must query the DB and return dataSource="DATABASE".
    // No exception must be thrown for valid filter combinations.
    //
    // Validates: Requirements 3.3, 3.5
    // ==========================================================================

    /**
     * Generates filter-only request scenarios (no city, at least one filter set).
     * Each entry is: (region, category, mood, description).
     */
    static Stream<Arguments> filterOnlyCases() {
        return Stream.of(
                Arguments.of("North", null, null, "region only"),
                Arguments.of(null, "Heritage", null, "category only"),
                Arguments.of(null, null, "adventure", "mood only"),
                Arguments.of("South", "Nature", null, "region+category"),
                Arguments.of("West", null, "relaxing", "region+mood"),
                Arguments.of(null, "Spiritual", "peaceful", "category+mood"),
                Arguments.of("East", "Heritage", "cultural", "all three filters")
        );
    }

    /**
     * Property 2b: For any request with no city but at least one filter,
     * dataSource MUST be "DATABASE" and no exception must be thrown.
     *
     * **Validates: Requirements 3.3, 3.5**
     */
    @ParameterizedTest(name = "filter-only ({3}) → dataSource=DATABASE, no exception")
    @MethodSource("filterOnlyCases")
    @DisplayName("Property 2b: Filter-only requests (no city) always return dataSource=DATABASE without throwing")
    void property_filterOnlyRequest_returnsDatabase_noException(
            String region, String category, String mood, String description) {

        // Arrange
        PlannerRequest request = new PlannerRequest();
        request.setDays(2);
        request.setTravellerType("SOLO");
        request.setRegion(region);
        request.setCategory(category);
        request.setMood(mood);
        // city intentionally NOT set

        // Provide enough places for the filter query
        List<Place> places = buildPlaces("Delhi", 6);

        // smartFilter is called with the filter params; stubbing with any() for flexibility
        lenient().when(repository.smartFilter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(places);

        // Act & Assert: no exception thrown
        PlannerResponseDto response = service.generate(request);

        assertEquals("DATABASE", response.getDataSource(),
                String.format("PRESERVATION VIOLATED: filter-only request (%s) must return DATABASE. " +
                        "Got dataSource=%s.", description, response.getDataSource()));
    }

    // ==========================================================================
    // Example Test 1: enhanceWithAi=true on DB-sourced response still calls enhanceItinerary
    //
    // Validates: Requirement 3.2
    // ==========================================================================

    /**
     * Example: When a DB-sourced itinerary has enhanceWithAi=true,
     * PlannerAiService.enhanceItinerary must be invoked (not skipped).
     *
     * This verifies the AI enhancement layer is independent from the AI fallback layer.
     *
     * **Validates: Requirement 3.2**
     */
    @Test
    @DisplayName("Example: enhanceWithAi=true on DB-sourced response calls enhanceItinerary")
    void example_enhanceWithAi_true_callsEnhanceItinerary() {
        // Arrange: well-covered city — 6 places for 3 days (6 >= 6)
        PlannerRequest request = new PlannerRequest();
        request.setCity("Jaipur");
        request.setDays(3);
        request.setTravellerType("COUPLE");
        request.setEnhanceWithAi(true);  // AI enhancement requested

        when(repository.smartFilter(null, null, null, null, null, null, null, null, null, null))
                .thenReturn(buildPlaces("Jaipur", 6));

        // Mock enhanceItinerary to return an enhancement (simulating working AI enhancement)
        when(plannerAiService.enhanceItinerary(any(), anyString(), nullable(String.class), nullable(String.class)))
                .thenReturn(Optional.of(AiEnhancementDto.builder()
                        .aiSummary("Perfect couple trip to Jaipur")
                        .tips(List.of("Visit Amber Fort at sunrise", "Try dal baati churma"))
                        .additionalRecommendations(List.of())
                        .build()));

        // Act
        PlannerResponseDto response = service.generate(request);

        // Assert: dataSource is still DATABASE (not changed by enhancement)
        assertEquals("DATABASE", response.getDataSource(),
                "enhanceWithAi must not change dataSource from DATABASE.");

        // Assert: enhanceItinerary was actually called
        verify(plannerAiService).enhanceItinerary(any(), anyString(), nullable(String.class), nullable(String.class));

        // Assert: enhancement was merged into response
        assertEquals("Perfect couple trip to Jaipur", response.getAiSummary(),
                "AI summary from enhanceItinerary must be merged into the DB response.");
        assertEquals(2, response.getTips().size(),
                "AI tips from enhanceItinerary must be merged into the DB response.");
    }

    // ==========================================================================
    // Example Test 2: Invalid requests (days=0, days=15, no preferences) throw BadRequestException
    //
    // Validates: Requirement 3.4
    // ==========================================================================

    /**
     * Example: days=0 throws BadRequestException with message "Days must be between 1 and 14".
     *
     * **Validates: Requirement 3.4**
     */
    @Test
    @DisplayName("Example: days=0 throws BadRequestException with correct message")
    void example_invalidDays_zero_throwsBadRequestException() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(0);
        request.setCity("Goa");
        request.setTravellerType("SOLO");

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.generate(request),
                "days=0 must throw BadRequestException");

        assertEquals("Days must be between 1 and 14", ex.getMessage(),
                "BadRequestException must carry the exact validation message.");
    }

    /**
     * Example: days=15 throws BadRequestException with message "Days must be between 1 and 14".
     *
     * **Validates: Requirement 3.4**
     */
    @Test
    @DisplayName("Example: days=15 throws BadRequestException (out of upper bound)")
    void example_invalidDays_fifteen_throwsBadRequestException() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(15);
        request.setCity("Goa");
        request.setTravellerType("SOLO");

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.generate(request),
                "days=15 must throw BadRequestException");

        assertEquals("Days must be between 1 and 14", ex.getMessage(),
                "BadRequestException must carry the exact validation message.");
    }

    /**
     * Example: No preferences (no city, no region, no category, etc.) throws BadRequestException.
     *
     * **Validates: Requirement 3.4**
     */
    @Test
    @DisplayName("Example: request with no preferences throws BadRequestException")
    void example_noPreferences_throwsBadRequestException() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(3);
        // All filter fields left null/default

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.generate(request),
                "Request with no preferences must throw BadRequestException");

        assertTrue(ex.getMessage() != null && !ex.getMessage().isBlank(),
                "BadRequestException must have a descriptive message.");
    }

    // ==========================================================================
    // Helpers
    // ==========================================================================

    /**
     * Creates a list of {@code count} Place objects all belonging to the given city.
     * Places are given distinct IDs so they pass dedup checks in PlannerService.
     */
    private List<Place> buildPlaces(String city, int count) {
        List<Place> places = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Place place = new Place();
            place.setPlaceId(city.toLowerCase() + "-" + i);
            place.setPlaceName(city + " Place " + i);
            place.setCity(city);
            place.setState("State");
            place.setRegion("North");
            place.setCategory("Heritage");
            place.setSignificance("Historic landmark");
            place.setRating(4.5);
            place.setRecommendedDurationHours(2.0);
            place.setPriority("Recommended");
            place.setSafetyScore(9.0);
            place.setCleanlinessScore(8.5);
            place.setBestTimeToVisit("Oct-Mar");
            place.setIdealVisitTime("Morning");
            places.add(place);
        }
        return places;
    }
}
