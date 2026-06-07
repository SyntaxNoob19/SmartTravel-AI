package com.riya.smarttravel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto implements Serializable {
    private String id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Double latitude;
    private Double longitude;
    private String eventType; // FESTIVAL, CONCERT, SPORTS, CULTURAL, etc.
    private String category;
    private String url;
    private String imageUrl;
    private Boolean isFree;
    private String priceRange;
    
    @JsonProperty("event_url")
    private String eventUrl;
    
    @JsonProperty("city")
    private String city;
}
