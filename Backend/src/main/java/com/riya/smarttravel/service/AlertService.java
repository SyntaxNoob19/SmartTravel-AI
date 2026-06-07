package com.riya.smarttravel.service;

import com.riya.smarttravel.dto.CrowdAlertDto;
import com.riya.smarttravel.dto.DangerZoneDto;
import com.riya.smarttravel.entity.Place;
import com.riya.smarttravel.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AlertService {

    private final PlaceRepository placeRepository;

    public AlertService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    /**
     * Get crowd alerts for places in a city
     */
    public List<CrowdAlertDto> getCrowdAlerts(String city) {
        List<CrowdAlertDto> alerts = new ArrayList<>();
        placeRepository.findByCityContainingIgnoreCase(city).forEach(place -> {
            CrowdAlertDto alert = analyzeCrowdLevel(place);
            if (alert != null) {
                alerts.add(alert);
            }
        });
        return alerts;
    }

    /**
     * Analyze crowd level based on place characteristics
     */
    public CrowdAlertDto analyzeCrowdLevel(Place place) {
        CrowdAlertDto alert = new CrowdAlertDto();
        alert.setPlaceId(place.getPlaceId());
        alert.setPlaceName(place.getPlaceName());
        alert.setTimestamp(LocalDateTime.now());

        // Normalize crowd level because source data may use values like "High"/"Medium".
        String normalizedCrowdLevel = normalizeCrowdLevel(place.getCrowdLevel());
        alert.setCrowdLevel(normalizedCrowdLevel);

        // Determine peak hours (typically 10 AM - 6 PM)
        int currentHour = LocalDateTime.now().getHour();
        boolean isTimePeak = currentHour >= 10 && currentHour <= 18;
        boolean isAlwaysBusyCategory = "HIGH".equals(normalizedCrowdLevel) || "VERY_HIGH".equals(normalizedCrowdLevel);
        alert.setIsPeakHours(isTimePeak || isAlwaysBusyCategory);

        // Calculate crowd percentage
        double crowdPercentage = switch (normalizedCrowdLevel) {
            case "LOW" -> alert.getIsPeakHours() ? 25.0 : 10.0;
            case "MODERATE" -> alert.getIsPeakHours() ? 50.0 : 30.0;
            case "HIGH" -> alert.getIsPeakHours() ? 75.0 : 60.0;
            case "VERY_HIGH" -> 90.0;
            default -> 40.0;
        };

        alert.setCrowdPercentage(crowdPercentage);
        alert.setTrend("STABLE"); // Could be enhanced with real-time data
        alert.setMessage(generateCrowdMessage(normalizedCrowdLevel, alert.getIsPeakHours()));

        return alert;
    }

    /**
     * Get danger zone alerts for places
     */
    public List<DangerZoneDto> getDangerZoneAlerts(String city) {
        List<DangerZoneDto> alerts = new ArrayList<>();
        placeRepository.findByCityContainingIgnoreCase(city).forEach(place -> {
            DangerZoneDto alert = analyzeSafetyLevel(place);
            if (alert != null) {
                alerts.add(alert);
            }
        });
        return alerts;
    }

    /**
     * Analyze safety level based on place characteristics
     */
    public DangerZoneDto analyzeSafetyLevel(Place place) {
        DangerZoneDto alert = new DangerZoneDto();
        alert.setPlaceId(place.getPlaceId());
        alert.setPlaceName(place.getPlaceName());
        alert.setTimestamp(LocalDateTime.now());

        // Get safety score (source data may be on 0-10 or 0-100 scale).
        Double safetyScore = place.getSafetyScore() != null ? place.getSafetyScore() : 85.0;
        alert.setSafetyScore(safetyScore);

        // Normalize to 0-100 so risk thresholds stay consistent.
        double normalizedSafetyScore = normalizeSafetyScore(safetyScore);
        String riskLevel;
        if (normalizedSafetyScore >= 85.0) {
            riskLevel = "LOW";
        } else if (normalizedSafetyScore >= 70.0) {
            riskLevel = "MEDIUM";
        } else if (normalizedSafetyScore >= 50.0) {
            riskLevel = "HIGH";
        } else {
            riskLevel = "CRITICAL";
        }

        alert.setRiskLevel(riskLevel);
        alert.setIsActive(riskLevel.equals("HIGH") || riskLevel.equals("CRITICAL"));
        alert.setAlertType("GENERAL_SAFETY");
        alert.setDescription(generateSafetyMessage(riskLevel, safetyScore));
        alert.setRecommendation(generateSafetyRecommendation(riskLevel));

        return alert;
    }

    /**
     * Get critical alerts for high-risk places
     */
    public List<DangerZoneDto> getCriticalAlerts() {
        List<DangerZoneDto> criticalAlerts = new ArrayList<>();
        placeRepository.findAll().forEach(place -> {
            Double safetyScore = place.getSafetyScore();
            if (safetyScore != null && safetyScore < 50) {
                DangerZoneDto alert = analyzeSafetyLevel(place);
                if (alert.getIsActive()) {
                    criticalAlerts.add(alert);
                }
            }
        });
        return criticalAlerts;
    }

    /**
     * Check if a specific place is in a danger zone
     */
    public boolean isPlaceInDangerZone(String placeId) {
        Optional<Place> place = placeRepository.findById(placeId);
        if (place.isEmpty()) {
            return false;
        }

        Double safetyScore = place.get().getSafetyScore();
        return safetyScore != null && safetyScore < 50;
    }

    /**
     * Get alerts for a range of coordinates
     */
    public List<DangerZoneDto> getAlertsInRadius(Double latitude, Double longitude, Double radiusKm) {
        List<DangerZoneDto> alerts = new ArrayList<>();
        placeRepository.findAll().forEach(place -> {
            if (place.getLatitude() != null && place.getLongitude() != null) {
                double distance = calculateDistance(latitude, longitude, 
                                                  place.getLatitude(), place.getLongitude());
                if (distance <= radiusKm) {
                    DangerZoneDto alert = analyzeSafetyLevel(place);
                    if (alert.getIsActive()) {
                        alerts.add(alert);
                    }
                }
            }
        });
        return alerts;
    }

    private String generateCrowdMessage(String crowdLevel, Boolean isPeakHours) {
        String baseMessage = switch (crowdLevel) {
            case "LOW" -> "Tourist footfall is low";
            case "MODERATE" -> "Moderate number of visitors";
            case "HIGH" -> "High tourist traffic expected";
            case "VERY_HIGH" -> "Extremely crowded, plan accordingly";
            default -> "Normal crowd levels";
        };

        if (isPeakHours) {
            baseMessage += " (Peak hours)";
        }
        return baseMessage;
    }

    private String generateSafetyMessage(String riskLevel, Double safetyScore) {
        return switch (riskLevel) {
            case "LOW" -> "This area is safe with excellent safety measures";
            case "MEDIUM" -> "Moderate safety precautions recommended";
            case "HIGH" -> "Higher risk area, exercise caution";
            case "CRITICAL" -> "Avoid this area - critical safety concerns";
            default -> "Safety information unavailable";
        };
    }

    private String generateSafetyRecommendation(String riskLevel) {
        return switch (riskLevel) {
            case "LOW" -> "Enjoy! Area is well-secured";
            case "MEDIUM" -> "Stay aware of surroundings and follow local guidelines";
            case "HIGH" -> "Visit only during daytime, travel in groups";
            case "CRITICAL" -> "Not recommended for tourists at this time";
            default -> "Check with local authorities";
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

    private double normalizeSafetyScore(Double rawScore) {
        if (rawScore == null) {
            return 85.0;
        }

        // Heuristic: values up to 10 likely represent a 0-10 scale.
        if (rawScore <= 10.0) {
            return rawScore * 10.0;
        }
        return rawScore;
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }
}
