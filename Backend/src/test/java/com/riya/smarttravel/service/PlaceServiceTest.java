package com.riya.smarttravel.service;

import com.riya.smarttravel.dto.CitySummaryDto;
import com.riya.smarttravel.dto.PlaceResponseDto;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.exception.ResourceNotFoundException;
import com.riya.smarttravel.entity.Place;
import com.riya.smarttravel.repository.CitySummaryProjection;
import com.riya.smarttravel.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository repository;

    @InjectMocks
    private PlaceService service;

    @Test
    void getPlaceByIdReturnsMappedResponse() {
        Place place = new Place();
        place.setPlaceId("P001");
        place.setPlaceName("Marine Drive");
        place.setCity("Mumbai");
        place.setState("Maharashtra");
        place.setRegion("West");
        place.setPlaceType("Sightseeing");
        place.setCategory("Urban");
        place.setMoodTags("relaxing,nightlife");
        place.setSignificance("Scenic promenade");
        place.setDescription("Sea-facing promenade");
        place.setBestTimeToVisit("October to March");
        place.setIdealVisitTime("Evening");
        place.setRecommendedDurationHours(2.5);
        place.setEntryFee(0.0);
        place.setRating(4.6);
        place.setFamilyFriendly(true);
        place.setSafetyScore(8.9);
        place.setPriority("Must Visit");

        when(repository.findByPlaceId("P001")).thenReturn(Optional.of(place));

        PlaceResponseDto response = service.getPlaceById(" P001 ");

        assertEquals("P001", response.getPlaceId());
        assertEquals("Scenic promenade", response.getSignificance());
        assertEquals(2.5, response.getRecommendedDurationHours());
        assertEquals(4.6, response.getRating());
        assertEquals(true, response.getFamilyFriendly());
        assertEquals(8.9, response.getSafetyScore());
        verify(repository).findByPlaceId("P001");
    }

    @Test
    void getPlaceByIdRejectsBlankInput() {
        assertThrows(BadRequestException.class, () -> service.getPlaceById(" "));
    }

    @Test
    void getTrendingCitiesMapsPlaceCount() {
        when(repository.findTrendingCities()).thenReturn(List.of(new TestCitySummaryProjection("Jaipur", "Rajasthan", "North", 5L)));

        List<CitySummaryDto> response = service.getTrendingCities();

        assertEquals(1, response.size());
        assertEquals("Jaipur", response.get(0).getCity());
        assertEquals(5L, response.get(0).getPlaceCount());
    }

    @Test
    void smartFilterRequiresAtLeastOneFilter() {
        assertThrows(BadRequestException.class,
                () -> service.smartFilter(" ", null, "", null, null, null, null, null, null, null, null, null));
    }

    @Test
    void searchThrowsNotFoundWhenRepositoryReturnsEmptyList() {
        when(repository.searchPlaces("beach")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.search("beach"));
    }

    @Test
    void smartFilterRejectsUnsupportedSortField() {
        assertThrows(BadRequestException.class,
                () -> service.smartFilter("North", null, null, null, null, null, null, null, null, 4.0, "unknown", null));
    }

    @Test
    void smartFilterSortsByRatingDescending() {
        Place lowerRated = new Place();
        lowerRated.setPlaceId("P100");
        lowerRated.setRating(4.1);

        Place higherRated = new Place();
        higherRated.setPlaceId("P200");
        higherRated.setRating(4.8);

        when(repository.smartFilter("North", null, null, null, null, null, null, null, null, 4.0))
                .thenReturn(List.of(lowerRated, higherRated));

        List<PlaceResponseDto> results = service.smartFilter(
                "North", null, null, null, null, null, null, null, null, 4.0, "rating", "desc");

        assertEquals(2, results.size());
        assertEquals("P200", results.get(0).getPlaceId());
        assertEquals("P100", results.get(1).getPlaceId());
    }

    private static final class TestCitySummaryProjection implements CitySummaryProjection {
        private final String city;
        private final String state;
        private final String region;
        private final Long placeCount;

        private TestCitySummaryProjection(String city, String state, String region, Long placeCount) {
            this.city = city;
            this.state = state;
            this.region = region;
            this.placeCount = placeCount;
        }

        @Override
        public String getCity() {
            return city;
        }

        @Override
        public String getState() {
            return state;
        }

        @Override
        public String getRegion() {
            return region;
        }

        @Override
        public Long getPlaceCount() {
            return placeCount;
        }
    }
}
