package com.riya.smarttravel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.riya.smarttravel.dto.AdditionalRecommendationDto;
import com.riya.smarttravel.dto.AiEnhancementDto;
import com.riya.smarttravel.dto.PlannerDayDto;
import com.riya.smarttravel.dto.PlannerLocationDto;
import com.riya.smarttravel.dto.PlannerPlaceDto;
import com.riya.smarttravel.dto.PlannerRequest;
import com.riya.smarttravel.dto.PlannerResponseDto;
import com.riya.smarttravel.dto.PlaceResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

enum AiFallbackStatus {
    NOT_CONFIGURED,
    CALL_FAILED,
    SUCCESS
}

record AiFallbackResult(AiFallbackStatus status, PlannerResponseDto response) {
    public static AiFallbackResult notConfigured() {
        return new AiFallbackResult(AiFallbackStatus.NOT_CONFIGURED, null);
    }

    public static AiFallbackResult failed() {
        return new AiFallbackResult(AiFallbackStatus.CALL_FAILED, null);
    }

    public static AiFallbackResult success(PlannerResponseDto dto) {
        return new AiFallbackResult(AiFallbackStatus.SUCCESS, dto);
    }

    public boolean isSuccess() {
        return status == AiFallbackStatus.SUCCESS;
    }
}

@Slf4j
@Service
public class PlannerAiService {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${planner.ai.enabled:true}")
    private boolean aiEnabled = true;

    @Value("${planner.ai.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String aiApiUrl;

    @Value("${planner.ai.api.key}")
    private String aiApiKey;

    @Value("${planner.ai.model:openai/gpt-4o-mini}")
    private String aiModel;

    @Value("${planner.ai.max-tokens:4000}")
    private int aiMaxTokens;

    @PostConstruct
    public void validateAiConfiguration() {
        if (aiEnabled && (aiApiKey == null || aiApiKey.isBlank())) {
            log.warn("AI itinerary generation is DISABLED: OPENROUTER_API_KEY is not set. Set the environment variable to enable AI-powered itineraries.");
        }
    }

    public AiFallbackResult generateFallbackItinerary(PlannerRequest request,
            String city,
            int days,
            double maxHoursPerDay,
            String travellerType) {
        if (!isAiConfigured()) {
            return AiFallbackResult.notConfigured();
        }

        try {
            String effectiveCity = city == null || city.isBlank() ? "India" : city;
            String prompt = buildFallbackPrompt(
                    effectiveCity,
                    days,
                    travellerType,
                    request.getBudgetLevel(),
                    request.getPreferences(),
                    maxHoursPerDay);
            log.info("OpenRouter request started for city={}, model={}", effectiveCity, aiModel);
            JsonNode aiJson = callModelForJson(prompt);
            if (aiJson == null) {
                return AiFallbackResult.failed();
            }

            PlannerResponseDto response = mapFallbackResponse(aiJson, days, travellerType, maxHoursPerDay);
            log.info("OpenRouter response received, parsing itinerary for city={}", effectiveCity);
            log.info("AI itinerary parsed successfully: days={}, places={}", response.getGeneratedDays(), response.getTotalPlaces());
            return AiFallbackResult.success(response);
        } catch (Exception ex) {
            log.error("AI fallback itinerary generation failed", ex);
            return AiFallbackResult.failed();
        }
    }

    public Optional<AiEnhancementDto> enhanceItinerary(PlannerResponseDto itinerary,
            String travellerType,
            String preferences,
            String budgetLevel) {
        if (!isAiConfigured()) {
            return Optional.empty();
        }

        try {
            String itineraryJson = objectMapper.writeValueAsString(itinerary);
            String prompt = buildEnhancementPrompt(itineraryJson, travellerType, preferences, budgetLevel);
            JsonNode aiJson = callModelForJson(prompt);
            if (aiJson == null) {
                return Optional.empty();
            }

            List<String> tips = new ArrayList<>();
            JsonNode tipsNode = aiJson.path("tips");
            if (tipsNode.isArray()) {
                for (JsonNode tip : tipsNode) {
                    if (tip != null && tip.isTextual()) {
                        tips.add(tip.asText());
                    }
                }
            }

            List<AdditionalRecommendationDto> recommendations = new ArrayList<>();
            JsonNode recommendationsNode = aiJson.path("additionalRecommendations");
            if (recommendationsNode.isArray()) {
                for (JsonNode recommendation : recommendationsNode) {
                    recommendations.add(AdditionalRecommendationDto.builder()
                            .placeName(recommendation.path("placeName").asText(null))
                            .category(recommendation.path("category").asText(null))
                            .reason(recommendation.path("reason").asText(null))
                            .build());
                }
            }

            List<String> whyChoosePlace = new ArrayList<>();
            JsonNode whyChooseNode = aiJson.path("whyChoosePlace");
            if (whyChooseNode.isArray()) {
                for (JsonNode item : whyChooseNode) {
                    if (item != null && item.isTextual()) {
                        whyChoosePlace.add(item.asText());
                    }
                }
            }

            return Optional.of(AiEnhancementDto.builder()
                    .aiSummary(aiJson.path("aiSummary").asText(null))
                    .tips(tips)
                    .additionalRecommendations(recommendations)
                    .aboutPlace(aiJson.path("aboutPlace").isTextual() ? aiJson.path("aboutPlace").asText() : null)
                    .whyChoosePlace(whyChoosePlace.isEmpty() ? null : whyChoosePlace)
                    .build());
        } catch (Exception ex) {
            log.error("AI itinerary enhancement failed", ex);
            return Optional.empty();
        }
    }

    private PlannerResponseDto mapFallbackResponse(JsonNode root,
            int requestedDays,
            String travellerType,
            double maxHoursPerDay) {
        List<PlannerDayDto> itinerary = new ArrayList<>();
        JsonNode daysNode = root.path("itinerary");
        if (daysNode.isArray()) {
            for (JsonNode dayNode : daysNode) {
                itinerary.add(mapDay(dayNode));
            }
        }

        int generatedDays = root.path("generatedDays").isInt()
                ? root.path("generatedDays").asInt()
                : itinerary.size();
        int totalPlaces = root.path("totalPlaces").isInt()
                ? root.path("totalPlaces").asInt()
                : itinerary.stream().mapToInt(day -> day.getPlaces() == null ? 0 : day.getPlaces().size()).sum();

        double totalTripHours = root.path("totalTripHours").isNumber()
                ? root.path("totalTripHours").asDouble()
                : itinerary.stream().mapToDouble(day -> {
                    double visit = day.getTotalPlannedHours() == null ? 0.0 : day.getTotalPlannedHours();
                    double travel = day.getEstimatedTravelHours() == null ? 0.0 : day.getEstimatedTravelHours();
                    return visit + travel;
                }).sum();

        String safeTravellerType = travellerType == null ? "STANDARD" : travellerType.toUpperCase(Locale.ROOT);
        String aiSummary = root.path("aiSummary").isTextual()
                ? root.path("aiSummary").asText("")
                : "";

        List<String> tips = new ArrayList<>();
        JsonNode tipsNode = root.path("tips");
        if (tipsNode.isArray()) {
            for (JsonNode tip : tipsNode) {
                if (tip != null && tip.isTextual()) {
                    tips.add(tip.asText());
                }
            }
        }

        List<AdditionalRecommendationDto> recommendations = new ArrayList<>();
        JsonNode recommendationsNode = root.path("additionalRecommendations");
        if (recommendationsNode.isArray()) {
            for (JsonNode recommendation : recommendationsNode) {
                recommendations.add(AdditionalRecommendationDto.builder()
                        .placeName(recommendation.path("placeName").asText(null))
                        .category(recommendation.path("category").asText(null))
                        .reason(recommendation.path("reason").asText(null))
                        .build());
            }
        }

        if (aiSummary.isBlank()) {
            String summary = root.path("summary").asText("");
            aiSummary = summary.isBlank()
                    ? "AI generated itinerary for " + requestedDays + " days in "
                            + safeTravellerType.toLowerCase(Locale.ROOT)
                    : summary;
        }

        if (tips.isEmpty()) {
            tips.add("Start early to avoid peak crowds and heat.");
            tips.add("Keep some flexibility for local discoveries.");
            tips.add("Carry water, cash, and a charged phone.");
        }

        if (recommendations.isEmpty()) {
            recommendations.addAll(deriveRecommendationsFromItinerary(itinerary));
        }

        String aboutPlace = root.path("aboutPlace").isTextual() ? root.path("aboutPlace").asText() : "";
        List<String> whyChoosePlace = new ArrayList<>();
        JsonNode whyChooseNode = root.path("whyChoosePlace");
        if (whyChooseNode.isArray()) {
            for (JsonNode item : whyChooseNode) {
                if (item != null && item.isTextual()) {
                    whyChoosePlace.add(item.asText());
                }
            }
        }

        return PlannerResponseDto.builder()
                .requestedDays(requestedDays)
                .generatedDays(generatedDays)
                .totalPlaces(totalPlaces)
                .travellerType(safeTravellerType)
                .dataSource(root.path("dataSource").asText("AI_GENERATED"))
                .maxHoursPerDay(maxHoursPerDay)
                .totalTripHours(round2(totalTripHours))
                .summary(root.path("summary").asText("AI-generated itinerary"))
                .budgetAdvice(root.path("budgetAdvice").asText(null))
                .generalSafetyTips(root.path("generalSafetyTips").asText(null))
                .aiSummary(aiSummary)
                .tips(tips.isEmpty() ? Collections.emptyList() : tips)
                .additionalRecommendations(recommendations.isEmpty() ? Collections.emptyList() : recommendations)
                .aboutPlace(aboutPlace)
                .whyChoosePlace(whyChoosePlace.isEmpty() ? Collections.emptyList() : whyChoosePlace)
                .itinerary(itinerary)
                .build();
    }

    private List<AdditionalRecommendationDto> deriveRecommendationsFromItinerary(List<PlannerDayDto> itinerary) {
        List<AdditionalRecommendationDto> recommendations = new ArrayList<>();
        if (itinerary == null) {
            return recommendations;
        }

        for (PlannerDayDto day : itinerary) {
            if (day == null || day.getPlaces() == null) {
                continue;
            }

            for (PlannerPlaceDto place : day.getPlaces()) {
                if (place == null || place.getPlaceName() == null || place.getPlaceName().isBlank()) {
                    continue;
                }

                String reason = place.getSignificance();
                if (reason == null || reason.isBlank()) {
                    reason = place.getDescription();
                }
                if (reason == null || reason.isBlank()) {
                    reason = "Recommended to enrich your trip plan.";
                }

                recommendations.add(AdditionalRecommendationDto.builder()
                        .placeName(place.getPlaceName())
                        .category(place.getCategory())
                        .reason(reason)
                        .build());

                if (recommendations.size() >= 3) {
                    return recommendations;
                }
            }
        }

        return recommendations;
    }

    private PlannerDayDto mapDay(JsonNode dayNode) {
        PlannerLocationDto location = PlannerLocationDto.builder()
                .city(dayNode.path("location").path("city").asText(null))
                .state(dayNode.path("location").path("state").asText(null))
                .build();

        List<PlannerPlaceDto> places = new ArrayList<>();
        JsonNode placesNode = dayNode.path("places");
        if (placesNode.isArray()) {
            for (JsonNode place : placesNode) {
                places.add(PlannerPlaceDto.builder()
                        .placeId(place.path("placeId").asText(null))
                        .placeName(place.path("placeName").asText(null))
                        .category(place.path("category").asText(null))
                        .significance(place.path("significance").asText(null))
                        .description(place.path("description").asText(null))
                        .localTips(place.path("localTips").asText(null))
                        .safetyAdvice(place.path("safetyAdvice").asText(null))
                        .rating(place.path("rating").isNumber() ? place.path("rating").asDouble() : null)
                        .recommendedDurationHours(place.path("recommendedDurationHours").isNumber()
                                ? place.path("recommendedDurationHours").asDouble()
                                : null)
                        .bestTimeToVisit(place.path("bestTimeToVisit").asText(null))
                        .idealVisitTime(place.path("idealVisitTime").asText(null))
                        .plannedVisitTimeSlot(place.path("plannedVisitTimeSlot").asText(null))
                        .build());
            }
        }

        return PlannerDayDto.builder()
                .dayNumber(dayNode.path("dayNumber").asInt())
                .daySummary(dayNode.path("daySummary").asText(null))
                .estimatedTravelHours(dayNode.path("estimatedTravelHours").isNumber()
                        ? dayNode.path("estimatedTravelHours").asDouble()
                        : null)
                .location(location)
                .places(places)
                .totalPlannedHours(dayNode.path("totalPlannedHours").isNumber()
                        ? dayNode.path("totalPlannedHours").asDouble()
                        : null)
                .travelNotes(dayNode.path("travelNotes").asText(null))
                .build();
    }

    private JsonNode callModelForJson(String prompt) {
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", aiModel);
            payload.put("temperature", 0.2);
            payload.put("max_tokens", Math.max(256, aiMaxTokens));
            payload.set("messages", objectMapper.createArrayNode()
                    .add(objectMapper.createObjectNode()
                            .put("role", "system")
                            .put("content", "You are a strict JSON API. Return only valid JSON without markdown."))
                    .add(objectMapper.createObjectNode()
                            .put("role", "user")
                            .put("content", prompt)));
            payload.set("response_format", objectMapper.createObjectNode().put("type", "json_object"));

            String endpoint = buildOpenRouterEndpoint();

            Request request = new Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", "Bearer " + aiApiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(objectMapper.writeValueAsString(payload), JSON_MEDIA_TYPE))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() == null) {
                    log.warn("AI API call failed because response body is empty. status={}", response.code());
                    return null;
                }

                String responseText = response.body().string();
                JsonNode responseNode = objectMapper.readTree(responseText);

                if (!response.isSuccessful()) {
                    log.warn("AI API call failed with status {} and body {}", response.code(), responseText);
                    return null;
                }

                JsonNode contentNode = responseNode.path("choices").path(0).path("message").path("content");
                if (contentNode.isMissingNode() || contentNode.isNull()) {
                    return null;
                }

                String jsonContent = stripMarkdownFence(contentNode.asText());
                return objectMapper.readTree(jsonContent);
            }
        } catch (Exception ex) {
            log.error("Error while calling AI model", ex);
            return null;
        }
    }

    private String stripMarkdownFence(String content) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.startsWith("```") && trimmed.endsWith("```")) {
            int firstLine = trimmed.indexOf('\n');
            if (firstLine >= 0) {
                return trimmed.substring(firstLine + 1, trimmed.length() - 3).trim();
            }
        }
        return trimmed;
    }

    public boolean isAiConfigured() {
        return aiApiUrl != null && !aiApiUrl.trim().isEmpty() &&
                aiApiKey != null && !aiApiKey.trim().isEmpty() &&
                !"your-api-key".equals(aiApiKey);
    }

    private String buildFallbackPrompt(String city,
            int days,
            String travellerType,
            String budget,
            String preferences,
            double maxHoursPerDay) {
        String budgetSafe = budget == null || budget.isBlank() ? "Medium" : budget;
        String preferencesSafe = preferences == null || preferences.isBlank() ? "nature, heritage, food" : preferences;

        return """
                You are an expert Indian travel planner. Generate VALID JSON ONLY (no markdown, no explanations, no commentary).

                MUST-HAVE RULES:
                1. Return ONLY JSON.
                2. Every day MUST have dataSource, dayNumber, daySummary, location (city, state), places[] with 2-4 items, totalPlannedHours, estimatedTravelHours, travelNotes.
                3. Every place MUST have: placeId, placeName, category, significance, description, localTips, safetyAdvice, rating, recommendedDurationHours, bestTimeToVisit, idealVisitTime, plannedVisitTimeSlot.
                4. Use REAL and correct places ONLY within the city limits of %s, India. Strictly do NOT include places from neighboring cities, different states, or any place outside this city (for example, if Kolkata is requested, do not include Puri or Konark or other cities in Odisha; keep all places strictly within the requested city's borders). Every single place in the itinerary MUST be physically located inside this city. Ensure they are famous, accurate tourist landmarks and spots of this city.
                5. Ensure plannedVisitTimeSlot and idealVisitTime follow formats like "9:00 AM - 11:30 AM", "12:00 PM - 2:30 PM", etc.
                6. Sum of place duration hours + travel hours must not exceed %.1f per day.
                7. Distribute %d days across %s locations.
                8. Generate a highly detailed and correct itinerary: do not use placeholders or generic "sightseeing" stops. Give specific, rich descriptions (2-3 complete sentences per place) describing its unique features, exact tips, and safety tips.
                9. Include 'aboutPlace': A beautiful, compelling 3-4 sentence description introducing %s as a travel destination, detailing its history, culture, charm, and overall vibe.
                10. Include 'whyChoosePlace': An array of exactly 5 highly compelling points (strings) explaining why a traveler should choose this destination, highlighting its iconic activities, local cuisine/street food, scenery, and culture.

                PREFERENCE: %s
                BUDGET: %s
                TRAVELLER_TYPE: %s

                Return JSON only (no markdown, no ```):
                {
                  "generatedDays": %d,
                  "totalPlaces": number,
                  "totalTripHours": number,
                  "summary": "2-3 sentence summary",
                  "budgetAdvice": "1 sentence advice",
                  "generalSafetyTips": "1 sentence safety tip",
                  "aboutPlace": "3-4 sentence detailed introduction about %s",
                  "whyChoosePlace": [
                    "Reason 1 about local landmarks or sights",
                    "Reason 2 about local cuisine or food specialties",
                    "Reason 3 about local culture, festivals or vibes",
                    "Reason 4 about scenic views or nature highlights",
                    "Reason 5 about ease of travel or unique local experiences"
                  ],
                  "dataSource": "AI_GENERATED",
                  "itinerary": [
                    {
                      "dayNumber": 1,
                      "location": {"city": "%s", "state": "..."},
                      "daySummary": "Detailed description of the day's highlights",
                      "travelNotes": "Local transport or morning timing note",
                      "totalPlannedHours": number,
                      "estimatedTravelHours": number,
                      "places": [
                        {
                          "placeId": "id1",
                          "placeName": "Real Famous Place",
                          "category": "heritage|nature|spiritual|beach|adventure|food",
                          "significance": "Historical or cultural importance",
                          "description": "2-3 sentence description explaining what to see and do here.",
                          "localTips": "Specific practical advice (e.g. best photo spot, what to wear)",
                          "safetyAdvice": "Realistic safety tip for this specific spot",
                          "rating": 4.5,
                          "recommendedDurationHours": 2.0,
                          "bestTimeToVisit": "morning",
                          "idealVisitTime": "9:00 AM - 11:00 AM",
                          "plannedVisitTimeSlot": "9:00 AM - 11:00 AM"
                        }
                      ]
                    }
                  ]
                }
                """
                .formatted(city, maxHoursPerDay, days, city, city, preferencesSafe, budgetSafe, travellerType, days,
                        city, city);
    }

    private String buildEnhancementPrompt(String itineraryJson,
            String travellerType,
            String preferences,
            String budgetLevel) {
        String preferencesSafe = preferences == null || preferences.isBlank() ? "general" : preferences;
        String budgetSafe = budgetLevel == null || budgetLevel.isBlank() ? "Medium" : budgetLevel;

        return """
                You are an expert Indian travel planner. Enhance the given itinerary. Return ONLY JSON (no markdown, no explanations).

                INPUT:
                %s

                TRAVELLER_TYPE: %s
                PREFERENCES: %s
                BUDGET: %s

                TASKS (MUST COMPLETE ALL):
                1. Write personalized aiSummary (2-3 sentences) explaining why this itinerary matches traveller type.
                2. Generate 3-5 travel tips (tips array of strings).
                3. Add 2-3 additionalRecommendations (place names, categories, reasons).
                4. Generate 'aboutPlace': A beautiful, compelling 3-4 sentence description introducing the destination city (from the input) as a travel destination, detailing its history, culture, charm, and overall vibe.
                5. Generate 'whyChoosePlace': An array of exactly 5 highly compelling points (strings) explaining why a traveler should choose this destination, highlighting its iconic activities, local cuisine/street food, scenery, and culture.

                OUTPUT ONLY JSON (no markdown):
                {
                  "aiSummary": "2-3 sentence personalized explanation",
                  "tips": ["tip1", "tip2", "tip3"],
                  "additionalRecommendations": [
                    {"placeName": "real place name", "category": "heritage|food|spiritual|nature", "reason": "why recommended"}
                  ],
                  "aboutPlace": "3-4 sentence detailed introduction of the city",
                  "whyChoosePlace": [
                    "Reason 1 about local landmarks or sights",
                    "Reason 2 about local cuisine or food specialties",
                    "Reason 3 about local culture, festivals or vibes",
                    "Reason 4 about scenic views or nature highlights",
                    "Reason 5 about ease of travel or unique local experiences"
                  ]
                }
                """
                .formatted(itineraryJson, travellerType, preferencesSafe, budgetSafe);
    }

    private String buildOpenRouterEndpoint() {
        String base = aiApiUrl == null ? "" : aiApiUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base;
    }

    public List<Map<String, String>> getRegionalRecommendations(String region) {
        if (region == null || region.isBlank()) {
            return Collections.emptyList();
        }

        String cleanRegion = region.trim();

        if (isAiConfigured()) {
            try {
                String prompt = buildRegionalRecommendationPrompt(cleanRegion);
                JsonNode rootNode = callModelForJson(prompt);
                if (rootNode != null && rootNode.isArray()) {
                    List<Map<String, String>> recommendations = new ArrayList<>();
                    for (JsonNode item : rootNode) {
                        Map<String, String> rec = new HashMap<>();
                        rec.put("name", item.path("name").asText(""));
                        rec.put("city", item.path("city").asText(""));
                        rec.put("description", item.path("description").asText(""));
                        rec.put("category", item.path("category").asText(""));
                        recommendations.add(rec);
                    }
                    if (!recommendations.isEmpty()) {
                        return recommendations;
                    }
                }
            } catch (Exception e) {
                log.error("AI regional recommendation failed, falling back", e);
            }
        }

        return getFallbackRegionalRecommendations(cleanRegion);
    }

    private String buildRegionalRecommendationPrompt(String region) {
        String availablePlaces = switch (region.toLowerCase(Locale.ROOT)) {
            case "north" ->
                "Srinagar, Shimla, Manali, Haridwar, Mussoorie, Nainital, Kasol, Mcleodganj, Mathura, Varanasi";
            case "south" -> "Hampi, Gokarna, Rameswaram, Andaman";
            case "east" -> "Cherrapunji, Bodhgaya";
            case "west" -> "Indore, Panchmarhi";
            default -> "Srinagar, Hampi, Cherrapunji, Indore";
        };

        return """
                You are a regional travel expert for India. Recommend exactly 3 places/cities to visit in %s India.

                Choose ONLY from this exact list of available destinations (these are distinct from the main shown options):
                [%s]

                For each recommended place, provide:
                1. name: The exact name matching the casing from the list.
                2. city: State or sub-region name (e.g. "Jammu & Kashmir" for Srinagar, "Karnataka" for Hampi).
                3. description: A compelling 1-2 sentence description explaining why someone should visit this place (highlighting its unique vibes, attractions, or food).
                4. category: A single-word category (e.g. "heritage", "nature", "spiritual", "beach", "adventure").

                Return ONLY a valid JSON array of objects. No markdown formatting, no code block backticks, no explanations.

                Example Format:
                [
                  {
                    "name": "Srinagar",
                    "city": "Jammu & Kashmir",
                    "description": "Experience beautiful shikara rides on Dal Lake and stroll through historic Mughal gardens.",
                    "category": "nature"
                  }
                ]
                """
                .formatted(region, availablePlaces);
    }

    private List<Map<String, String>> getFallbackRegionalRecommendations(String region) {
        List<Map<String, String>> recs = new ArrayList<>();

        switch (region.toLowerCase(Locale.ROOT)) {
            case "north":
                recs.add(createRecMap("Srinagar", "Jammu & Kashmir",
                        "Experience beautiful shikara rides on Dal Lake, houseboats, and historic Mughal gardens.",
                        "nature"));
                recs.add(createRecMap("Shimla", "Himachal Pradesh",
                        "Walk through colonial-era Mall Road and enjoy panoramic snow-capped mountain views.",
                        "nature"));
                recs.add(createRecMap("Manali", "Himachal Pradesh",
                        "A popular hotspot for adventure, scenic valleys, snow sports, and vibrant local cafes.",
                        "adventure"));
                break;
            case "south":
                recs.add(createRecMap("Hampi", "Karnataka",
                        "Explore the surreal boulder-strewn landscape and magnificent ruins of the ancient Vijayanagara Empire.",
                        "heritage"));
                recs.add(createRecMap("Gokarna", "Karnataka",
                        "Relax on pristine, quiet beaches like Om Beach with a serene, laid-back atmosphere.",
                        "beach"));
                recs.add(createRecMap("Rameswaram", "Tamil Nadu",
                        "A peaceful island town famous for its historic temples, long corridors, and spiritual vibe.",
                        "spiritual"));
                break;
            case "east":
                recs.add(createRecMap("Cherrapunji", "Meghalaya",
                        "Famous for living root bridges, cascading waterfalls, and being one of the wettest places on earth.",
                        "nature"));
                recs.add(createRecMap("Bodhgaya", "Bihar",
                        "A profound spiritual center where Lord Buddha attained enlightenment under the Bodhi Tree.",
                        "spiritual"));
                break;
            case "west":
                recs.add(createRecMap("Indore", "Madhya Pradesh",
                        "A food lover's paradise, famous for its vibrant Sarafa night food market and historic palaces.",
                        "city"));
                recs.add(createRecMap("Panchmarhi", "Madhya Pradesh",
                        "The queen of Satpura, offering scenic viewpoints, waterfalls, and ancient rock temples.",
                        "nature"));
                break;
            default:
                recs.add(createRecMap("Srinagar", "Jammu & Kashmir", "Mughal gardens and houseboat stays.", "nature"));
                recs.add(createRecMap("Hampi", "Karnataka", "Historic ruins of the Vijayanagara Empire.", "heritage"));
                break;
        }

        return recs;
    }

    private Map<String, String> createRecMap(String name, String city, String description, String category) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("city", city);
        map.put("description", description);
        map.put("category", category);
        return map;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public List<PlaceResponseDto> generatePlacesForCity(String query) {
        if (!isAiConfigured()) {
            return Collections.emptyList();
        }
        try {
            String prompt = buildPlaceGenerationPrompt(query);
            log.info("Generating places for query: {}", query);
            JsonNode aiJson = callModelForJson(prompt);
            if (aiJson == null || !aiJson.has("places") || !aiJson.get("places").isArray()) {
                return Collections.emptyList();
            }
            JsonNode placesArray = aiJson.get("places");
            List<PlaceResponseDto> places = new ArrayList<>();
            for (JsonNode node : placesArray) {
                places.add(mapToPlaceResponseDto(node, query));
            }
            log.info("Successfully generated {} places for query: {}", places.size(), query);
            return places;
        } catch (Exception e) {
            log.error("Failed to generate places for query: {}", query, e);
            return Collections.emptyList();
        }
    }

    private String buildPlaceGenerationPrompt(String query) {
        return """
            You are an expert Indian travel planner. The user has searched for "%s".
            Generate a list of 5-8 famous tourist places, landmarks, or attractions for this destination.
            CRITICAL REQUIREMENT: ONLY return places located within INDIA. If the requested destination is outside India or invalid, return an empty array for places.
            Return ONLY a valid JSON object with a single key "places" containing an array of objects. No markdown, no explanations.
            
            Each object MUST have the following properties:
            "placeId": a unique string identifier starting with "ai-"
            "placeName": name of the place
            "city": city name
            "state": state name
            "region": region name (North, South, East, West, Central, North East)
            "placeType": type of place (e.g. Monument, Beach, Temple, Park, Museum)
            "category": general category (heritage, nature, spiritual, beach, adventure, city)
            "moodTags": comma separated moods (e.g. peaceful, historic)
            "significance": why it's important
            "description": a 2-3 sentence description
            "bestTimeToVisit": e.g. "October to March"
            "idealVisitTime": e.g. "Morning"
            "recommendedDurationHours": number (e.g. 2.5)
            "entryFee": number (e.g. 50, use 0 if free)
            "rating": number between 3.0 and 5.0
            "crowdLevel": "LOW", "MODERATE", "HIGH", or "VERY_HIGH"
            "familyFriendly": boolean
            "budgetLevel": "Low", "Medium", or "High"
            "localTips": 1 sentence local tip
            "safetyScore": number between 1.0 and 10.0
            "cleanlinessScore": number between 1.0 and 10.0
            "priority": "Must Visit", "Recommended", or "Optional"
            """
            .formatted(query);
    }

    private PlaceResponseDto mapToPlaceResponseDto(JsonNode node, String query) {
        return PlaceResponseDto.builder()
                .placeId(node.path("placeId").asText("ai-" + java.util.UUID.randomUUID().toString().substring(0, 8)))
                .placeName(node.path("placeName").asText(query))
                .city(node.path("city").asText(query))
                .state(node.path("state").asText("Unknown"))
                .region(node.path("region").asText("Unknown"))
                .placeType(node.path("placeType").asText("Attraction"))
                .category(node.path("category").asText("city"))
                .moodTags(node.path("moodTags").asText(""))
                .significance(node.path("significance").asText(""))
                .description(node.path("description").asText("A popular destination."))
                .bestTimeToVisit(node.path("bestTimeToVisit").asText(""))
                .idealVisitTime(node.path("idealVisitTime").asText(""))
                .recommendedDurationHours(node.path("recommendedDurationHours").isNumber() ? node.path("recommendedDurationHours").asDouble() : 2.0)
                .entryFee(node.path("entryFee").isNumber() ? node.path("entryFee").asDouble() : 0.0)
                .rating(node.path("rating").isNumber() ? node.path("rating").asDouble() : 4.0)
                .crowdLevel(node.path("crowdLevel").asText("MODERATE"))
                .familyFriendly(node.path("familyFriendly").isBoolean() ? node.path("familyFriendly").asBoolean() : true)
                .budgetLevel(node.path("budgetLevel").asText("Medium"))
                .localTips(node.path("localTips").asText(""))
                .safetyScore(node.path("safetyScore").isNumber() ? node.path("safetyScore").asDouble() : 8.0)
                .cleanlinessScore(node.path("cleanlinessScore").isNumber() ? node.path("cleanlinessScore").asDouble() : 8.0)
                .priority(node.path("priority").asText("Recommended"))
                .build();
    }
}