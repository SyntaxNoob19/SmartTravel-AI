package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.ApiResponse;
import com.riya.smarttravel.dto.PlaceResponseDto;
import com.riya.smarttravel.service.PlaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/explore")
public class ExploreController {

    private final PlaceService service;

    public ExploreController(PlaceService service) {
        this.service = service;
    }

    @GetMapping("/city/{city}")
    public ApiResponse<List<PlaceResponseDto>> city(@PathVariable String city) {
        List<PlaceResponseDto> data = service.getPlacesByCity(city);
        return buildResponse("Places fetched by city", data);
    }

    @GetMapping("/search")
    public ApiResponse<List<PlaceResponseDto>> search(@RequestParam String query) {
        List<PlaceResponseDto> data = service.search(query);
        return buildResponse("Search results fetched successfully", data);
    }

    @GetMapping("/filter")
    public ApiResponse<List<PlaceResponseDto>> filter(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String mood,
            @RequestParam(required = false) String budgetLevel,
            @RequestParam(required = false) String crowdLevel,
            @RequestParam(required = false) Boolean familyFriendly,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) String weatherType,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        List<PlaceResponseDto> data = service.smartFilter(
                region,
                category,
                mood,
                budgetLevel,
                crowdLevel,
                familyFriendly,
                priority,
                season,
                weatherType,
                minRating,
                sortBy,
                sortDir
        );
        return buildResponse("Filtered places fetched successfully", data);
    }

    private ApiResponse<List<PlaceResponseDto>> buildResponse(String message, List<PlaceResponseDto> data) {
        return ApiResponse.<List<PlaceResponseDto>>builder()
                .success(true)
                .message(message)
                .count(data.size())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
