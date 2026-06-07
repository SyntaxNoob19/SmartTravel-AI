package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.*;
import com.riya.smarttravel.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/external")
public class ExternalApisController {

    private final WeatherService weatherService;
    private final HolidayService holidayService;
    private final GeocodeService geocodeService;
    private final ImageService imageService;
    private final AlertService alertService;

    public ExternalApisController(WeatherService weatherService,
                                  HolidayService holidayService,
                                  GeocodeService geocodeService,
                                  ImageService imageService,
                                  AlertService alertService) {
        this.weatherService = weatherService;
        this.holidayService = holidayService;
        this.geocodeService = geocodeService;
        this.imageService = imageService;
        this.alertService = alertService;
    }

    // =========================== WEATHER ENDPOINTS ===========================

    /**
     * Get current weather for a location
     */
    @GetMapping("/weather/current")
    public ResponseEntity<WeatherDto> getCurrentWeather(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        log.info("Fetching current weather for lat={}, lon={}", latitude, longitude);
        WeatherDto weather = weatherService.getCurrentWeather(latitude, longitude);
        return ResponseEntity.ok(weather);
    }

    /**
     * Get 7-day forecast for a location
     */
    @GetMapping("/weather/forecast")
    public ResponseEntity<List<WeatherDto>> getForecast(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        log.info("Fetching forecast for lat={}, lon={}", latitude, longitude);
        List<WeatherDto> forecast = weatherService.getForecast(latitude, longitude);
        return ResponseEntity.ok(forecast);
    }

    /**
     * Get weather for a place by place name
     */
    @GetMapping("/weather/place/{placeName}")
    public ResponseEntity<Map<String, Object>> getWeatherForPlace(
            @PathVariable String placeName) {
        log.info("Fetching weather for place: {}", placeName);
        LocationDto location = geocodeService.forwardGeocode(placeName);
        
        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        WeatherDto weather = weatherService.getCurrentWeather(location.getLatitude(), location.getLongitude());
        List<WeatherDto> forecast = weatherService.getForecast(location.getLatitude(), location.getLongitude());

        Map<String, Object> response = new HashMap<>();
        response.put("place", placeName);
        response.put("location", location);
        response.put("current", weather);
        response.put("forecast", forecast);

        return ResponseEntity.ok(response);
    }

    // =========================== HOLIDAY ENDPOINTS ===========================

    /**
     * Get holidays for a specific year and country
     */
    @GetMapping("/holidays/{year}/{countryCode}")
    public ResponseEntity<List<HolidayDto>> getHolidays(
            @PathVariable int year,
            @PathVariable String countryCode) {
        log.info("Fetching holidays for year={}, country={}", year, countryCode);
        List<HolidayDto> holidays = holidayService.getHolidaysByYear(year, countryCode);
        return ResponseEntity.ok(holidays);
    }

    /**
     * Get long weekends for a specific month and country
     */
    @GetMapping("/holidays/long-weekends/{year}/{month}/{countryCode}")
    public ResponseEntity<List<HolidayDto>> getLongWeekends(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable String countryCode) {
        log.info("Fetching long weekends for year={}, month={}, country={}", year, month, countryCode);
        List<HolidayDto> longWeekends = holidayService.getLongWeekends(year, month, countryCode);
        return ResponseEntity.ok(longWeekends);
    }

    /**
     * Get upcoming holidays globally
     */
    @GetMapping("/holidays/upcoming")
    public ResponseEntity<List<HolidayDto>> getUpcomingHolidays() {
        log.info("Fetching upcoming holidays");
        List<HolidayDto> holidays = holidayService.getUpcomingHolidays();
        return ResponseEntity.ok(holidays);
    }

    /**
     * Get holidays for current year
     */
    @GetMapping("/holidays/current/{countryCode}")
    public ResponseEntity<List<HolidayDto>> getCurrentYearHolidays(
            @PathVariable String countryCode) {
        log.info("Fetching current year holidays for country={}", countryCode);
        List<HolidayDto> holidays = holidayService.getHolidaysForCurrentYear(countryCode);
        return ResponseEntity.ok(holidays);
    }

    // =========================== GEOCODING ENDPOINTS ===========================

    /**
     * Forward geocoding: Convert address to coordinates
     */
    @GetMapping("/geocode/forward")
    public ResponseEntity<LocationDto> forwardGeocode(
            @RequestParam String address) {
        log.info("Forward geocoding for address: {}", address);
        LocationDto location = geocodeService.forwardGeocode(address);
        return location != null ? ResponseEntity.ok(location) : ResponseEntity.notFound().build();
    }

    /**
     * Reverse geocoding: Convert coordinates to address
     */
    @GetMapping("/geocode/reverse")
    public ResponseEntity<LocationDto> reverseGeocode(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        log.info("Reverse geocoding for lat={}, lon={}", latitude, longitude);
        LocationDto location = geocodeService.reverseGeocode(latitude, longitude);
        return location != null ? ResponseEntity.ok(location) : ResponseEntity.notFound().build();
    }

    /**
     * Search for places
     */
    @GetMapping("/geocode/search")
    public ResponseEntity<List<LocationDto>> searchPlaces(
            @RequestParam String query) {
        log.info("Searching places for query: {}", query);
        List<LocationDto> locations = geocodeService.searchPlaces(query);
        return ResponseEntity.ok(locations);
    }

    // =========================== IMAGE ENDPOINTS ===========================

    /**
     * Get travel images for a query
     */
    @GetMapping("/images/search")
    public ResponseEntity<List<ImageDto>> searchImages(
            @RequestParam String query) {
        log.info("Searching images for query: {}", query);
        List<ImageDto> images = imageService.searchImages(query);
        return ResponseEntity.ok(images);
    }

    /**
     * Get images for a city
     */
    @GetMapping("/images/city/{city}")
    public ResponseEntity<List<ImageDto>> getImagesByCity(
            @PathVariable String city) {
        log.info("Fetching images for city: {}", city);
        List<ImageDto> images = imageService.getImagesByCity(city);
        return ResponseEntity.ok(images);
    }

    /**
     * Get images for a place/landmark
     */
    @GetMapping("/images/place/{placeName}")
    public ResponseEntity<List<ImageDto>> getImagesByPlace(
            @PathVariable String placeName) {
        log.info("Fetching images for place: {}", placeName);
        List<ImageDto> images = imageService.getImagesByPlace(placeName);
        return ResponseEntity.ok(images);
    }

    /**
     * Get random images for a query
     */
    @GetMapping("/images/random")
    public ResponseEntity<List<ImageDto>> getRandomImages(
            @RequestParam String query) {
        log.info("Fetching random images for query: {}", query);
        List<ImageDto> images = imageService.getRandomImages(query);
        return ResponseEntity.ok(images);
    }

    // =========================== ALERT ENDPOINTS ===========================

    /**
     * Get crowd alerts for a city
     */
    @GetMapping("/alerts/crowd/{city}")
    public ResponseEntity<List<CrowdAlertDto>> getCrowdAlerts(
            @PathVariable String city) {
        log.info("Fetching crowd alerts for city: {}", city);
        List<CrowdAlertDto> alerts = alertService.getCrowdAlerts(city);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get danger zone alerts for a city
     */
    @GetMapping("/alerts/danger/{city}")
    public ResponseEntity<List<DangerZoneDto>> getDangerZoneAlerts(
            @PathVariable String city) {
        log.info("Fetching danger zone alerts for city: {}", city);
        List<DangerZoneDto> alerts = alertService.getDangerZoneAlerts(city);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get critical safety alerts
     */
    @GetMapping("/alerts/critical")
    public ResponseEntity<List<DangerZoneDto>> getCriticalAlerts() {
        log.info("Fetching critical alerts");
        List<DangerZoneDto> alerts = alertService.getCriticalAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * Check if a place is in danger zone
     */
    @GetMapping("/alerts/danger/check/{placeId}")
    public ResponseEntity<Map<String, Boolean>> isPlaceInDangerZone(
            @PathVariable String placeId) {
        log.info("Checking danger zone status for place: {}", placeId);
        boolean isDanger = alertService.isPlaceInDangerZone(placeId);
        return ResponseEntity.ok(Map.of("isInDangerZone", isDanger));
    }

    /**
     * Get alerts within a radius
     */
    @GetMapping("/alerts/radius")
    public ResponseEntity<List<DangerZoneDto>> getAlertsInRadius(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double radiusKm) {
        log.info("Fetching alerts within {}km from lat={}, lon={}", radiusKm, latitude, longitude);
        List<DangerZoneDto> alerts = alertService.getAlertsInRadius(latitude, longitude, radiusKm);
        return ResponseEntity.ok(alerts);
    }

    // =========================== COMBINED ENDPOINTS ===========================

    /**
     * Get comprehensive travel information for a city
     */
    @GetMapping("/place-info/{placeName}")
    public ResponseEntity<Map<String, Object>> getPlaceInfo(
            @PathVariable String placeName) {
        log.info("Fetching comprehensive info for place: {}", placeName);
        
        LocationDto location = geocodeService.forwardGeocode(placeName);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> info = new HashMap<>();
        info.put("location", location);
        info.put("weather", weatherService.getCurrentWeather(location.getLatitude(), location.getLongitude()));
        info.put("forecast", weatherService.getForecast(location.getLatitude(), location.getLongitude()));
        info.put("images", imageService.getImagesByPlace(placeName));
        
        // Add danger alerts if city is available
        if (location.getCity() != null) {
            info.put("dangerAlerts", alertService.getDangerZoneAlerts(location.getCity()));
            info.put("crowdAlerts", alertService.getCrowdAlerts(location.getCity()));
        }

        return ResponseEntity.ok(info);
    }

    /**
     * Get trip planning information for a city with holidays
     */
    @GetMapping("/trip-planner/{city}/{countryCode}")
    public ResponseEntity<Map<String, Object>> getTripPlannerInfo(
            @PathVariable String city,
            @PathVariable String countryCode,
            @RequestParam(defaultValue = "12") int monthCount) {
        log.info("Fetching trip planner info for city={}, country={}", city, countryCode);
        
        LocationDto location = geocodeService.forwardGeocode(city);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        WeatherDto currentWeather = weatherService.getCurrentWeather(location.getLatitude(), location.getLongitude());
        List<WeatherDto> forecast = weatherService.getForecast(location.getLatitude(), location.getLongitude());

        Map<String, Object> tripInfo = new HashMap<>();
        tripInfo.put("location", location);
        tripInfo.put("weather", currentWeather);
        tripInfo.put("forecast", forecast);
        tripInfo.put("images", imageService.getImagesByCity(city));
        
        // Get holidays for current and next year
        int currentYear = LocalDate.now().getYear();
        List<HolidayDto> holidaysCurrent = holidayService.getHolidaysByYear(currentYear, countryCode);
        List<HolidayDto> holidaysNext = holidayService.getHolidaysByYear(currentYear + 1, countryCode);
        tripInfo.put("holidaysCurrent", holidaysCurrent);
        tripInfo.put("holidaysNext", holidaysNext);
        
        // Get long weekends for the next 3 months
        List<HolidayDto> longWeekends = new java.util.ArrayList<>();
        LocalDate base = LocalDate.now();
        for (int i = 0; i < 3; i++) {
            LocalDate d = base.plusMonths(i);
            longWeekends.addAll(holidayService.getLongWeekends(d.getYear(), d.getMonthValue(), countryCode));
        }
        tripInfo.put("longWeekends", longWeekends);
        
        // Get safety information
        List<DangerZoneDto> safetyAlerts = alertService.getDangerZoneAlerts(city);
        List<CrowdAlertDto> crowdLevels = alertService.getCrowdAlerts(city);
        tripInfo.put("safetyAlerts", safetyAlerts);
        tripInfo.put("crowdLevels", crowdLevels);
        tripInfo.put("bestTimeToVisit", buildBestTimeToVisitRecommendation(currentWeather, forecast, crowdLevels));
        tripInfo.put("aiSummary", buildAiSummary(city, currentWeather, safetyAlerts, holidaysCurrent, longWeekends, crowdLevels));

        return ResponseEntity.ok(tripInfo);
    }

    private Map<String, Object> buildBestTimeToVisitRecommendation(WeatherDto currentWeather,
                                                                   List<WeatherDto> forecast,
                                                                   List<CrowdAlertDto> crowdLevels) {
        Map<String, Object> recommendation = new HashMap<>();
        String crowdWindow = deriveCrowdWindow(crowdLevels);
        WeatherDto bestForecastDay = pickBestForecastDay(forecast);

        String weatherWindow = deriveWeatherWindow(currentWeather, bestForecastDay);
        String avoidWindow = deriveAvoidWindow(currentWeather, bestForecastDay);

        String bestDayText = "the next clear day";
        if (bestForecastDay != null && bestForecastDay.getTime() != null) {
            bestDayText = bestForecastDay.getTime();
        }

        String dayPreference = "prefer weekdays";
        if (currentWeather != null
                && currentWeather.getTemperature() != null
                && currentWeather.getTemperature() >= 36.0) {
            dayPreference = "prefer weekdays and split outings between morning and evening";
        }
        if (currentWeather != null
                && currentWeather.getPrecipitationProbability() != null
                && currentWeather.getPrecipitationProbability() >= 55.0) {
            dayPreference = "prefer weekdays with flexible indoor backups";
        }

        String primary = String.format(
                "%s is best visited on %s between %s. %s and avoid %s. Crowd advice: %s.",
                "This city",
                bestDayText,
                weatherWindow,
                dayPreference,
                avoidWindow,
                crowdWindow
        );

        recommendation.put("crowdWindow", crowdWindow);
        recommendation.put("recommendation", primary);
        recommendation.put("bestForecastDay", bestDayText);
        recommendation.put("weatherWindow", weatherWindow);
        recommendation.put("avoidWindow", avoidWindow);
        return recommendation;
    }

    private String buildAiSummary(String city,
                                  WeatherDto currentWeather,
                                  List<DangerZoneDto> safetyAlerts,
                                  List<HolidayDto> holidaysCurrent,
                                  List<HolidayDto> longWeekends,
                                  List<CrowdAlertDto> crowdLevels) {
        String condition = currentWeather != null && currentWeather.getCondition() != null
                ? currentWeather.getCondition().toLowerCase(Locale.ROOT)
                : "stable";
        String temp = currentWeather != null && currentWeather.getTemperature() != null
                ? String.format(Locale.ROOT, "%.1fC", currentWeather.getTemperature())
                : "N/A";

        long highRiskCount = safetyAlerts == null ? 0 : safetyAlerts.stream()
                .filter(alert -> alert.getRiskLevel() != null)
                .map(alert -> alert.getRiskLevel().toUpperCase(Locale.ROOT))
                .filter(level -> level.equals("HIGH") || level.equals("CRITICAL"))
                .count();

        long highCrowdCount = crowdLevels == null ? 0 : crowdLevels.stream()
                .filter(alert -> alert.getCrowdLevel() != null)
                .map(alert -> alert.getCrowdLevel().toUpperCase(Locale.ROOT))
                .filter(level -> level.equals("HIGH") || level.equals("VERY_HIGH"))
                .count();

        String nextHolidayText = "no major holiday listed soon";
        if (holidaysCurrent != null) {
            LocalDate today = LocalDate.now();
            HolidayDto nextHoliday = holidaysCurrent.stream()
                    .filter(holiday -> holiday.getDate() != null && !holiday.getDate().isBefore(today))
                    .findFirst()
                    .orElse(null);
            if (nextHoliday != null) {
                nextHolidayText = nextHoliday.getName() + " on " + nextHoliday.getDate();
            }
        }

        String longWeekendText = longWeekends == null || longWeekends.isEmpty()
                ? "No immediate long weekend in this month"
                : "Long weekend opportunities are available this month";

        String safetyText = highRiskCount > 0
                ? "Some attractions need extra caution"
                : "Current safety signals look favorable";
        String crowdText = highCrowdCount >= 3
                ? "Expect heavier tourist traffic at top spots"
                : "Crowd pressure appears manageable";

        return String.format(
                "%s update: Weather is %s at %s. %s. %s. Next holiday marker: %s. %s.",
                city,
                condition,
                temp,
                safetyText,
                crowdText,
                nextHolidayText,
                longWeekendText
        );
    }

    private WeatherDto pickBestForecastDay(List<WeatherDto> forecast) {
        if (forecast == null || forecast.isEmpty()) {
            return null;
        }

        WeatherDto best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (WeatherDto day : forecast) {
            double score = 0.0;
            Double temp = day.getTemperature();
            Double rain = day.getPrecipitationProbability();
            String condition = day.getCondition() == null ? "" : day.getCondition().toLowerCase(Locale.ROOT);

            if (temp != null) {
                score += (temp >= 20.0 && temp <= 32.0) ? 2.0 : (temp <= 36.0 ? 1.0 : -1.0);
            }
            if (rain != null) {
                score += rain <= 20.0 ? 2.0 : (rain <= 40.0 ? 1.0 : -1.0);
            }
            if (condition.contains("clear") || condition.contains("partly")) {
                score += 1.0;
            } else if (condition.contains("rain") || condition.contains("storm")) {
                score -= 1.0;
            }

            if (score > bestScore) {
                bestScore = score;
                best = day;
            }
        }
        return best;
    }

    private String deriveCrowdWindow(List<CrowdAlertDto> crowdLevels) {
        if (crowdLevels == null || crowdLevels.isEmpty()) {
            return "target weekday mornings for smoother visits";
        }

        long highOrVeryHigh = crowdLevels.stream()
                .filter(alert -> alert.getCrowdLevel() != null)
                .map(alert -> alert.getCrowdLevel().toUpperCase(Locale.ROOT))
                .filter(level -> level.equals("HIGH") || level.equals("VERY_HIGH"))
                .count();

        long lowOrModerate = crowdLevels.stream()
                .filter(alert -> alert.getCrowdLevel() != null)
                .map(alert -> alert.getCrowdLevel().toUpperCase(Locale.ROOT))
                .filter(level -> level.equals("LOW") || level.equals("MODERATE"))
                .count();

        int total = crowdLevels.size();
        double highRatio = total == 0 ? 0.0 : (double) highOrVeryHigh / total;
        double lowRatio = total == 0 ? 0.0 : (double) lowOrModerate / total;

        if (highRatio >= 0.60) {
            return "6:30 AM to 9:30 AM on weekdays to avoid peak queues";
        }
        if (lowRatio >= 0.60) {
            return "9:30 AM to 12:30 PM is comfortable across most weekdays";
        }
        return "10:00 AM to 1:00 PM works well on weekdays with moderate traffic";
    }

    private String deriveWeatherWindow(WeatherDto currentWeather, WeatherDto bestForecastDay) {
        Double temp = null;
        Double rain = null;

        if (bestForecastDay != null) {
            temp = bestForecastDay.getTemperature();
            rain = bestForecastDay.getPrecipitationProbability();
        }

        if (temp == null && currentWeather != null) {
            temp = currentWeather.getTemperature();
        }
        if (rain == null && currentWeather != null) {
            rain = currentWeather.getPrecipitationProbability();
        }

        if (temp != null && temp >= 36.0) {
            return "6:00 AM to 9:00 AM and 6:00 PM to 8:00 PM";
        }
        if (temp != null && temp <= 14.0) {
            return "10:30 AM to 3:30 PM";
        }
        if (rain != null && rain >= 60.0) {
            return "8:00 AM to 11:00 AM with weather buffers";
        }
        if (rain != null && rain >= 35.0) {
            return "8:30 AM to 11:30 AM";
        }
        return "8:00 AM to 11:00 AM";
    }

    private String deriveAvoidWindow(WeatherDto currentWeather, WeatherDto bestForecastDay) {
        Double temp = null;
        Double rain = null;

        if (bestForecastDay != null) {
            temp = bestForecastDay.getTemperature();
            rain = bestForecastDay.getPrecipitationProbability();
        }

        if (temp == null && currentWeather != null) {
            temp = currentWeather.getTemperature();
        }
        if (rain == null && currentWeather != null) {
            rain = currentWeather.getPrecipitationProbability();
        }

        if (temp != null && temp >= 36.0) {
            return "12:00 PM to 4:30 PM";
        }
        if (temp != null && temp <= 12.0) {
            return "before 8:00 AM and after 8:30 PM";
        }
        if (rain != null && rain >= 60.0) {
            return "late afternoon when showers are more disruptive";
        }
        return "peak late-afternoon hours around 2:00 PM to 5:00 PM";
    }
}
