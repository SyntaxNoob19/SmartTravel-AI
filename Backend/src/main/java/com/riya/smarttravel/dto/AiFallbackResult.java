package com.riya.smarttravel.dto;

import java.util.List;

public record AiFallbackResult(
    AiFallbackStatus status,
    List<PlannerDayDto> itinerary,
    String message
) {
}
