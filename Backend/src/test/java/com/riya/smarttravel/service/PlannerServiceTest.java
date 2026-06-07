package com.riya.smarttravel.service;

import com.riya.smarttravel.dto.PlannerRequest;
import com.riya.smarttravel.dto.PlannerResponseDto;
import com.riya.smarttravel.dto.AiEnhancementDto;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.exception.ResourceNotFoundException;
import com.riya.smarttravel.entity.Place;
import com.riya.smarttravel.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlannerServiceTest {

    @Mock
    private PlaceRepository repository;

        @Mock
        private PlannerAiService plannerAiService;

    @InjectMocks
    private PlannerService service;

        @BeforeEach
        void setup() {
            lenient().when(plannerAiService.generateFallbackItinerary(any(), nullable(String.class), anyInt(), anyDouble(), anyString()))
            .thenReturn(AiFallbackResult.notConfigured());
            lenient().when(plannerAiService.enhanceItinerary(any(), anyString(), nullable(String.class), nullable(String.class)))
            .thenReturn(Optional.empty());
        }

    @Test
    void generateBuildsMultiDayItinerary() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(2);
        request.setTravellerType("SOLO");
        request.setRegion("North");
        request.setMinRating(4.0);
        request.setMaxHoursPerDay(4.0);

        when(repository.smartFilter("North", null, null, null, null, null, null, null, null, 4.0))
                .thenReturn(List.of(
                        place("IND001", "Jaipur", 4.8, 2.0, "Must Visit"),
                        place("IND002", "Jaipur", 4.6, 2.0, "Recommended"),
                        place("IND003", "Jaipur", 4.5, 2.5, "Recommended")
                ));

        PlannerResponseDto response = service.generate(request);

        assertEquals(2, response.getRequestedDays());
        assertEquals(2, response.getGeneratedDays());
        assertEquals(3, response.getTotalPlaces());
        assertEquals("SOLO", response.getTravellerType());
        assertEquals(2, response.getItinerary().size());
        assertEquals(2, response.getItinerary().get(0).getPlaces().size());
        assertEquals(1, response.getItinerary().get(1).getPlaces().size());
    }

    @Test
    void generateRejectsMissingPreferences() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(2);

        assertThrows(BadRequestException.class, () -> service.generate(request));
    }

    @Test
    void generateRejectsUnsupportedTravellerType() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(2);
        request.setTravellerType("business");

        assertThrows(BadRequestException.class, () -> service.generate(request));
    }

    @Test
    void generateThrowsNotFoundWhenNoCandidatesExist() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(2);
        request.setRegion("West");

        when(repository.smartFilter("West", null, null, null, null, null, null, null, null, null))
                .thenReturn(List.of());
        when(repository.findAll()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.generate(request));
    }

        @Test
        void generateUsesAiFallbackWhenRepositoryReturnsEmpty() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(2);
        request.setCity("Kolkata");
        request.setTravellerType("COUPLE");

        when(repository.smartFilter(null, null, null, null, null, null, null, null, null, null))
            .thenReturn(List.of());

        PlannerResponseDto aiFallback = PlannerResponseDto.builder()
            .requestedDays(2)
            .generatedDays(2)
            .totalPlaces(3)
            .travellerType("COUPLE")
            .dataSource("AI_GENERATED")
            .maxHoursPerDay(8.0)
            .totalTripHours(10.5)
            .summary("AI fallback itinerary")
            .itinerary(List.of())
            .build();

        when(plannerAiService.generateFallbackItinerary(any(), anyString(), anyInt(), anyDouble(), anyString()))
            .thenReturn(AiFallbackResult.success(aiFallback));

        PlannerResponseDto response = service.generate(request);

        assertEquals("AI_GENERATED", response.getDataSource());
        assertEquals("COUPLE", response.getTravellerType());
        }

    @Test
    void generateRelaxesFiltersBeforeAiFallback() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(1);
        request.setCity("Kolkata");
        request.setTravellerType("FAMILY");
        request.setBudgetLevel("Medium");
        request.setMinRating(4.0);
        request.setEnhanceWithAi(true);

        when(repository.smartFilter(null, null, null, "Medium", null, null, null, null, null, 4.0))
                .thenReturn(List.of());
        when(repository.smartFilter(null, null, null, null, null, null, null, null, null, 4.0))
                .thenReturn(List.of(place("IND129", "Kolkata", 4.6, 2.0, "Must Visit")));

        PlannerResponseDto response = service.generate(request);

        assertEquals("DATABASE", response.getDataSource());
        assertEquals(1, response.getTotalPlaces());
        assertEquals("Kolkata", response.getItinerary().get(0).getLocation().getCity());
    }

    @Test
    void generateRuleBasedFallbackStaysInsideRequestedCity() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(1);
        request.setCity("Indore");
        request.setTravellerType("FAMILY");
        request.setMinRating(4.0);

        lenient().when(repository.smartFilter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        Place indore = place("IND777", "Indore", 4.5, 2.0, "Recommended");
        lenient().when(repository.findAll()).thenReturn(List.of(indore, place("IND999", "Mumbai", 4.9, 2.0, "Must Visit")));
        lenient().when(repository.findByCityContainingIgnoreCase(eq("indore"))).thenReturn(List.of(indore));

        // With the fix, for an unknown city with 0 DB places and AI not configured,
        // we throw an exception rather than returning rule-based fallback
        assertThrows(ResourceNotFoundException.class, () -> service.generate(request));
    }

    @Test
    void generateRuleBasedFallbackThrowsWhenRequestedCityUnavailable() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(1);
        request.setCity("Indore");
        request.setTravellerType("FAMILY");
        request.setMinRating(4.0);

        lenient().when(repository.smartFilter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        lenient().when(repository.findAll()).thenReturn(List.of(place("IND999", "Mumbai", 4.9, 2.0, "Must Visit")));
        lenient().when(repository.findByCityContainingIgnoreCase(eq("Indore"))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.generate(request));
    }

    @Test
    void generateCanEnhanceDatabaseItineraryWithAi() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(1);
        request.setRegion("North");
        request.setTravellerType("SOLO");
        request.setMinRating(4.0);
        request.setEnhanceWithAi(true);

        when(repository.smartFilter("North", null, null, null, null, null, null, null, null, 4.0))
                .thenReturn(List.of(place("IND001", "Jaipur", 4.8, 2.0, "Must Visit")));

        when(plannerAiService.enhanceItinerary(any(), anyString(), nullable(String.class), nullable(String.class)))
                .thenReturn(Optional.of(AiEnhancementDto.builder()
                        .aiSummary("Personalized solo summary")
                        .tips(List.of("Start early", "Use public transport"))
                .additionalRecommendations(List.of())
                        .build()));

        PlannerResponseDto response = service.generate(request);

        assertEquals("DATABASE", response.getDataSource());
        assertEquals("Personalized solo summary", response.getAiSummary());
        assertEquals(2, response.getTips().size());
    }

    @Test
    void generatePrefersNearbyPlacesForSameDay() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(1);
        request.setRegion("North");
        request.setTravellerType("SOLO");
        request.setMinRating(4.0);
        request.setMaxHoursPerDay(4.0);

        Place delhiA = place("IND008", "Delhi", 4.7, 2.0, "Must Visit");
        delhiA.setLatitude(28.6129);
        delhiA.setLongitude(77.2295);

        Place delhiB = place("IND010", "Delhi", 4.4, 2.0, "Recommended");
        delhiB.setLatitude(28.5535);
        delhiB.setLongitude(77.2588);

        Place badrinath = place("IND279", "Badrinath", 4.9, 2.0, "Recommended");
        badrinath.setLatitude(30.7440);
        badrinath.setLongitude(79.4930);

        when(repository.smartFilter("North", null, null, null, null, null, null, null, null, 4.0))
                .thenReturn(List.of(delhiA, badrinath, delhiB));

        PlannerResponseDto response = service.generate(request);

        assertEquals(1, response.getGeneratedDays());
        assertEquals(2, response.getTotalPlaces());
        assertEquals("Delhi", response.getItinerary().get(0).getLocation().getCity());
    }

    @Test
    void generateSupportsGroupAliasForFamilyAndFriends() {
        PlannerRequest request = new PlannerRequest();
        request.setDays(1);
        request.setRegion("North");
        request.setTravellerType("friends");
        request.setMinRating(4.0);
        request.setMaxHoursPerDay(4.0);

        when(repository.smartFilter("North", null, null, null, null, null, null, null, null, 4.0))
                .thenReturn(List.of(
                        place("IND001", "Jaipur", 4.8, 2.0, "Must Visit"),
                        place("IND002", "Jaipur", 4.6, 2.0, "Recommended")
                ));

        PlannerResponseDto response = service.generate(request);

        assertEquals("FRIENDS", response.getTravellerType());
        assertEquals(1, response.getGeneratedDays());
        assertEquals(2, response.getTotalPlaces());
    }

    @Test
    void generateDifferentiatesFriendsAndFamilySelection() {
        PlannerRequest friendsRequest = new PlannerRequest();
        friendsRequest.setDays(1);
        friendsRequest.setCity("Kolkata");
        friendsRequest.setTravellerType("friends");
        friendsRequest.setMinRating(4.0);
        friendsRequest.setMaxHoursPerDay(6.0);

        PlannerRequest familyRequest = new PlannerRequest();
        familyRequest.setDays(1);
        familyRequest.setCity("Kolkata");
        familyRequest.setTravellerType("family");
        familyRequest.setMinRating(4.0);
        familyRequest.setMaxHoursPerDay(6.0);

        Place nightlife = place("IND900", "Kolkata", 4.4, 2.0, "Recommended");
        nightlife.setCategory("Urban");
        nightlife.setCrowdLevel("High");
        nightlife.setMoodTags("social,fun,activity");
        nightlife.setDescription("Night activity and social scene");

        Place spiritual = place("IND901", "Kolkata", 4.5, 2.0, "Recommended");
        spiritual.setCategory("Spiritual");
        spiritual.setFamilyFriendly(true);
        spiritual.setDescription("Temple and pilgrim attraction");

        when(repository.smartFilter(null, null, null, null, null, null, null, null, null, 4.0))
                .thenReturn(List.of(nightlife, spiritual));

        PlannerResponseDto friendsPlan = service.generate(friendsRequest);
        PlannerResponseDto familyPlan = service.generate(familyRequest);

        assertEquals("FRIENDS", friendsPlan.getTravellerType());
        assertEquals("FAMILY", familyPlan.getTravellerType());
        assertEquals("IND900", friendsPlan.getItinerary().get(0).getPlaces().get(0).getPlaceId());
        assertEquals("IND901", familyPlan.getItinerary().get(0).getPlaces().get(0).getPlaceId());
    }

    private Place place(String id, String city, double rating, double hours, String priority) {
        Place place = new Place();
        place.setPlaceId(id);
        place.setPlaceName("Place " + id);
        place.setCity(city);
        place.setState("State");
        place.setCategory("Heritage");
        place.setSignificance("Historic");
        place.setRating(rating);
        place.setRecommendedDurationHours(hours);
        place.setPriority(priority);
        place.setSafetyScore(9.0);
        place.setCleanlinessScore(8.5);
        place.setBestTimeToVisit("Oct-Mar");
        place.setIdealVisitTime("Morning");
        return place;
    }
}
