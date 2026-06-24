package com.riya.smarttravel.service;

import com.riya.smarttravel.dto.CitySummaryDto;
import com.riya.smarttravel.dto.PlaceResponseDto;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.exception.ResourceNotFoundException;
import com.riya.smarttravel.entity.Place;
import com.riya.smarttravel.repository.CitySummaryProjection;
import com.riya.smarttravel.repository.PlaceRepository;
import com.riya.smarttravel.util.InputSanitizer;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class PlaceService {

    private static final int MAX_RESULTS = 20;
    private final PlaceRepository repository;
    private final PlannerAiService plannerAiService;

    public PlaceService(PlaceRepository repository, PlannerAiService plannerAiService) {
        this.repository = repository;
        this.plannerAiService = plannerAiService;
    }

    public List<CitySummaryDto> getCitiesByRegion(String region) {
        String normalized = validateTextInput(region, "Region");
        return mapCitySummaries(repository.findCitySummariesByRegion(normalized),
                "No cities found for region: " + normalized);
    }

    public List<CitySummaryDto> getCitiesByCategory(String category) {
        String normalized = validateTextInput(category, "Category");
        return mapCitySummaries(repository.findCitySummariesByCategory(normalized),
                "No cities found for category: " + normalized);
    }

    public List<CitySummaryDto> getCitiesByMood(String mood) {
        String normalized = validateTextInput(mood, "Mood");
        return mapCitySummaries(repository.findCitySummariesByMood(normalized),
                "No cities found for mood: " + normalized);
    }

    public List<CitySummaryDto> getCitiesBySeason(String season) {
        String normalized = validateTextInput(season, "Season");
        return mapCitySummaries(repository.findCitySummariesBySeason(normalized),
                "No cities found for season: " + normalized);
    }

    public List<CitySummaryDto> getCitiesByDuration(double hours) {
        if (hours <= 0 || hours > 24) {
            throw new BadRequestException("Recommended duration must be between 0 and 24 hours");
        }
        return mapCitySummaries(repository.findCitySummariesByDuration(hours),
                "No cities found for duration: " + hours + " hours");
    }

    public List<CitySummaryDto> getTrendingCities() {
        return mapCitySummaries(repository.findTrendingCities(),
                "No trending cities found");
    }

    public List<PlaceResponseDto> getPlacesByCity(String city) {
        String normalized = validateTextInput(city, "City");
        List<Place> places = repository.findByCityContainingIgnoreCase(normalized);
        if (places == null || places.isEmpty()) {
            List<PlaceResponseDto> aiPlaces = plannerAiService.generatePlacesForCity(normalized);
            if (aiPlaces != null && !aiPlaces.isEmpty()) {
                return aiPlaces;
            }
        }
        return mapPlaces(places, "No places found for city: " + normalized);
    }

    public List<PlaceResponseDto> search(String query) {
        String normalized = validateTextInput(query, "Search query");
        List<Place> places = repository.searchPlaces(normalized).stream()
                .limit(MAX_RESULTS)
                .toList();
        if (places == null || places.isEmpty()) {
            List<PlaceResponseDto> aiPlaces = plannerAiService.generatePlacesForCity(normalized);
            if (aiPlaces != null && !aiPlaces.isEmpty()) {
                return aiPlaces;
            }
        }
        return mapPlaces(places, "No places found for query: " + normalized);
    }

    public List<PlaceResponseDto> smartFilter(String region,
                                              String category,
                                              String mood,
                                              String budgetLevel,
                                              String crowdLevel,
                                              Boolean familyFriendly,
                                              String priority,
                                              String season,
                                              String weatherType,
                                              Double minRating,
                                              String sortBy,
                                              String sortDir) {
        String normalizedRegion = InputSanitizer.normalize(region);
        String normalizedCategory = InputSanitizer.normalize(category);
        String normalizedMood = InputSanitizer.normalize(mood);
        String normalizedBudgetLevel = InputSanitizer.normalize(budgetLevel);
        String normalizedCrowdLevel = InputSanitizer.normalize(crowdLevel);
        String normalizedPriority = InputSanitizer.normalize(priority);
        String normalizedSeason = InputSanitizer.normalize(season);
        String normalizedWeatherType = InputSanitizer.normalize(weatherType);
        String normalizedSortBy = normalizeSortBy(sortBy);
        String normalizedSortDir = normalizeSortDirection(sortDir, normalizedSortBy);

        if (minRating != null && (minRating < 0 || minRating > 5)) {
            throw new BadRequestException("Minimum rating must be between 0 and 5");
        }

        if (normalizedRegion == null
                && normalizedCategory == null
                && normalizedMood == null
                && normalizedBudgetLevel == null
                && normalizedCrowdLevel == null
                && familyFriendly == null
                && normalizedPriority == null
                && normalizedSeason == null
                && normalizedWeatherType == null
                && minRating == null) {
            throw new BadRequestException("At least one filter is required");
        }

        List<Place> places = repository.smartFilter(
                normalizedRegion,
                normalizedCategory,
                normalizedMood,
                normalizedBudgetLevel,
                normalizedCrowdLevel,
                familyFriendly,
                normalizedPriority,
                normalizedSeason,
                normalizedWeatherType,
                minRating
        );

        if (normalizedSortBy != null) {
            places = places.stream()
                    .sorted(buildComparator(normalizedSortBy, normalizedSortDir))
                    .toList();
        }

        places = places.stream()
                .limit(MAX_RESULTS)
                .toList();

        return mapPlaces(places, "No places found for the selected filters");
    }

    private String validateTextInput(String input, String fieldName) {
        String normalized = InputSanitizer.normalize(input);
        if (normalized == null) {
            throw new BadRequestException(fieldName + " cannot be blank");
        }
        if (normalized.length() < 2) {
            throw new BadRequestException(fieldName + " must be at least 2 characters");
        }
        return normalized;
    }

    private List<CitySummaryDto> mapCitySummaries(List<CitySummaryProjection> rows, String emptyMessage) {
        if (rows == null || rows.isEmpty()) {
            throw new ResourceNotFoundException(emptyMessage);
        }

        return rows.stream()
                .limit(8)
                .map(row -> new CitySummaryDto(
                        row.getCity(),
                        row.getState(),
                        row.getRegion(),
                        row.getPlaceCount()
                ))
                .toList();
    }

    private List<PlaceResponseDto> mapPlaces(List<Place> places, String emptyMessage) {
        if (places == null || places.isEmpty()) {
            throw new ResourceNotFoundException(emptyMessage);
        }

        return places.stream()
                .map(this::toPlaceResponseDto)
                .toList();
    }

    private PlaceResponseDto toPlaceResponseDto(Place place) {
        return PlaceResponseDto.builder()
                .placeId(place.getPlaceId())
                .placeName(place.getPlaceName())
                .city(place.getCity())
                .state(place.getState())
                .region(place.getRegion())
                .placeType(place.getPlaceType())
                .category(place.getCategory())
                .moodTags(place.getMoodTags())
                .significance(place.getSignificance())
                .description(place.getDescription())
                .bestTimeToVisit(place.getBestTimeToVisit())
                .idealVisitTime(place.getIdealVisitTime())
                .recommendedDurationHours(place.getRecommendedDurationHours())
                .entryFee(place.getEntryFee())
                .rating(place.getRating())
                .crowdLevel(place.getCrowdLevel())
                .familyFriendly(place.getFamilyFriendly())
                .adventureLevel(place.getAdventureLevel())
                .culturalValue(place.getCulturalValue())
                .natureValue(place.getNatureValue())
                .budgetLevel(place.getBudgetLevel())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .nearestAirport(place.getNearestAirport())
                .nearestRailway(place.getNearestRailway())
                .localTips(place.getLocalTips())
                .foodSpecialty(place.getFoodSpecialty())
                .safetyScore(place.getSafetyScore())
                .cleanlinessScore(place.getCleanlinessScore())
                .photographySpots(place.getPhotographySpots())
                .weatherType(place.getWeatherType())
                .seasonalHighlight(place.getSeasonalHighlight())
                .priority(place.getPriority())
                .build();
    }

    public PlaceResponseDto getPlaceById(String placeId) {
        String normalizedPlaceId = validateTextInput(placeId, "Place ID");
        Place place = repository.findByPlaceId(normalizedPlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found: " + normalizedPlaceId));

        return toPlaceResponseDto(place);
    }

    private String normalizeSortBy(String sortBy) {
        String normalized = InputSanitizer.normalize(sortBy);
        if (normalized == null) {
            return null;
        }
        return switch (normalized.toLowerCase(Locale.ROOT)) {
            case "rating" -> "rating";
            case "safetyscore", "safety_score" -> "safetyScore";
            case "cleanlinessscore", "cleanliness_score" -> "cleanlinessScore";
            case "priority" -> "priority";
            case "entryfee", "entry_fee" -> "entryFee";
            default -> throw new BadRequestException("Unsupported sort field: " + normalized);
        };
    }

    private String normalizeSortDirection(String sortDir, String sortBy) {
        String normalized = InputSanitizer.normalize(sortDir);
        if (sortBy == null) {
            if (normalized != null) {
                throw new BadRequestException("sortDir requires sortBy");
            }
            return "desc";
        }
        if (normalized == null) {
            return "desc";
        }
        String lower = normalized.toLowerCase(Locale.ROOT);
        if (!lower.equals("asc") && !lower.equals("desc")) {
            throw new BadRequestException("sortDir must be either asc or desc");
        }
        return lower;
    }

    private Comparator<Place> buildComparator(String sortBy, String sortDir) {
        Comparator<Place> comparator = switch (sortBy) {
            case "rating" -> Comparator.comparing(Place::getRating, Comparator.nullsLast(Double::compareTo));
            case "safetyScore" -> Comparator.comparing(Place::getSafetyScore, Comparator.nullsLast(Double::compareTo));
            case "cleanlinessScore" -> Comparator.comparing(Place::getCleanlinessScore, Comparator.nullsLast(Double::compareTo));
            case "entryFee" -> Comparator.comparing(Place::getEntryFee, Comparator.nullsLast(Double::compareTo));
            case "priority" -> Comparator.comparingInt(place -> priorityRank(place.getPriority()));
            default -> throw new BadRequestException("Unsupported sort field: " + sortBy);
        };

        return "asc".equals(sortDir) ? comparator : comparator.reversed();
    }

    private int priorityRank(String priority) {
        if (priority == null) {
            return 0;
        }
        return switch (priority.toLowerCase(Locale.ROOT)) {
            case "must visit" -> 3;
            case "recommended" -> 2;
            case "optional" -> 1;
            default -> 0;
        };
    }
}
