package com.riya.smarttravel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DangerZoneDto implements Serializable {
    private String placeId;
    private String placeName;
    private Double safetyScore; // 0-100
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private String alertType; // WEATHER, POLLUTION, CRIME, ACCIDENT, etc.
    private String description;
    private LocalDateTime timestamp;
    private String recommendation;
    private Boolean isActive;
}
