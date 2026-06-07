package com.riya.smarttravel.controller;

import com.riya.smarttravel.dto.ApiResponse;
import com.riya.smarttravel.dto.AuthLoginRequest;
import com.riya.smarttravel.dto.AuthRegisterRequest;
import com.riya.smarttravel.dto.AuthUserDto;
import com.riya.smarttravel.service.AuthService;
import com.riya.smarttravel.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthUserDto>> register(@Valid @RequestBody AuthRegisterRequest request, HttpServletRequest httpRequest) {
        AuthUserDto user = authService.register(request);
        httpRequest.getSession(true).setAttribute("authenticatedUserEmail", user.getEmail());
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok(ApiResponse.<AuthUserDto>builder()
                .success(true)
                .message("Account created successfully")
                .count(1)
                .data(user)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthUserDto>> login(@Valid @RequestBody AuthLoginRequest request, HttpServletRequest httpRequest) {
        AuthUserDto user = authService.login(request);
        httpRequest.getSession(true).setAttribute("authenticatedUserEmail", user.getEmail());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok(ApiResponse.<AuthUserDto>builder()
                .success(true)
                .message("Login successful")
                .count(1)
                .data(user)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserDto>> current() {
        AuthUserDto user = userService.getAuthenticatedUser().toDto();
        return ResponseEntity.ok(ApiResponse.<AuthUserDto>builder()
                .success(true)
                .message("Current user")
                .count(1)
                .data(user)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.noContent().build();
    }
}