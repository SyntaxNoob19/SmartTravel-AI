package com.riya.smarttravel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrowdAlertDto implements Serializable {
    private String placeId;
    private String placeName;
    private String crowdLevel; // LOW, MODERATE, HIGH, VERY_HIGH
    private Double crowdPercentage;
    private LocalDateTime timestamp;
    private String trend; // INCREASING, STABLE, DECREASING
    private String message;
    private Boolean isPeakHours;
}
