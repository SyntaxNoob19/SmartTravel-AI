package com.riya.smarttravel.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SavedTripDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String tripName;
    private String destination;
    private PlannerRequest plannerRequest;
    private PlannerResponseDto plannerResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}