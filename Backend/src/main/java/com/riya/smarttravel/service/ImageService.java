package com.riya.smarttravel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.smarttravel.dto.ImageDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class ImageService {

    private static final String UNSPLASH_API = "https://api.unsplash.com";
    
    @Value("${unsplash.api.key}")
    private String unsplashApiKey;
    
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Search for travel/place images
     */
    @Cacheable(value = "travel-images", key = "#query")
    public List<ImageDto> searchImages(String query) {
        if (unsplashApiKey == null || unsplashApiKey.isEmpty()) {
            log.warn("Unsplash API key not configured");
            return buildFallbackImages(query);
        }

        try {
            String encodedQuery = query.replace(" ", "+");
            String url = String.format(
                "%s/search/photos?query=%s&per_page=10&client_id=%s",
                UNSPLASH_API, encodedQuery, unsplashApiKey
            );

            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                log.error("Failed to fetch images: {}", response.code());
                return buildFallbackImages(query);
            }

            JsonNode data = objectMapper.readTree(response.body().string());
            JsonNode results = data.get("results");
            List<ImageDto> images = new ArrayList<>();

            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    ImageDto image = mapToImageDto(node);
                    images.add(image);
                }
            }

            return images.isEmpty() ? buildFallbackImages(query) : images;

        } catch (Exception e) {
            log.error("Error fetching images for query: {}", query, e);
            return buildFallbackImages(query);
        }
    }

    /**
     * Get random images (cached lightly)
     */
    public List<ImageDto> getRandomImages(String query) {
        try {
            String encodedQuery = query.replace(" ", "+");
            String url = String.format(
                "%s/search/photos?query=%s&per_page=6&client_id=%s&order_by=random",
                UNSPLASH_API, encodedQuery, unsplashApiKey
            );

            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                return buildFallbackImages(query);
            }

            JsonNode data = objectMapper.readTree(response.body().string());
            JsonNode results = data.get("results");
            List<ImageDto> images = new ArrayList<>();

            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    ImageDto image = mapToImageDto(node);
                    images.add(image);
                }
            }

            return images.isEmpty() ? buildFallbackImages(query) : images;

        } catch (Exception e) {
            log.error("Error fetching random images for query: {}", query, e);
            return buildFallbackImages(query);
        }
    }

    /**
     * Get images for a specific city
     */
    @Cacheable(value = "city-images", key = "#city")
    public List<ImageDto> getImagesByCity(String city) {
        return searchImages(city + " travel");
    }

    /**
     * Get images for a place/landmark
     */
    @Cacheable(value = "place-images", key = "#placeName")
    public List<ImageDto> getImagesByPlace(String placeName) {
        return searchImages(placeName);
    }

    private ImageDto mapToImageDto(JsonNode node) {
        ImageDto image = new ImageDto();
        image.setId(node.path("id").asText("unknown"));
        image.setDescription(node.path("description").isNull() ? "Travel view" : node.path("description").asText("Travel view"));
        image.setAltDescription(node.path("alt_description").isNull() ? image.getDescription() : node.path("alt_description").asText(image.getDescription()));

        JsonNode urls = node.get("urls");
        if (urls != null) {
            image.setSmallUrl(urls.path("small").asText());
            image.setRegularUrl(urls.path("regular").asText());
            image.setFullUrl(urls.path("full").asText());
        }

        JsonNode user = node.get("user");
        if (user != null) {
            image.setPhotographer(user.path("name").asText("Unknown"));
            image.setPhotographerUrl(user.path("portfolio_url").asText("https://unsplash.com/"));
        }

        return image;
    }

    private List<ImageDto> buildFallbackImages(String query) {
        String normalized = query == null ? "travel" : query.trim().toLowerCase(Locale.ROOT);
        List<ImageDto> images = new ArrayList<>();

        images.add(createFallbackImage(normalized + "_travel_fallback_1",
                "https://source.unsplash.com/featured/400x300/?" + normalized,
                "https://source.unsplash.com/featured/800x600/?" + normalized,
                "https://source.unsplash.com/featured/1200x900/?" + normalized,
                capitalize(normalized) + " travel at golden hour"));

        images.add(createFallbackImage(normalized + "_travel_fallback_2",
                "https://source.unsplash.com/featured/400x300/?" + normalized + ",landmark",
                "https://source.unsplash.com/featured/800x600/?" + normalized + ",landmark",
                "https://source.unsplash.com/featured/1200x900/?" + normalized + ",landmark",
                "Street-level view of " + capitalize(normalized) + " travel"));

        images.add(createFallbackImage(normalized + "_travel_fallback_3",
                "https://source.unsplash.com/featured/400x300/?" + normalized + ",architecture",
                "https://source.unsplash.com/featured/800x600/?" + normalized + ",architecture",
                "https://source.unsplash.com/featured/1200x900/?" + normalized + ",architecture",
                capitalize(normalized) + " travel skyline and architecture"));

        return images;
    }

    private ImageDto createFallbackImage(String id, String small, String regular, String full, String description) {
        ImageDto image = new ImageDto();
        image.setId(id);
        image.setSmallUrl(small);
        image.setRegularUrl(regular);
        image.setFullUrl(full);
        image.setDescription(description);
        image.setAltDescription(description);
        image.setPhotographer("Unsplash Source");
        image.setPhotographerUrl("https://unsplash.com/");
        return image;
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) {
            return "Travel";
        }
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }
}
