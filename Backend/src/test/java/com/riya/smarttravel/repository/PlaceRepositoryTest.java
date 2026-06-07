package com.riya.smarttravel.repository;

import com.riya.smarttravel.entity.Place;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository repository;

    @Test
    void findTrendingCitiesReturnsTypedProjection() {
        repository.save(place("IND001", "Jaipur", "North", "Heritage", "historic,royal", 4.6, true, "High", "Must Visit", "Sunny"));
        repository.save(place("IND002", "Jaipur", "North", "Heritage", "family,cultural", 4.7, true, "Medium", "Recommended", "Sunny"));
        repository.save(place("IND003", "Mumbai", "West", "Urban", "vibrant,scenic", 4.5, false, "High", "Optional", "Tropical"));

        List<CitySummaryProjection> summaries = repository.findTrendingCities();

        assertFalse(summaries.isEmpty());
        assertEquals("Jaipur", summaries.get(0).getCity());
        assertEquals(2L, summaries.get(0).getPlaceCount());
    }

    @Test
    void smartFilterSupportsTypedAndOptionalCriteria() {
        repository.save(place("IND004", "Jaipur", "North", "Heritage", "historic,royal", 4.8, true, "High", "Must Visit", "Sunny"));
        repository.save(place("IND005", "Jaipur", "North", "Adventure", "adventure,scenic", 4.1, false, "Low", "Optional", "Cool"));

        List<Place> results = repository.smartFilter(
                "North",
                "Heritage",
                "historic",
                "High",
                "High",
                true,
                "Must Visit",
                "Oct",
                "Sunny",
                4.5
        );

        assertEquals(1, results.size());
        assertEquals("IND004", results.get(0).getPlaceId());
        assertTrue(repository.findByPlaceId("IND004").isPresent());
    }

    private Place place(String id,
                        String city,
                        String region,
                        String category,
                        String moods,
                        double rating,
                        boolean familyFriendly,
                        String budgetLevel,
                        String priority,
                        String weatherType) {
        Place place = new Place();
        place.setPlaceId(id);
        place.setPlaceName("Place " + id);
        place.setCity(city);
        place.setState("State");
        place.setRegion(region);
        place.setPlaceType("Monument");
        place.setCategory(category);
        place.setSignificance("Historic landmark");
        place.setDescription("Description for " + id);
        place.setMoodTags(moods);
        place.setBestTimeToVisit("Oct-Mar");
        place.setIdealVisitTime("Morning");
        place.setRecommendedDurationHours(2.0);
        place.setEntryFee(100.0);
        place.setRating(rating);
        place.setCrowdLevel("High");
        place.setFamilyFriendly(familyFriendly);
        place.setAdventureLevel("Medium");
        place.setCulturalValue("High");
        place.setNatureValue("Medium");
        place.setBudgetLevel(budgetLevel);
        place.setLatitude(26.9);
        place.setLongitude(75.8);
        place.setNearestAirport("Airport");
        place.setNearestRailway("Railway");
        place.setLocalTips("Visit early");
        place.setFoodSpecialty("Local dish");
        place.setSafetyScore(9.0);
        place.setCleanlinessScore(8.5);
        place.setPhotographySpots("High");
        place.setWeatherType(weatherType);
        place.setSeasonalHighlight("Diwali lights");
        place.setPriority(priority);
        return place;
    }
}
