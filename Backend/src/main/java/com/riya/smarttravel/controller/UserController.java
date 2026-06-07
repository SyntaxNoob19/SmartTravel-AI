package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.ApiResponse;
import com.riya.smarttravel.dto.AuthUserDto;
import com.riya.smarttravel.dto.UserProfileDto;
import com.riya.smarttravel.dto.SavedTripSummaryDto;
import com.riya.smarttravel.entity.UserAccount;
import com.riya.smarttravel.repository.UserAccountRepository;
import com.riya.smarttravel.service.SavedTripService;
import com.riya.smarttravel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAccountRepository userAccountRepository;
    private final SavedTripService savedTripService;
    private final UserService userService;

    @GetMapping("/{email}/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(@PathVariable("email") String email) {
        UserAccount currentUser = userService.getAuthenticatedUser();
        if (!currentUser.getEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(
                    ApiResponse.<UserProfileDto>builder()
                            .success(false)
                            .message("Unauthorized access to user profile")
                            .count(0)
                            .data(null)
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(email).orElse(null);
        if (account == null) {
            return ResponseEntity.ok(ApiResponse.<UserProfileDto>builder()
                    .success(false)
                    .message("User not found")
                    .count(0)
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        AuthUserDto userDto = AuthUserDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .build();

        List<SavedTripSummaryDto> trips = savedTripService.listTrips(account.getEmail());

        UserProfileDto profile = UserProfileDto.builder()
                .user(userDto)
                .trips(trips)
                .build();

        return ResponseEntity.ok(ApiResponse.<UserProfileDto>builder()
                .success(true)
                .message("User profile")
                .count(trips == null ? 0 : trips.size())
                .data(profile)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
