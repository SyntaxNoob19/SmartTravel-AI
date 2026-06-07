package com.riya.smarttravel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlannerRequest {
    private Integer days;
    private String travellerType;
    private Integer groupSize;
    private Boolean enhanceWithAi;
    private String city;
    private String preferences;
    private String region;
    private String category;
    private String mood;
    private String budgetLevel;
    private String crowdLevel;
    private Boolean familyFriendly;
    private String priority;
    private String season;
    private String weatherType;
    private Double minRating;
    private Double maxHoursPerDay;
}
