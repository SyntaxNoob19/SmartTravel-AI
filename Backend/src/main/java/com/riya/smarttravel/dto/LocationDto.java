package com.riya.smarttravel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto implements Serializable {
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String state;
    private String country;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("place_name")
    private String placeName;
    
    private String type;
}
