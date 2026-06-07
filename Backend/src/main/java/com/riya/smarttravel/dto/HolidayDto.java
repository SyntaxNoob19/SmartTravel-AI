package com.riya.smarttravel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDto implements Serializable {
    private LocalDate date;
    private String name;
    private String type;
    private Boolean isLongWeekend;
    
    @JsonProperty("country_code")
    private String countryCode;
    
    private Integer daysOffFromTo;
}
