package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.ApiResponse;
import com.riya.smarttravel.dto.PlannerRequest;
import com.riya.smarttravel.dto.PlannerResponseDto;
import com.riya.smarttravel.service.PlannerService;
import com.riya.smarttravel.service.PlannerAiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planner")
public class PlannerController {

    private final PlannerService plannerService;
    private final PlannerAiService plannerAiService;

    public PlannerController(PlannerService plannerService, PlannerAiService plannerAiService) {
        this.plannerService = plannerService;
        this.plannerAiService = plannerAiService;
    }

    @PostMapping("/generate")
    public ApiResponse<PlannerResponseDto> generate(@RequestBody PlannerRequest request) {
        PlannerResponseDto data = plannerService.generate(request);

        return ApiResponse.<PlannerResponseDto>builder()
                .success(true)
                .message("Itinerary generated successfully")
                .count(data.getTotalPlaces())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/recommendations")
    public ApiResponse<List<Map<String, String>>> getRecommendations(@RequestParam String region) {
        List<Map<String, String>> data = plannerAiService.getRegionalRecommendations(region);

        return ApiResponse.<List<Map<String, String>>>builder()
                .success(true)
                .message("Regional recommendations fetched successfully")
                .count(data.size())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
