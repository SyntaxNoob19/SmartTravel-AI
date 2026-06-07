package com.riya.smarttravel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.riya.smarttravel.dto.PlannerDayDto;
import com.riya.smarttravel.dto.PlannerPlaceDto;
import com.riya.smarttravel.dto.PlannerResponseDto;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.exception.GlobalExceptionHandler;
import com.riya.smarttravel.service.PlannerService;
import com.riya.smarttravel.service.PlannerAiService;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlannerControllerTest {

    private final PlannerService service = mock(PlannerService.class);
    private final PlannerAiService plannerAiService = mock(PlannerAiService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new PlannerController(service, plannerAiService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    @Test
    void generateReturnsItinerary() throws Exception {
        PlannerResponseDto response = PlannerResponseDto.builder()
                .requestedDays(2)
                .generatedDays(2)
                .totalPlaces(3)
                .travellerType("SOLO")
                .maxHoursPerDay(8.0)
                .summary("Generated 2-day itinerary with 3 places")
                .itinerary(List.of(
                        PlannerDayDto.builder()
                                .dayNumber(1)
                                .totalPlannedHours(4.0)
                                .places(List.of(
                                        PlannerPlaceDto.builder()
                                                .placeId("IND001")
                                                .placeName("Hawa Mahal")
                                                .rating(4.5)
                                                .recommendedDurationHours(2.0)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        when(service.generate(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/api/planner/generate")
                        .contentType("application/json")
                        .content("""
                                {
                                  "days": 2,
                                  "region": "North",
                                  "minRating": 4.0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Itinerary generated successfully"))
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.data.requestedDays").value(2))
                .andExpect(jsonPath("$.data.itinerary[0].dayNumber").value(1))
                .andExpect(jsonPath("$.data.itinerary[0].places[0].placeId").value("IND001"));
    }

    @Test
    void generateReturnsBadRequestForInvalidRequest() throws Exception {
        when(service.generate(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BadRequestException("Days must be between 1 and 14"));

        mockMvc.perform(post("/api/planner/generate")
                        .contentType("application/json")
                        .content("""
                                {
                                  "days": 0,
                                  "region": "North"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Days must be between 1 and 14"));
    }
}
