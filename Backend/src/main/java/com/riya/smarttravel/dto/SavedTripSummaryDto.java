package com.riya.smarttravel.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SavedTripSummaryDto {
    private Long id;
    private String tripName;
    private String destination;
    private Integer generatedDays;
    private Integer totalPlaces;
    private String summary;
    private LocalDateTime createdAt;
    private String budget;
}