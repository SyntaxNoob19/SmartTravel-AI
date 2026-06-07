package com.riya.smarttravel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto implements Serializable {
    private String id;
    
    @JsonProperty("small")
    private String smallUrl;
    
    @JsonProperty("regular")
    private String regularUrl;
    
    @JsonProperty("full")
    private String fullUrl;
    
    private String description;
    private String altDescription;
    
    @JsonProperty("photographer")
    private String photographer;
    
    @JsonProperty("photographer_url")
    private String photographerUrl;
    
    @JsonProperty("urls")
    private UrlsDto urls;
    
    @JsonProperty("user")
    private UserDto user;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UrlsDto {
        private String small;
        private String regular;
        private String full;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private String name;
        private String username;
    }
}
