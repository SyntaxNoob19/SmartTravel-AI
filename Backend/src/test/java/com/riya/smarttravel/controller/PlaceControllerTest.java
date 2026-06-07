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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaceControllerTest {

    private final PlaceService service = mock(PlaceService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new PlaceController(service))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    @Test
    void getPlaceReturnsExpandedPlaceDetails() throws Exception {
        PlaceResponseDto dto = PlaceResponseDto.builder()
                .placeId("IND001")
                .placeName("Hawa Mahal")
                .significance("Historic palace")
                .entryFee(50.0)
                .rating(4.5)
                .nearestAirport("Jaipur Intl")
                .safetyScore(9.2)
                .build();

        when(service.getPlaceById("IND001")).thenReturn(dto);

        mockMvc.perform(get("/api/place/IND001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.placeId").value("IND001"))
                .andExpect(jsonPath("$.data.significance").value("Historic palace"))
                .andExpect(jsonPath("$.data.entryFee").value(50.0))
                .andExpect(jsonPath("$.data.rating").value(4.5))
                .andExpect(jsonPath("$.data.nearestAirport").value("Jaipur Intl"))
                .andExpect(jsonPath("$.data.safetyScore").value(9.2));
    }

    @Test
    void getPlaceReturnsNotFoundWhenMissing() throws Exception {
        when(service.getPlaceById("IND999"))
                .thenThrow(new ResourceNotFoundException("Place not found: IND999"));

        mockMvc.perform(get("/api/place/IND999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Place not found: IND999"));
    }
}
