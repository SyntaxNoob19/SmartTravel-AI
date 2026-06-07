package com.riya.smarttravel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlannerResponseDto {
    private Integer requestedDays;
    private Integer generatedDays;
    private Integer totalPlaces;
    private String travellerType;
    private String dataSource;
    private Double maxHoursPerDay;
    private Double totalTripHours;
    private String summary;
    private String budgetAdvice;
    private String generalSafetyTips;
    private String aiSummary;
    private List<String> tips;
    private List<AdditionalRecommendationDto> additionalRecommendations;
    private String aboutPlace;
    private List<String> whyChoosePlace;
    private List<PlannerDayDto> itinerary;
}
