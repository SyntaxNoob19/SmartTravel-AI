package com.riya.smarttravel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.smarttravel.dto.PlannerRequest;
import com.riya.smarttravel.dto.PlannerResponseDto;
import com.riya.smarttravel.dto.SavedTripCreateRequest;
import com.riya.smarttravel.dto.SavedTripDto;
import com.riya.smarttravel.dto.SavedTripSummaryDto;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.exception.ResourceNotFoundException;
import com.riya.smarttravel.entity.SavedTrip;
import com.riya.smarttravel.entity.UserAccount;
import com.riya.smarttravel.repository.SavedTripRepository;
import com.riya.smarttravel.repository.UserAccountRepository;
import com.riya.smarttravel.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedTripService {

    private static final DateTimeFormatter TITLE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final SavedTripRepository savedTripRepository;
    private final UserAccountRepository userAccountRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Transactional
    public SavedTripDto saveTrip(String email, SavedTripCreateRequest request) {
        String normalizedEmail = normalizeEmail(email != null ? email : request == null ? null : request.getUserEmail());
        if (normalizedEmail == null) {
            throw new BadRequestException("User email is required");
        }
        UserAccount currentUser = userService.getAuthenticatedUser();
        if (!currentUser.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new BadRequestException("Unauthorized access to user data");
        }
        if (request == null || request.getPlannerResponse() == null) {
            throw new BadRequestException("Planner data is required");
        }

        UserAccount account = userAccountRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found: " + normalizedEmail));

        SavedTrip savedTrip = new SavedTrip();
        savedTrip.setUserId(account.getId());
        savedTrip.setUserEmail(account.getEmail());
        savedTrip.setTripName(buildTripName(request.getTripName(), request.getDestination(), request.getPlannerResponse()));
        savedTrip.setDestination(resolveDestination(request.getDestination(), request.getPlannerRequest(), request.getPlannerResponse()));
        savedTrip.setPlannerRequestJson(writeJson(request.getPlannerRequest()));
        savedTrip.setPlannerResponseJson(writeJson(request.getPlannerResponse()));

        SavedTrip stored = savedTripRepository.save(savedTrip);
        return toDto(stored, request.getPlannerRequest(), request.getPlannerResponse());
    }

    @Transactional(readOnly = true)
    public List<SavedTripSummaryDto> listTrips(String email) {
        String normalizedEmail = normalizeEmail(email);
        UserAccount currentUser = userService.getAuthenticatedUser();
        if (!currentUser.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new BadRequestException("Unauthorized access to user data");
        }

        return savedTripRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(normalizedEmail)
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SavedTripSummaryDto> getRecentTrips(int limit) {
        UserAccount currentUser = userService.getAuthenticatedUser();
        return savedTripRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(currentUser.getEmail())
                .stream()
                .limit(limit)
                .map(this::toSummaryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SavedTripDto getTrip(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Trip id is required");
        }

        UserAccount currentUser = userService.getAuthenticatedUser();
        SavedTrip savedTrip = savedTripRepository.findByIdAndUserEmailIgnoreCase(id, currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found or unauthorized"));
        return toDto(savedTrip, readPlannerRequest(savedTrip.getPlannerRequestJson()), readPlannerResponse(savedTrip.getPlannerResponseJson()));
    }

    @Transactional
    public void deleteTrip(Long id, String email) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Trip id is required");
        }
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null) {
            throw new BadRequestException("User email is required");
        }
        UserAccount currentUser = userService.getAuthenticatedUser();
        if (!currentUser.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new BadRequestException("Unauthorized access to user data");
        }
        SavedTrip trip = savedTripRepository.findByIdAndUserEmailIgnoreCase(id, currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        savedTripRepository.delete(trip);
    }

    private SavedTripSummaryDto toSummaryDto(SavedTrip trip) {
        PlannerResponseDto response = readPlannerResponse(trip.getPlannerResponseJson());
        PlannerRequest request = readPlannerRequest(trip.getPlannerRequestJson());
        return SavedTripSummaryDto.builder()
                .id(trip.getId())
                .tripName(trip.getTripName())
                .destination(trip.getDestination())
                .generatedDays(response == null ? null : response.getGeneratedDays())
                .totalPlaces(response == null ? null : response.getTotalPlaces())
                .summary(response == null ? null : response.getSummary())
                .createdAt(trip.getCreatedAt())
                .budget(request == null ? null : request.getBudgetLevel())
                .build();
    }

    private SavedTripDto toDto(SavedTrip trip, PlannerRequest request, PlannerResponseDto response) {
        return SavedTripDto.builder()
                .id(trip.getId())
                .userId(trip.getUserId())
                .userEmail(trip.getUserEmail())
                .tripName(trip.getTripName())
                .destination(trip.getDestination())
                .plannerRequest(request)
                .plannerResponse(response)
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }

    private String buildTripName(String tripName, String destination, PlannerResponseDto response) {
        String normalizedTripName = InputSanitizer.normalize(tripName);
        if (normalizedTripName != null) {
            return normalizedTripName;
        }

        String normalizedDestination = InputSanitizer.normalize(destination);
        if (normalizedDestination == null) {
            normalizedDestination = InputSanitizer.normalize(response == null ? null : response.getSummary());
        }

        String base = normalizedDestination == null ? "Saved Trip" : normalizedDestination;
        return base + " • " + LocalDateTime.now().format(TITLE_TIME_FORMAT);
    }

    private String resolveDestination(String destination, PlannerRequest request, PlannerResponseDto response) {
        String normalizedDestination = InputSanitizer.normalize(destination);
        if (normalizedDestination != null) {
            return normalizedDestination;
        }

        if (request != null) {
            normalizedDestination = InputSanitizer.normalize(request.getCity());
            if (normalizedDestination != null) {
                return normalizedDestination;
            }
            normalizedDestination = InputSanitizer.normalize(request.getRegion());
            if (normalizedDestination != null) {
                return normalizedDestination;
            }
        }

        normalizedDestination = InputSanitizer.normalize(response == null ? null : response.getSummary());
        return normalizedDestination == null ? "Destination" : normalizedDestination;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Unable to store trip details");
        }
    }

    private PlannerRequest readPlannerRequest(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, PlannerRequest.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Unable to read saved planner request");
        }
    }

    private PlannerResponseDto readPlannerResponse(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, PlannerResponseDto.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Unable to read saved planner response");
        }
    }

    private String normalizeEmail(String email) {
        String normalized = InputSanitizer.normalize(email);
        return normalized == null ? null : normalized.toLowerCase();
    }
}