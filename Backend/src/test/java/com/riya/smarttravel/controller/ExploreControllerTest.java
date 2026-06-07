package com.riya.smarttravel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.riya.smarttravel.dto.PlaceResponseDto;
import com.riya.smarttravel.exception.GlobalExceptionHandler;
import com.riya.smarttravel.exception.ResourceNotFoundException;
import com.riya.smarttravel.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExploreControllerTest {

    private final PlaceService service = mock(PlaceService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExploreController(service))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    @Test
    void filterReturnsExpandedPlacePayload() throws Exception {
        PlaceResponseDto dto = PlaceResponseDto.builder()
                .placeId("IND001")
                .placeName("Hawa Mahal")
                .rating(4.5)
                .recommendedDurationHours(2.0)
                .familyFriendly(true)
                .weatherType("Sunny")
                .priority("Must Visit")
                .build();

        when(service.smartFilter(
                eq("North"),
                eq("Heritage"),
                eq("historic"),
                eq(null),
                eq(null),
                eq(true),
                eq(null),
                eq(null),
                eq(null),
                eq(4.0),
                eq("rating"),
                eq("desc")
        )).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/explore/filter")
                        .param("region", "North")
                        .param("category", "Heritage")
                        .param("mood", "historic")
                        .param("familyFriendly", "true")
                        .param("minRating", "4.0")
                        .param("sortBy", "rating")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].placeId").value("IND001"))
                .andExpect(jsonPath("$.data[0].rating").value(4.5))
                .andExpect(jsonPath("$.data[0].recommendedDurationHours").value(2.0))
                .andExpect(jsonPath("$.data[0].familyFriendly").value(true))
                .andExpect(jsonPath("$.data[0].priority").value("Must Visit"));
    }

    @Test
    void filterReturnsBadRequestForInvalidMinRatingType() throws Exception {
        mockMvc.perform(get("/api/explore/filter")
                        .param("region", "North")
                        .param("minRating", "oops"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid parameter type: minRating"));
    }

    @Test
    void cityReturnsNotFoundWhenServiceThrows() throws Exception {
        when(service.getPlacesByCity("Unknown"))
                .thenThrow(new ResourceNotFoundException("No places found for city: Unknown"));

        mockMvc.perform(get("/api/explore/city/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No places found for city: Unknown"));
    }
}
