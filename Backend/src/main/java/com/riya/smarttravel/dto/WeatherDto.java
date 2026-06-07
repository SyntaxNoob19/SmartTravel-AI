package com.riya.smarttravel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDto implements Serializable {
    private Double temperature;
    private String condition;
    private Double humidity;
    private Double windSpeed;
    private Double precipitationProbability;
    private String time;
    
    @JsonProperty("weather_code")
    private Integer weatherCode;
    
    @JsonProperty("relative_humidity_2m")
    private Double relativeHumidity;
    
    @JsonProperty("wind_speed_10m")
    private Double windSpeed10m;
    
    @JsonProperty("precipitation_probability")
    private Double precipitationProb;
}
