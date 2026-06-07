package com.riya.smarttravel.dto;

import lombok.Data;

@Data
public class SavedTripCreateRequest {
    private Long userId;
    private String userEmail;
    private String tripName;
    private String destination;
    private PlannerRequest plannerRequest;
    private PlannerResponseDto plannerResponse;
}