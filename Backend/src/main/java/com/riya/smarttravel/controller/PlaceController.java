package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.ApiResponse;
import com.riya.smarttravel.dto.PlaceResponseDto;
import com.riya.smarttravel.service.PlaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    private final PlaceService service;

    public PlaceController(PlaceService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ApiResponse<PlaceResponseDto> getPlace(@PathVariable String id) {

        PlaceResponseDto data = service.getPlaceById(id);

        return ApiResponse.<PlaceResponseDto>builder()
                .success(true)
                .message("Place fetched successfully")
                .count(1)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
