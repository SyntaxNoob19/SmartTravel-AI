
package com.riya.smarttravel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CitySummaryDto {
    private String city;
    private String state;
    private String region;
    private Long placeCount;
}
