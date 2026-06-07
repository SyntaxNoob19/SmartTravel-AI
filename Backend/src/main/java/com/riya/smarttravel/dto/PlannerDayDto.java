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
@Builder
public class PlannerDayDto {
    private int dayNumber;
    private PlannerLocationDto location;
    private Double totalPlannedHours;
    private Double estimatedTravelHours;
    private String daySummary;
    private String travelNotes;
    private List<PlannerPlaceDto> places;
}
