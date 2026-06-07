package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.ApiResponse;
import com.riya.smarttravel.dto.ProfileDto;
import com.riya.smarttravel.entity.UserAccount;
import com.riya.smarttravel.repository.BudgetRepository;
import com.riya.smarttravel.repository.SavedTripRepository;
import com.riya.smarttravel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final SavedTripRepository savedTripRepository;
    private final BudgetRepository budgetRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfile() {
        UserAccount user = userService.getAuthenticatedUser();
        long tripCount = savedTripRepository.countByUserEmailIgnoreCase(user.getEmail());
        long budgetCount = budgetRepository.countByUser(user);
        ProfileDto dto = new ProfileDto(
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                tripCount,
                budgetCount
        );
        ApiResponse<ProfileDto> resp = ApiResponse.<ProfileDto>builder()
                .success(true)
                .message("User profile fetched")
                .count(1)
                .data(dto)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(resp);
    }
}
