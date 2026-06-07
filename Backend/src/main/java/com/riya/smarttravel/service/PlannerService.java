package com.riya.smarttravel.service;

import com.riya.smarttravel.dto.AiEnhancementDto;
import com.riya.smarttravel.dto.PlannerDayDto;
import com.riya.smarttravel.dto.PlannerLocationDto;
import com.riya.smarttravel.dto.PlannerPlaceDto;
import com.riya.smarttravel.dto.PlannerRequest;
import com.riya.smarttravel.dto.PlannerResponseDto;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.exception.ResourceNotFoundException;
import com.riya.smarttravel.entity.Place;
import com.riya.smarttravel.repository.PlaceRepository;
import com.riya.smarttravel.util.InputSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlannerService {

    private static final double DEFAULT_HOURS_PER_DAY = 8.0;
    private static final double DEFAULT_PLACE_HOURS = 2.0;
    private static final double CITY_DISTANCE_FALLBACK_KM = 12.0;
    private static final double SAME_STATE_DISTANCE_FALLBACK_KM = 220.0;
    private static final double CROSS_STATE_DISTANCE_FALLBACK_KM = 900.0;
    private static final double AVG_TRAVEL_SPEED_KMH = 35.0;
    private static final double CITY_OUTLIER_DISTANCE_KM = 120.0;

    private enum TravellerType {
        SOLO,
        COUPLE,
        FRIENDS,
        FAMILY,
        GROUP,
        STANDARD
    }

    private final PlaceRepository repository;
    private final PlannerAiService plannerAiService;

    public PlannerService(PlaceRepository repository, PlannerAiService plannerAiService) {
        this.repository = repository;
        this.plannerAiService = plannerAiService;
    }

    public PlannerResponseDto generate(PlannerRequest request) {
        if (request == null) {
            throw new BadRequestException("Planner request is required");
        }
        if (request.getDays() == null || request.getDays() <= 0 || request.getDays() > 14) {
            throw new BadRequestException("Days must be between 1 and 14");
        }

        double maxHoursPerDay = request.getMaxHoursPerDay() == null ? DEFAULT_HOURS_PER_DAY
                : request.getMaxHoursPerDay();
        if (maxHoursPerDay <= 0 || maxHoursPerDay > 16) {
            throw new BadRequestException("maxHoursPerDay must be between 1 and 16");
        }
        if (request.getMinRating() != null && (request.getMinRating() < 0 || request.getMinRating() > 5)) {
            throw new BadRequestException("minRating must be between 0 and 5");
        }

        String normalizedCity = InputSanitizer.normalize(request.getCity());
        TravellerType travellerType = parseTravellerType(request.getTravellerType());
        String normalizedRegion = InputSanitizer.normalize(request.getRegion());
        String normalizedCategory = InputSanitizer.normalize(request.getCategory());
        String normalizedMood = InputSanitizer.normalize(request.getMood());
        String normalizedBudget = InputSanitizer.normalize(request.getBudgetLevel());
        String normalizedCrowd = InputSanitizer.normalize(request.getCrowdLevel());
        String normalizedPriority = InputSanitizer.normalize(request.getPriority());
        String normalizedSeason = InputSanitizer.normalize(request.getSeason());
        String normalizedWeather = InputSanitizer.normalize(request.getWeatherType());

        if (normalizedCity == null
                && normalizedRegion == null
                && normalizedCategory == null
                && normalizedMood == null
                && normalizedBudget == null
                && normalizedCrowd == null
                && request.getFamilyFriendly() == null
                && normalizedPriority == null
                && normalizedSeason == null
                && normalizedWeather == null
                && request.getMinRating() == null
                && travellerType == TravellerType.STANDARD) {
            throw new BadRequestException("At least one planner preference is required");
        }

        List<Place> rawCandidates = findCandidatesWithFallback(
                normalizedCity,
                normalizedRegion,
                normalizedCategory,
                normalizedMood,
                normalizedBudget,
                normalizedCrowd,
                request.getFamilyFriendly(),
                normalizedPriority,
                normalizedSeason,
                normalizedWeather,
                request.getMinRating());

        // Expand sparse results; capture as final for use in lambdas
        final List<Place> candidates = expandCandidatesWhenSparse(rawCandidates, normalizedCity, request.getDays());

        // If a specific city was requested but the DB doesn't have enough places to
        // fill
        // the requested days well (< days*2 places), prefer AI generation for richer
        // results.
        int minimumDbCoverage = request.getDays() * 2;
        boolean isCityUnknownOrSparse = normalizedCity != null && candidates.size() < minimumDbCoverage;

        log.info("Trip request received for city={}, days={}, travellerType={}", normalizedCity, request.getDays(), travellerType);

        if (candidates.isEmpty() || isCityUnknownOrSparse) {
            log.info("Destination detected: city={}, dbCandidates={}, minimumRequired={}, usingAI={}", 
                     normalizedCity, candidates.size(), minimumDbCoverage, isCityUnknownOrSparse);
            
            AiFallbackResult aiResult = plannerAiService
                    .generateFallbackItinerary(
                            request,
                            normalizedCity,
                            request.getDays(),
                            maxHoursPerDay,
                            travellerType.name());

            if (aiResult.isSuccess()) {
                return aiResult.response();
            }

            // AI was not available or failed
            if (candidates.isEmpty()) {
                // Zero DB coverage — no useful fallback exists
                if (normalizedCity != null) {
                    // Specific city was requested but not found
                    if (aiResult.status() == AiFallbackStatus.NOT_CONFIGURED) {
                        log.warn("AI itinerary generation unavailable for city={}. Using local fallback.", normalizedCity);
                        throw new ResourceNotFoundException(
                            "No places found for city '" + request.getCity() + "'. AI generation is disabled — set OPENROUTER_API_KEY to enable it.");
                    } else {
                        // AI was configured but the call failed
                        throw new ResourceNotFoundException(
                            "No places found for city '" + request.getCity() + "' and AI generation failed. Please try again later.");
                    }
                } else {
                    // No city specified (region/filter-only request) - use rule-based fallback
                    return generateRuleBasedFallback(request, travellerType, maxHoursPerDay);
                }
            } else {
                // Sparse DB coverage — return what we have with a clear dataSource
                log.warn("AI itinerary generation unavailable for city={}. Using local fallback.", normalizedCity);
                return buildDbResponse(candidates, request, travellerType, maxHoursPerDay);
            }
        }

        List<PlannerDayDto> itinerary = buildItinerary(candidates, request.getDays(), maxHoursPerDay, travellerType);
        if (itinerary.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No itinerary could be generated from the selected planner preferences");
        }

        int totalPlaces = itinerary.stream().mapToInt(day -> day.getPlaces().size()).sum();
        double totalTripHours = itinerary.stream()
                .mapToDouble(day -> (day.getTotalPlannedHours() == null ? 0.0 : day.getTotalPlannedHours())
                        + (day.getEstimatedTravelHours() == null ? 0.0 : day.getEstimatedTravelHours()))
                .sum();

        PlannerResponseDto response = PlannerResponseDto.builder()
                .requestedDays(request.getDays())
                .generatedDays(itinerary.size())
                .totalPlaces(totalPlaces)
                .travellerType(travellerType.name())
                .dataSource("DATABASE")
                .maxHoursPerDay(maxHoursPerDay)
                .totalTripHours(round2(totalTripHours))
                .summary("Generated " + itinerary.size() + "-day itinerary with " + totalPlaces + " places and "
                        + round2(totalTripHours) + " total trip hours")
                .budgetAdvice(buildBudgetAdvice(request.getBudgetLevel()))
                .generalSafetyTips(buildGeneralSafetyTips(travellerType.name()))
                .aiSummary("")
                .tips(Collections.emptyList())
                .additionalRecommendations(Collections.emptyList())
                .itinerary(itinerary)
                .build();

        return applyAiEnhancementIfAvailable(response, request, travellerType.name());
    }

    /**
     * Builds a response from DB candidates – used as an AI fallback when AI is
     * temporarily unavailable but the DB still has some places for the city.
     */
    private PlannerResponseDto buildDbResponse(List<Place> candidates,
            PlannerRequest request,
            TravellerType travellerType,
            double maxHoursPerDay) {
        List<PlannerDayDto> itinerary = buildItinerary(candidates, request.getDays(), maxHoursPerDay, travellerType);
        if (itinerary.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No itinerary could be generated from the selected planner preferences");
        }

        int totalPlaces = itinerary.stream().mapToInt(day -> day.getPlaces().size()).sum();
        double totalTripHours = itinerary.stream()
                .mapToDouble(day -> (day.getTotalPlannedHours() == null ? 0.0 : day.getTotalPlannedHours())
                        + (day.getEstimatedTravelHours() == null ? 0.0 : day.getEstimatedTravelHours()))
                .sum();

        PlannerResponseDto response = PlannerResponseDto.builder()
                .requestedDays(request.getDays())
                .generatedDays(itinerary.size())
                .totalPlaces(totalPlaces)
                .travellerType(travellerType.name())
                .dataSource("DATABASE")
                .maxHoursPerDay(maxHoursPerDay)
                .totalTripHours(round2(totalTripHours))
                .summary("Generated " + itinerary.size() + "-day itinerary with " + totalPlaces + " places and "
                        + round2(totalTripHours) + " total trip hours")
                .budgetAdvice(buildBudgetAdvice(request.getBudgetLevel()))
                .generalSafetyTips(buildGeneralSafetyTips(travellerType.name()))
                .aiSummary("")
                .tips(Collections.emptyList())
                .additionalRecommendations(Collections.emptyList())
                .itinerary(itinerary)
                .build();

        return applyAiEnhancementIfAvailable(response, request, travellerType.name());
    }

    private PlannerResponseDto applyAiEnhancementIfAvailable(PlannerResponseDto response,
            PlannerRequest request,
            String travellerType) {
        return plannerAiService
                .enhanceItinerary(response, travellerType, request.getPreferences(), request.getBudgetLevel())
                .map(enhancement -> mergeEnhancement(response, enhancement))
                .orElse(response);
    }

    private PlannerResponseDto mergeEnhancement(PlannerResponseDto response, AiEnhancementDto enhancement) {
        return response.toBuilder()
                .aiSummary(enhancement.getAiSummary() == null ? "" : enhancement.getAiSummary())
                .tips(enhancement.getTips() == null ? Collections.emptyList() : enhancement.getTips())
                .additionalRecommendations(enhancement.getAdditionalRecommendations() == null
                        ? Collections.emptyList()
                        : enhancement.getAdditionalRecommendations())
                .aboutPlace(
                        enhancement.getAboutPlace() == null ? response.getAboutPlace() : enhancement.getAboutPlace())
                .whyChoosePlace(enhancement.getWhyChoosePlace() == null ? response.getWhyChoosePlace()
                        : enhancement.getWhyChoosePlace())
                .build();
    }

    private PlannerResponseDto generateRuleBasedFallback(PlannerRequest request,
            TravellerType travellerType,
            double maxHoursPerDay) {
        List<Place> allPlaces = repository.findAll();
        if (allPlaces == null || allPlaces.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No places found for the selected planner preferences and AI fallback is unavailable");
        }

        String normalizedCity = InputSanitizer.normalize(request.getCity());
        Double minRating = request.getMinRating();

        List<Place> scopedPlaces;
        if (normalizedCity != null) {
            scopedPlaces = repository.findByCityContainingIgnoreCase(normalizedCity).stream()
                    .filter(place -> matchesExactCity(place, normalizedCity))
                    .filter(place -> minRating == null || (place.getRating() != null && place.getRating() >= minRating))
                    .toList();

            if (scopedPlaces.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No places found for requested city '" + request.getCity()
                                + "' and AI fallback is unavailable. Check OpenRouter key, model, and quota/billing.");
            }
        } else {
            scopedPlaces = allPlaces;
        }

        List<Place> backupCandidates = scopedPlaces.stream()
                .sorted(buildPlannerComparator())
                .limit(Math.max(6L, request.getDays().longValue() * 4L))
                .toList();

        List<PlannerDayDto> itinerary = buildItinerary(backupCandidates, request.getDays(), maxHoursPerDay,
                travellerType);
        if (itinerary.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No itinerary could be generated from available data and AI fallback is unavailable");
        }

        int totalPlaces = itinerary.stream().mapToInt(day -> day.getPlaces().size()).sum();
        double totalTripHours = itinerary.stream()
                .mapToDouble(day -> (day.getTotalPlannedHours() == null ? 0.0 : day.getTotalPlannedHours())
                        + (day.getEstimatedTravelHours() == null ? 0.0 : day.getEstimatedTravelHours()))
                .sum();

        return PlannerResponseDto.builder()
                .requestedDays(request.getDays())
                .generatedDays(itinerary.size())
                .totalPlaces(totalPlaces)
                .travellerType(travellerType.name())
                .dataSource("RULE_BASED_FALLBACK")
                .maxHoursPerDay(maxHoursPerDay)
                .totalTripHours(round2(totalTripHours))
                .summary(normalizedCity == null
                        ? "Fallback itinerary generated from available Indian places"
                        : "Fallback itinerary generated from available places in " + request.getCity())
                .budgetAdvice(buildBudgetAdvice(request.getBudgetLevel()))
                .generalSafetyTips(buildGeneralSafetyTips(travellerType.name()))
                .aiSummary("")
                .tips(Collections.emptyList())
                .additionalRecommendations(Collections.emptyList())
                .itinerary(itinerary)
                .build();
    }

    private List<PlannerDayDto> buildItinerary(List<Place> candidates,
            int requestedDays,
            double maxHoursPerDay,
            TravellerType travellerType) {
        List<PlannerDayDto> itinerary = new ArrayList<>();
        List<Place> remaining = new ArrayList<>(candidates);

        for (int day = 1; day <= requestedDays && !remaining.isEmpty(); day++) {
            List<PlannerPlaceDto> dayPlaces = new ArrayList<>();
            List<Place> selectedRawPlaces = new ArrayList<>();
            double totalVisitHours = 0.0;
            double travelHours = 0.0;
            Place lastSelected = null;
            int remainingDays = requestedDays - day + 1;
            int targetPlacesForDay = Math.max(3, (int) Math.ceil((double) remaining.size() / remainingDays));
            if (travellerType == TravellerType.COUPLE || travellerType == TravellerType.FRIENDS) {
                targetPlacesForDay = Math.max(targetPlacesForDay, 3);
            } else if (travellerType == TravellerType.FAMILY || travellerType == TravellerType.GROUP) {
                targetPlacesForDay = Math.max(targetPlacesForDay, 4);
            }
            double targetVisitHours = Math.min(maxHoursPerDay * 0.8,
                    estimateAverageVisitHours(remaining, remainingDays));

            if (travellerType == TravellerType.SOLO) {
                targetVisitHours = Math.min(targetVisitHours, maxHoursPerDay * 0.75);
            } else if (travellerType == TravellerType.FRIENDS) {
                targetVisitHours = Math.min(targetVisitHours, maxHoursPerDay * 0.9);
            } else if (travellerType == TravellerType.FAMILY || travellerType == TravellerType.GROUP) {
                targetVisitHours = Math.min(targetVisitHours, maxHoursPerDay * 0.85);
            }

            while (!remaining.isEmpty()) {
                Place place = pickNextPlace(remaining, lastSelected, maxHoursPerDay - totalVisitHours,
                        dayPlaces.isEmpty(), travellerType);
                if (place == null) {
                    break;
                }

                double placeHours = getPlaceHours(place);
                double estimatedTravel = (lastSelected == null) ? 0.0
                        : estimateTravelHours(lastSelected, place, travellerType);
                double totalVisitWithCandidate = totalVisitHours + placeHours;

                if (!dayPlaces.isEmpty() && totalVisitWithCandidate > maxHoursPerDay) {
                    break;
                }

                String plannedSlot = assignVisitTimeSlot(totalVisitHours, placeHours, travellerType);
                dayPlaces.add(toPlannerPlace(place, plannedSlot));
                selectedRawPlaces.add(place);
                totalVisitHours += placeHours;
                travelHours += estimatedTravel;
                lastSelected = place;
                remaining.remove(place);

                boolean reachedTargetPlaceCount = dayPlaces.size() >= targetPlacesForDay;
                boolean reachedReasonableHours = totalVisitHours >= targetVisitHours;
                if (reachedTargetPlaceCount && reachedReasonableHours) {
                    break;
                }
            }

            if (dayPlaces.isEmpty()) {
                break;
            }

            PlannerLocationDto location = deriveDayLocation(selectedRawPlaces);
            String daySummary = buildDaySummary(location, selectedRawPlaces, travellerType);
            String travelNotes = buildTravelNotes(location, selectedRawPlaces, travelHours, travellerType);

            itinerary.add(PlannerDayDto.builder()
                    .dayNumber(day)
                    .location(location)
                    .totalPlannedHours(round2(totalVisitHours))
                    .estimatedTravelHours(round2(travelHours))
                    .daySummary(daySummary)
                    .travelNotes(travelNotes)
                    .places(dayPlaces)
                    .build());
        }

        return itinerary;
    }

    private Place pickNextPlace(List<Place> remaining,
            Place lastSelected,
            double remainingHours,
            boolean isFirstPlaceOfDay,
            TravellerType travellerType) {
        Place best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Place candidate : remaining) {
            double candidateHours = getPlaceHours(candidate);
            double travelHours = (isFirstPlaceOfDay || lastSelected == null) ? 0.0
                    : estimateTravelHours(lastSelected, candidate, travellerType);

            if (!isFirstPlaceOfDay && candidateHours > remainingHours) {
                continue;
            }

            double score = buildBaseScore(candidate, travellerType);
            if (!isFirstPlaceOfDay && lastSelected != null) {
                double distanceKm = estimateDistanceKm(lastSelected, candidate);
                score -= (distanceKm * distancePenaltyWeight(travellerType));
            }
            score -= candidateHours * 0.8;

            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return best;
    }

    private double buildBaseScore(Place place, TravellerType travellerType) {
        double rating = place.getRating() == null ? 0.0 : place.getRating();
        double safety = place.getSafetyScore() == null ? 0.0 : place.getSafetyScore();
        double cleanliness = place.getCleanlinessScore() == null ? 0.0 : place.getCleanlinessScore();
        double score = priorityRank(place.getPriority()) * 25.0
                + rating * 12.0
                + safety
                + cleanliness;

        double crowdPenalty = switch (normalizeCrowdLevel(place.getCrowdLevel())) {
            case "LOW" -> 0.0;
            case "MODERATE" -> 2.0;
            case "HIGH" -> 6.0;
            case "VERY_HIGH" -> 10.0;
            default -> 3.0;
        };

        switch (travellerType) {
            case SOLO -> {
                score += safety * 0.9;
                score += place.getFamilyFriendly() != null && place.getFamilyFriendly() ? 3.0 : 0.0;
                score -= crowdPenalty * 1.3;
                if (getPlaceHours(place) > 3.5) {
                    score -= 8.0;
                }
            }
            case COUPLE -> {
                score += romanticScenicBonus(place);
                score += cleanliness * 0.5;
                score -= crowdPenalty * 0.8;
            }
            case FRIENDS -> {
                score += friendsFunBonus(place);
                score += crowdPenalty * 0.8;
                score -= spiritualPenalty(place);
            }
            case FAMILY -> {
                score += familySuitabilityBonus(place);
                score += spiritualFamilyBonus(place);
                score -= crowdPenalty * 1.1;
            }
            case GROUP -> {
                score += groupSuitabilityBonus(place);
                score -= crowdPenalty * 0.4;
            }
            default -> {
                // Keep base score only for standard profile.
            }
        }

        return score;
    }

    private double estimateDistanceKm(Place from, Place to) {
        if (from.getLatitude() != null
                && from.getLongitude() != null
                && to.getLatitude() != null
                && to.getLongitude() != null) {
            return calculateDistanceKm(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
        }

        String fromCity = normalized(from.getCity());
        String toCity = normalized(to.getCity());
        if (fromCity != null && fromCity.equals(toCity)) {
            return CITY_DISTANCE_FALLBACK_KM;
        }

        String fromState = normalized(from.getState());
        String toState = normalized(to.getState());
        if (fromState != null && fromState.equals(toState)) {
            return SAME_STATE_DISTANCE_FALLBACK_KM;
        }

        return CROSS_STATE_DISTANCE_FALLBACK_KM;
    }

    private double estimateTravelHours(Place from, Place to, TravellerType travellerType) {
        double distance = estimateDistanceKm(from, to);
        return Math.max(0.25, distance / averageTravelSpeed(travellerType));
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadiusKm = 6371.0;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private String normalized(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    private PlannerPlaceDto toPlannerPlace(Place place, String plannedVisitTimeSlot) {
        String imageUrl = null;
        try {
            if (place != null && place.getPlaceName() != null) {
                imageUrl = java.net.URLEncoder.encode(place.getPlaceName(),
                        java.nio.charset.StandardCharsets.UTF_8.toString());
                imageUrl = "https://source.unsplash.com/featured/?" + imageUrl;
            }
        } catch (Exception e) {
            imageUrl = null;
        }

        return PlannerPlaceDto.builder()
                .placeId(place.getPlaceId())
                .placeName(place.getPlaceName())
                .category(normalizeCategoryName(place.getCategory(), place.getPlaceType()))
                .significance(place.getSignificance())
                .description(place.getDescription())
                .localTips(place.getLocalTips())
                .safetyAdvice(buildPlaceSafetyAdvice(place))
                .travelNote(buildPlaceTravelNote(place, plannedVisitTimeSlot))
                .entryFee(place.getEntryFee())
                .rating(place.getRating())
                .recommendedDurationHours(place.getRecommendedDurationHours())
                .bestTimeToVisit(place.getBestTimeToVisit())
                .idealVisitTime(place.getIdealVisitTime())
                .plannedVisitTimeSlot(plannedVisitTimeSlot)
                .imageUrl(imageUrl)
                .build();
    }

    private boolean matchesExactCity(Place place, String normalizedCity) {
        if (normalizedCity == null) {
            return true;
        }
        String placeCity = normalized(place.getCity());
        return placeCity != null && placeCity.equals(normalizedCity.toLowerCase(Locale.ROOT));
    }

    private Comparator<Place> buildPlannerComparator() {
        return Comparator
                .comparingInt((Place place) -> priorityRank(place.getPriority())).reversed()
                .thenComparing(place -> place.getRecommendedDurationHours() == null ? DEFAULT_PLACE_HOURS
                        : place.getRecommendedDurationHours())
                .thenComparing(Place::getRating, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Place::getSafetyScore, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Place::getCleanlinessScore, Comparator.nullsLast(Comparator.reverseOrder()));
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

    private double estimateAverageVisitHours(List<Place> remaining, int remainingDays) {
        double remainingVisitHours = remaining.stream()
                .mapToDouble(this::getPlaceHours)
                .sum();
        return remainingVisitHours / Math.max(1, remainingDays);
    }

    private double getPlaceHours(Place place) {
        if (place.getRecommendedDurationHours() == null || place.getRecommendedDurationHours() <= 0) {
            return DEFAULT_PLACE_HOURS;
        }
        return place.getRecommendedDurationHours();
    }

    private String assignVisitTimeSlot(double consumedVisitHours, double placeHours, TravellerType travellerType) {
        double startHour = switch (travellerType) {
            case COUPLE -> 9.5;
            case FRIENDS -> 10.0;
            case FAMILY -> 8.0;
            default -> 8.0;
        };

        double midpointHour = startHour + consumedVisitHours + (placeHours / 2.0);
        if (midpointHour < 12.0) {
            return "Morning";
        }
        if (midpointHour < 16.5) {
            return "Afternoon";
        }
        return "Evening";
    }

    private PlannerLocationDto deriveDayLocation(List<Place> selectedRawPlaces) {
        if (selectedRawPlaces.isEmpty()) {
            return PlannerLocationDto.builder().city("Unknown").state("Unknown").build();
        }

        Map<String, Long> cityCounts = selectedRawPlaces.stream()
                .map(Place::getCity)
                .filter(city -> city != null && !city.isBlank())
                .collect(Collectors.groupingBy(city -> city, Collectors.counting()));

        String dominantCity = cityCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(selectedRawPlaces.get(0).getCity());

        String dominantState = selectedRawPlaces.stream()
                .filter(place -> dominantCity != null && dominantCity.equals(place.getCity()))
                .map(Place::getState)
                .filter(state -> state != null && !state.isBlank())
                .findFirst()
                .orElse(selectedRawPlaces.get(0).getState());

        return PlannerLocationDto.builder()
                .city(dominantCity)
                .state(dominantState)
                .build();
    }

    private String buildDaySummary(PlannerLocationDto location, List<Place> places, TravellerType travellerType) {
        String city = location == null || location.getCity() == null ? "the area" : location.getCity();
        String categories = places.stream()
                .map(place -> normalizeCategoryName(place.getCategory(), place.getPlaceType()))
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .limit(2)
                .collect(Collectors.joining(" and "));

        String profilePrefix = switch (travellerType) {
            case SOLO -> "Solo-friendly ";
            case COUPLE -> "Couple-focused ";
            case FRIENDS -> "Friends-focused ";
            case FAMILY -> "Family-focused ";
            case GROUP -> "Group-friendly ";
            default -> "";
        };

        if (categories.isBlank()) {
            return profilePrefix + "highlights around " + city;
        }
        return profilePrefix + categories + " exploration around " + city;
    }

    private String buildTravelNotes(PlannerLocationDto location,
            List<Place> places,
            double travelHours,
            TravellerType travellerType) {
        String profileHint = switch (travellerType) {
            case SOLO -> "Keep emergency contacts and prefer well-lit routes.";
            case COUPLE -> "Add a relaxed meal stop between attractions.";
            case FRIENDS -> "Include activity stops and flexible social breaks.";
            case FAMILY -> "Plan kid breaks, washrooms, and meal stops near attractions.";
            case GROUP -> "Ideal for family or friends; pre-book transport for smoother movement.";
            default -> "";
        };

        if (places.size() <= 1) {
            return "Keep a flexible schedule and add nearby walks around the main attraction. " + profileHint;
        }

        String city = location == null || location.getCity() == null ? "this route" : location.getCity();
        if (travelHours >= 2.0) {
            return "Start early to cover inter-spot transfers efficiently around " + city + ". " + profileHint;
        }
        return "Most places are nearby; local cab or metro hops are sufficient around " + city + ". " + profileHint;
    }

    private String buildPlaceTravelNote(Place place, String plannedVisitTimeSlot) {
        StringBuilder note = new StringBuilder();

        if (place.getBestTimeToVisit() != null && !place.getBestTimeToVisit().isBlank()) {
            note.append("Best in ").append(place.getBestTimeToVisit().trim()).append(". ");
        }

        if (plannedVisitTimeSlot != null && !plannedVisitTimeSlot.isBlank()) {
            note.append("Planned for ").append(plannedVisitTimeSlot).append(". ");
        }

        if (place.getRecommendedDurationHours() != null) {
            note.append("Keep about ").append(round2(place.getRecommendedDurationHours())).append("h here. ");
        }

        if (place.getLocalTips() != null && !place.getLocalTips().isBlank()) {
            note.append(place.getLocalTips().trim()).append(". ");
        }

        if (place.getSafetyScore() != null && place.getSafetyScore() >= 8.5) {
            note.append("Good choice for a relaxed, low-risk stop.");
        } else if (place.getSafetyScore() != null && place.getSafetyScore() < 7.0) {
            note.append("Keep an eye on time and stay alert while visiting.");
        } else {
            note.append("Stay flexible and keep a small buffer between stops.");
        }

        return note.toString().trim();
    }

    private boolean isBigCity(String city) {
        if (city == null) {
            return false;
        }
        String c = city.trim().toLowerCase(Locale.ROOT);
        return c.equals("delhi") || c.equals("mumbai") || c.equals("bangalore")
                || c.equals("kolkata") || c.equals("chennai") || c.equals("hyderabad")
                || c.equals("ahmedabad") || c.equals("pune") || c.equals("jaipur")
                || c.equals("ncr");
    }

    private List<Place> expandCandidatesWhenSparse(List<Place> candidates, String normalizedCity, int requestedDays) {
        if (normalizedCity == null) {
            return candidates == null ? new ArrayList<>() : candidates;
        }

        int desiredPoolSize = Math.max(requestedDays * 3, 6);
        boolean isSmall = !isBigCity(normalizedCity);
        
        List<Place> expanded = candidates == null ? new ArrayList<>() : new ArrayList<>(candidates);
        
        if (expanded.size() >= desiredPoolSize && !isSmall) {
            return expanded;
        }

        int targetPoolSize = isSmall ? desiredPoolSize + 8 : desiredPoolSize;

        String anchorState = null;
        String anchorRegion = null;

        if (!expanded.isEmpty()) {
            anchorState = expanded.stream()
                    .map(Place::getState)
                    .filter(state -> state != null && !state.isBlank())
                    .findFirst()
                    .orElse(null);

            anchorRegion = expanded.stream()
                    .map(Place::getRegion)
                    .filter(region -> region != null && !region.isBlank())
                    .findFirst()
                    .orElse(null);
        } else {
            List<Place> cityMatches = repository.findByCityContainingIgnoreCase(normalizedCity);
            if (!cityMatches.isEmpty()) {
                anchorState = cityMatches.get(0).getState();
                anchorRegion = cityMatches.get(0).getRegion();
            }
        }

        if (anchorState == null && anchorRegion == null) {
            return expanded;
        }

        List<Place> allPlaces = repository.findAll();

        for (Place place : allPlaces) {
            if (expanded.size() >= targetPoolSize) {
                break;
            }
            if (place == null || place.getPlaceId() == null
                    || expanded.stream().anyMatch(existing -> place.getPlaceId().equals(existing.getPlaceId()))) {
                continue;
            }

            boolean sameState = anchorState != null && place.getState() != null
                    && anchorState.equalsIgnoreCase(place.getState());
            boolean sameRegion = anchorRegion != null && place.getRegion() != null
                    && anchorRegion.equalsIgnoreCase(place.getRegion());
            if (sameState || sameRegion) {
                expanded.add(place);
            }
        }

        return expanded.stream()
                .sorted(buildPlannerComparator())
                .toList();
    }

    private String normalizeCategoryName(String category, String placeType) {
        String normalizedCategory = normalized(category);
        if ("engineering".equals(normalizedCategory)) {
            return "Infrastructure";
        }
        if ("bridge".equals(normalized(placeType))) {
            return "Landmark";
        }
        return category;
    }

    private List<Place> filterCityOutliers(List<Place> places) {
        List<Place> geoPlaces = places.stream()
                .filter(place -> place.getLatitude() != null && place.getLongitude() != null)
                .toList();

        if (geoPlaces.size() < 2) {
            return places;
        }

        double centroidLat = geoPlaces.stream().mapToDouble(Place::getLatitude).average().orElse(0.0);
        double centroidLon = geoPlaces.stream().mapToDouble(Place::getLongitude).average().orElse(0.0);

        return places.stream()
                .filter(place -> {
                    if (place.getLatitude() == null || place.getLongitude() == null) {
                        return true;
                    }
                    double distance = calculateDistanceKm(centroidLat, centroidLon, place.getLatitude(),
                            place.getLongitude());
                    return distance <= CITY_OUTLIER_DISTANCE_KM;
                })
                .toList();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<Place> findCandidatesWithFallback(String normalizedCity,
            String normalizedRegion,
            String normalizedCategory,
            String normalizedMood,
            String normalizedBudget,
            String normalizedCrowd,
            Boolean familyFriendly,
            String normalizedPriority,
            String normalizedSeason,
            String normalizedWeather,
            Double minRating) {
        List<Place> candidates = queryCandidates(
                normalizedCity,
                normalizedRegion,
                normalizedCategory,
                normalizedMood,
                normalizedBudget,
                normalizedCrowd,
                familyFriendly,
                normalizedPriority,
                normalizedSeason,
                normalizedWeather,
                minRating);

        if (!candidates.isEmpty()) {
            return candidates;
        }

        candidates = queryCandidates(
                normalizedCity,
                normalizedRegion,
                normalizedCategory,
                normalizedMood,
                null,
                null,
                familyFriendly,
                normalizedPriority,
                null,
                null,
                minRating);

        if (!candidates.isEmpty()) {
            return candidates;
        }

        candidates = queryCandidates(
                normalizedCity,
                normalizedRegion,
                null,
                null,
                null,
                null,
                familyFriendly,
                normalizedPriority,
                null,
                null,
                null);

        if (!candidates.isEmpty()) {
            return candidates;
        }

        return queryCandidates(
                normalizedCity,
                normalizedRegion,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private List<Place> queryCandidates(String normalizedCity,
            String normalizedRegion,
            String normalizedCategory,
            String normalizedMood,
            String normalizedBudget,
            String normalizedCrowd,
            Boolean familyFriendly,
            String normalizedPriority,
            String normalizedSeason,
            String normalizedWeather,
            Double minRating) {
        List<Place> candidates = repository.smartFilter(
                normalizedRegion,
                normalizedCategory,
                normalizedMood,
                normalizedBudget,
                normalizedCrowd,
                familyFriendly,
                normalizedPriority,
                normalizedSeason,
                normalizedWeather,
                minRating).stream()
                .filter(place -> matchesExactCity(place, normalizedCity))
                .sorted(buildPlannerComparator())
                .toList();

        if (normalizedCity != null) {
            candidates = filterCityOutliers(candidates);
        }

        return candidates;
    }

    private TravellerType parseTravellerType(String rawTravellerType) {
        String normalized = InputSanitizer.normalize(rawTravellerType);
        if (normalized == null) {
            return TravellerType.STANDARD;
        }

        return switch (normalized.toLowerCase(Locale.ROOT)) {
            case "solo" -> TravellerType.SOLO;
            case "couple" -> TravellerType.COUPLE;
            case "friends" -> TravellerType.FRIENDS;
            case "family" -> TravellerType.FAMILY;
            case "group" -> TravellerType.GROUP;
            default -> throw new BadRequestException("travellerType must be SOLO, COUPLE, FRIENDS, FAMILY, or GROUP");
        };
    }

    private double distancePenaltyWeight(TravellerType travellerType) {
        return switch (travellerType) {
            case SOLO -> 0.11;
            case COUPLE -> 0.10;
            case FRIENDS -> 0.08;
            case FAMILY -> 0.14;
            case GROUP -> 0.13;
            default -> 0.10;
        };
    }

    private double averageTravelSpeed(TravellerType travellerType) {
        return switch (travellerType) {
            case SOLO -> AVG_TRAVEL_SPEED_KMH + 4.0;
            case COUPLE -> AVG_TRAVEL_SPEED_KMH;
            case FRIENDS -> AVG_TRAVEL_SPEED_KMH + 1.0;
            case FAMILY -> AVG_TRAVEL_SPEED_KMH - 6.0;
            case GROUP -> AVG_TRAVEL_SPEED_KMH - 5.0;
            default -> AVG_TRAVEL_SPEED_KMH;
        };
    }

    private String normalizeCrowdLevel(String crowdLevel) {
        if (crowdLevel == null || crowdLevel.isBlank()) {
            return "MODERATE";
        }
        String normalized = crowdLevel.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        return switch (normalized) {
            case "LOW", "MODERATE", "HIGH", "VERY_HIGH" -> normalized;
            case "MEDIUM" -> "MODERATE";
            case "VERYHIGH" -> "VERY_HIGH";
            default -> "MODERATE";
        };
    }

    private double romanticScenicBonus(Place place) {
        String source = ((place.getCategory() == null ? "" : place.getCategory()) + " "
                + (place.getSignificance() == null ? "" : place.getSignificance()) + " "
                + (place.getMoodTags() == null ? "" : place.getMoodTags()) + " "
                + (place.getDescription() == null ? "" : place.getDescription()))
                .toLowerCase(Locale.ROOT);

        double bonus = 0.0;
        if (source.contains("scenic") || source.contains("romantic") || source.contains("sunset")) {
            bonus += 12.0;
        }
        if (source.contains("heritage") || source.contains("spiritual") || source.contains("nature")) {
            bonus += 5.0;
        }
        return bonus;
    }

    private double groupSuitabilityBonus(Place place) {
        double bonus = 0.0;
        if (place.getFamilyFriendly() != null && place.getFamilyFriendly()) {
            bonus += 14.0;
        }

        String category = normalized(place.getCategory());
        if ("landmark".equals(category) || "infrastructure".equals(category) || "heritage".equals(category)) {
            bonus += 6.0;
        }
        return bonus;
    }

    private double friendsFunBonus(Place place) {
        String source = ((place.getCategory() == null ? "" : place.getCategory()) + " "
                + (place.getSignificance() == null ? "" : place.getSignificance()) + " "
                + (place.getMoodTags() == null ? "" : place.getMoodTags()) + " "
                + (place.getDescription() == null ? "" : place.getDescription()))
                .toLowerCase(Locale.ROOT);

        double bonus = 0.0;
        if (source.contains("adventure") || source.contains("activity") || source.contains("night")
                || source.contains("cafe") || source.contains("urban") || source.contains("fun")) {
            bonus += 14.0;
        }
        if (source.contains("heritage") || source.contains("landmark") || source.contains("infrastructure")) {
            bonus += 4.0;
        }
        return bonus;
    }

    private double familySuitabilityBonus(Place place) {
        double bonus = 0.0;
        if (place.getFamilyFriendly() != null && place.getFamilyFriendly()) {
            bonus += 18.0;
        }

        String normalizedCategory = normalized(place.getCategory());
        if ("nature".equals(normalizedCategory) || "heritage".equals(normalizedCategory)
                || "landmark".equals(normalizedCategory)) {
            bonus += 5.0;
        }
        return bonus;
    }

    private double spiritualFamilyBonus(Place place) {
        String source = ((place.getCategory() == null ? "" : place.getCategory()) + " "
                + (place.getSignificance() == null ? "" : place.getSignificance()) + " "
                + (place.getDescription() == null ? "" : place.getDescription()))
                .toLowerCase(Locale.ROOT);
        if (source.contains("spiritual") || source.contains("temple") || source.contains("pilgrim")) {
            return 12.0;
        }
        return 0.0;
    }

    private double spiritualPenalty(Place place) {
        String source = ((place.getCategory() == null ? "" : place.getCategory()) + " "
                + (place.getSignificance() == null ? "" : place.getSignificance()) + " "
                + (place.getDescription() == null ? "" : place.getDescription()))
                .toLowerCase(Locale.ROOT);
        if (source.contains("spiritual") || source.contains("temple") || source.contains("pilgrim")) {
            return 6.0;
        }
        return 0.0;
    }

    private String buildPlaceSafetyAdvice(Place place) {
        if (place.getSafetyScore() == null) {
            return "Stay alert in crowded areas and use trusted transport options.";
        }

        if (place.getSafetyScore() >= 8.5) {
            return "Generally safe location; keep standard precautions for belongings.";
        }
        if (place.getSafetyScore() >= 7.0) {
            return "Visit during active hours and prefer known routes for return travel.";
        }
        return "Use extra caution, avoid late-night isolation, and travel with verified transport.";
    }

    private String buildBudgetAdvice(String budgetLevel) {
        String normalizedBudget = normalized(budgetLevel);
        if ("low".equals(normalizedBudget)) {
            return "Prefer public transport, free attractions, and local street-food options to control spending.";
        }
        if ("high".equals(normalizedBudget)) {
            return "Combine premium stays, guided experiences, and advance reservations for smoother planning.";
        }
        return "Balance paid landmarks with free local spots and reserve major entries in advance.";
    }

    private String buildGeneralSafetyTips(String travellerType) {
        String normalizedType = normalized(travellerType);
        if ("solo".equals(normalizedType)) {
            return "Share daily plans with a contact, prefer well-lit routes, and avoid isolated areas at late hours.";
        }
        if ("family".equals(normalizedType)) {
            return "Keep child essentials ready, schedule regular breaks, and use verified transport providers.";
        }
        if ("friends".equals(normalizedType) || "group".equals(normalizedType)) {
            return "Stay coordinated with a common meetup point and avoid splitting in unfamiliar crowded zones.";
        }
        return "Carry ID copies, hydrate, and use trusted transport and payment methods across all stops.";
    }
}
