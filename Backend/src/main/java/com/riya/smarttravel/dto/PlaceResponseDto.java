package com.riya.smarttravel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponseDto {
    private String placeId;
    private String placeName;
    private String city;
    private String state;
    private String region;
    private String placeType;
    private String category;
    private String moodTags;
    private String significance;
    private String description;
    private String bestTimeToVisit;
    private String idealVisitTime;
    private Double recommendedDurationHours;
    private Double entryFee;
    private Double rating;
    private String crowdLevel;
    private Boolean familyFriendly;
    private String adventureLevel;
    private String culturalValue;
    private String natureValue;
    private String budgetLevel;
    private Double latitude;
    private Double longitude;
    private String nearestAirport;
    private String nearestRailway;
    private String localTips;
    private String foodSpecialty;
    private Double safetyScore;
    private Double cleanlinessScore;
    private String photographySpots;
    private String weatherType;
    private String seasonalHighlight;
    private String priority;
}
