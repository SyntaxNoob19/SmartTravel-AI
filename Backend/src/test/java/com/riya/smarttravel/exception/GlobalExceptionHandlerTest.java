package com.riya.smarttravel.exception;

import com.riya.smarttravel.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleGenericHidesInternalDetails() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleGeneric(new RuntimeException("database password leaked"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertEquals(0, response.getBody().getCount());
    }

    @Test
    void handleMethodNotSupportedReturns405() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("GET");

        ResponseEntity<ApiResponse<Object>> response = handler.handleMethodNotSupported(ex);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("Method GET is not supported for this endpoint", response.getBody().getMessage());
        assertEquals(0, response.getBody().getCount());
    }

    @Test
    void handleInvalidJsonReturns400() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleInvalidJson(
            new HttpMessageNotReadableException("malformed json", mock(HttpInputMessage.class))
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid JSON body", response.getBody().getMessage());
        assertEquals(0, response.getBody().getCount());
    }
}
