package com.riya.smarttravel.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserProfileDto {
    private AuthUserDto user;
    private List<SavedTripSummaryDto> trips;
}
