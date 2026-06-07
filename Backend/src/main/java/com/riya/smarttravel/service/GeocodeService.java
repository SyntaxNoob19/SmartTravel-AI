package com.riya.smarttravel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.smarttravel.dto.LocationDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GeocodeService {

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Forward geocoding: Convert address to lat/lon
     */
    @Cacheable(value = "geocode", key = "#address")
    public LocationDto forwardGeocode(String address) {
        try {
            String encodedAddress = address.replace(" ", "+");
            String url = String.format(
                "%s/search?q=%s&format=json&limit=1",
                NOMINATIM_API, encodedAddress
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "SmartTravel-App")
                    .build();

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                log.error("Failed to forward geocode: {}", response.code());
                return null;
            }

            JsonNode data = objectMapper.readTree(response.body().string());
            if (!data.isArray() || data.size() == 0) {
                log.warn("No results found for address: {}", address);
                return null;
            }

            JsonNode result = data.get(0);
            LocationDto location = new LocationDto();
            location.setLatitude(result.get("lat").asDouble());
            location.setLongitude(result.get("lon").asDouble());
            location.setDisplayName(result.get("display_name").asText());
            location.setAddress(address);

            JsonNode address_node = result.get("address");
            if (address_node != null) {
                location.setCity(address_node.get("city").asText());
                location.setState(address_node.get("state").asText());
                location.setCountry(address_node.get("country").asText());
            }

            return location;

        } catch (Exception e) {
            log.error("Error in forward geocoding for address: {}", address, e);
            return null;
        }
    }

    /**
     * Reverse geocoding: Convert lat/lon to address
     */
    @Cacheable(value = "reverse-geocode", key = "#lat + '_' + #lon")
    public LocationDto reverseGeocode(Double lat, Double lon) {
        try {
            String url = String.format(
                "%s/reverse?format=json&lat=%f&lon=%f",
                NOMINATIM_API, lat, lon
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "SmartTravel-App")
                    .build();

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                log.error("Failed to reverse geocode: {}", response.code());
                return null;
            }

            JsonNode data = objectMapper.readTree(response.body().string());

            LocationDto location = new LocationDto();
            location.setLatitude(lat);
            location.setLongitude(lon);
            location.setDisplayName(data.get("display_name").asText());

            JsonNode address = data.get("address");
            if (address != null) {
                location.setCity(address.get("city").asText());
                location.setState(address.get("state").asText());
                location.setCountry(address.get("country").asText());
                location.setAddress(data.get("display_name").asText());
            }

            return location;

        } catch (Exception e) {
            log.error("Error in reverse geocoding for lat={}, lon={}", lat, lon, e);
            return null;
        }
    }

    /**
     * Search for places by name
     */
    @Cacheable(value = "place-search", key = "#query")
    public List<LocationDto> searchPlaces(String query) {
        try {
            String encodedQuery = query.replace(" ", "+");
            String url = String.format(
                "%s/search?q=%s&format=json&limit=10",
                NOMINATIM_API, encodedQuery
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "SmartTravel-App")
                    .build();

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                log.error("Failed to search places: {}", response.code());
                return new ArrayList<>();
            }

            JsonNode data = objectMapper.readTree(response.body().string());
            List<LocationDto> locations = new ArrayList<>();

            if (data.isArray()) {
                for (JsonNode node : data) {
                    LocationDto location = new LocationDto();
                    location.setLatitude(node.get("lat").asDouble());
                    location.setLongitude(node.get("lon").asDouble());
                    location.setDisplayName(node.get("display_name").asText());
                    location.setPlaceName(node.get("name").asText());
                    location.setType(node.get("type").asText());

                    JsonNode addressNode = node.get("address");
                    if (addressNode != null) {
                        if (addressNode.has("city")) {
                            location.setCity(addressNode.get("city").asText());
                        }
                        if (addressNode.has("state")) {
                            location.setState(addressNode.get("state").asText());
                        }
                        if (addressNode.has("country")) {
                            location.setCountry(addressNode.get("country").asText());
                        }
                    }

                    locations.add(location);
                }
            }

            return locations;

        } catch (Exception e) {
            log.error("Error searching places for query: {}", query, e);
            return new ArrayList<>();
        }
    }
}
