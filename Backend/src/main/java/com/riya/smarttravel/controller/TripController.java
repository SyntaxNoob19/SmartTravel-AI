package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.ApiResponse;
import com.riya.smarttravel.dto.SavedTripCreateRequest;
import com.riya.smarttravel.dto.SavedTripDto;
import com.riya.smarttravel.dto.SavedTripSummaryDto;
import com.riya.smarttravel.service.SavedTripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final SavedTripService savedTripService;

    @PostMapping("/users/{email}")
    public ResponseEntity<ApiResponse<SavedTripDto>> saveTrip(
            @PathVariable String email,
            @Valid @RequestBody SavedTripCreateRequest request
    ) {
        SavedTripDto data = savedTripService.saveTrip(email, request);
        return ResponseEntity.ok(ApiResponse.<SavedTripDto>builder()
                .success(true)
                .message("Trip saved successfully")
                .count(1)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<ApiResponse<List<SavedTripSummaryDto>>> listTrips(@PathVariable String email) {
        List<SavedTripSummaryDto> data = savedTripService.listTrips(email);
        return ResponseEntity.ok(ApiResponse.<List<SavedTripSummaryDto>>builder()
                .success(true)
                .message("Trips fetched successfully")
                .count(data.size())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/user/recent")
    public ResponseEntity<ApiResponse<List<SavedTripSummaryDto>>> recentTrips(
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<SavedTripSummaryDto> data = savedTripService.getRecentTrips(limit);
        return ResponseEntity.ok(ApiResponse.<List<SavedTripSummaryDto>>builder()
                .success(true)
                .message("Recent trips fetched successfully")
                .count(data.size())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SavedTripDto>> getTrip(@PathVariable Long id, @RequestParam(required = false) String email) {
        SavedTripDto data = savedTripService.getTrip(id);
        return ResponseEntity.ok(ApiResponse.<SavedTripDto>builder()
                .success(true)
                .message("Trip fetched successfully")
                .count(1)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @PathVariable Long id,
            @RequestParam String email
    ) {
        savedTripService.deleteTrip(id, email);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Trip deleted successfully")
                .count(0)
                .timestamp(LocalDateTime.now())
                .build());
    }
}