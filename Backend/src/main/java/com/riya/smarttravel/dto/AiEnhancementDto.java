package com.riya.smarttravel.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AiEnhancementDto {
    private String aiSummary;
    private List<String> tips;
    private List<AdditionalRecommendationDto> additionalRecommendations;
    private String aboutPlace;
    private List<String> whyChoosePlace;
}