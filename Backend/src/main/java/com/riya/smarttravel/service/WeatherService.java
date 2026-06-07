package com.riya.smarttravel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.smarttravel.dto.WeatherDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WeatherService {

    private static final String OPEN_METEO_API = "https://api.open-meteo.com/v1/forecast";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get current weather and forecast for a location
     */
    @Cacheable(value = "weather", key = "#lat + '_' + #lon")
    public WeatherDto getCurrentWeather(Double lat, Double lon) {
        try {
            String url = String.format(
                "%s?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,precipitation_probability&timezone=auto",
                OPEN_METEO_API, lat, lon
            );

            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Failed to fetch weather: {}", response.code());
                return null;
            }

            JsonNode data = objectMapper.readTree(response.body().string());
            JsonNode current = data.get("current");

            WeatherDto weather = new WeatherDto();
            weather.setTemperature(current.get("temperature_2m").asDouble());
            weather.setRelativeHumidity(current.get("relative_humidity_2m").asDouble());
            weather.setWindSpeed10m(current.get("wind_speed_10m").asDouble());
            weather.setPrecipitationProb(current.get("precipitation_probability").asDouble());
            weather.setWeatherCode(current.get("weather_code").asInt());
            weather.setCondition(getWeatherCondition(current.get("weather_code").asInt()));
            weather.setTime(current.get("time").asText());

            return weather;

        } catch (Exception e) {
            log.error("Error fetching weather from Open-Meteo", e);
            return null;
        }
    }

    /**
     * Get 7-day forecast for a location
     */
    @Cacheable(value = "forecast", key = "#lat + '_' + #lon")
    public List<WeatherDto> getForecast(Double lat, Double lon) {
        try {
            String url = String.format(
                "%s?latitude=%f&longitude=%f&daily=temperature_2m_max,temperature_2m_min,weather_code,precipitation_probability_max&timezone=auto",
                OPEN_METEO_API, lat, lon
            );

            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Failed to fetch forecast: {}", response.code());
                return new ArrayList<>();
            }

            JsonNode data = objectMapper.readTree(response.body().string());
            JsonNode daily = data.get("daily");
            JsonNode dates = daily.get("time");
            JsonNode temps = daily.get("temperature_2m_max");
            JsonNode codes = daily.get("weather_code");
            JsonNode precip = daily.get("precipitation_probability_max");

            List<WeatherDto> forecast = new ArrayList<>();
            for (int i = 0; i < dates.size() && i < 7; i++) {
                WeatherDto weather = new WeatherDto();
                weather.setTime(dates.get(i).asText());
                weather.setTemperature(temps.get(i).asDouble());
                weather.setWeatherCode(codes.get(i).asInt());
                weather.setCondition(getWeatherCondition(codes.get(i).asInt()));
                weather.setPrecipitationProb(precip.get(i).asDouble());
                forecast.add(weather);
            }

            return forecast;

        } catch (Exception e) {
            log.error("Error fetching forecast from Open-Meteo", e);
            return new ArrayList<>();
        }
    }

    private String getWeatherCondition(int weatherCode) {
        return switch (weatherCode) {
            case 0 -> "Clear sky";
            case 1, 2 -> "Mainly clear";
            case 3 -> "Overcast";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            case 77 -> "Snow grains";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Unknown";
        };
    }
}
