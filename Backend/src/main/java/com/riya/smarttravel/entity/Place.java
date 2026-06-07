package com.riya.smarttravel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "places")
@Data
public class Place {

    @Id
    @Column(name = "place_id")
    private String placeId;

    @Column(name = "place_name")
    private String placeName;

    private String city;
    private String state;
    private String region;

    @Column(name = "place_type")
    private String placeType;

    private String category;

    @Column(name = "mood_tags")
    private String moodTags;

    private String significance;

    private String description;

    @Column(name = "best_time_to_visit")
    private String bestTimeToVisit;

    @Column(name = "ideal_visit_time")
    private String idealVisitTime;

    @Column(name = "recommended_duration_hours")
    private Double recommendedDurationHours;

    @Column(name = "entry_fee")
    private Double entryFee;

    private Double rating;

    @Column(name = "crowd_level")
    private String crowdLevel;

    @Column(name = "family_friendly")
    private Boolean familyFriendly;

    @Column(name = "adventure_level")
    private String adventureLevel;

    @Column(name = "cultural_value")
    private String culturalValue;

    @Column(name = "nature_value")
    private String natureValue;

    @Column(name = "budget_level")
    private String budgetLevel;

    private Double latitude;

    private Double longitude;

    @Column(name = "nearest_airport")
    private String nearestAirport;

    @Column(name = "nearest_railway")
    private String nearestRailway;

    @Column(name = "local_tips")
    private String localTips;

    @Column(name = "food_specialty")
    private String foodSpecialty;

    @Column(name = "safety_score")
    private Double safetyScore;

    @Column(name = "cleanliness_score")
    private Double cleanlinessScore;

    @Column(name = "photography_spots")
    private String photographySpots;

    @Column(name = "weather_type")
    private String weatherType;

    @Column(name = "seasonal_highlight")
    private String seasonalHighlight;

    private String priority;
}
