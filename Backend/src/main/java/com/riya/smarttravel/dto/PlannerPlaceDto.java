package com.riya.smarttravel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannerPlaceDto {
    private String placeId;
    private String placeName;
    private String category;
    private String significance;
    private String description;
    private String localTips;
    private String safetyAdvice;
    private String travelNote;
    private Double entryFee;
    private Double rating;
    private Double recommendedDurationHours;
    private String bestTimeToVisit;
    private String idealVisitTime;
    private String plannedVisitTimeSlot;
    private String imageUrl;
}
