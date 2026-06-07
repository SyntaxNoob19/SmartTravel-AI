package com.riya.smarttravel.service;

import com.riya.smarttravel.dto.AuthLoginRequest;
import com.riya.smarttravel.dto.AuthRegisterRequest;
import com.riya.smarttravel.dto.AuthUserDto;
import com.riya.smarttravel.exception.BadRequestException;
import com.riya.smarttravel.entity.UserAccount;
import com.riya.smarttravel.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthUserDto register(AuthRegisterRequest request) {
        String name = normalizeName(request.getName());
        String email = normalizeEmail(request.getEmail());
        String password = request.getPassword();

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("An account with this email already exists. Please login instead.");
        }

        UserAccount account = new UserAccount();
        account.setName(name);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(password));

        UserAccount saved = userAccountRepository.save(account);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public AuthUserDto login(AuthLoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        return toDto(account);
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthUserDto toDto(UserAccount account) {
        return AuthUserDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .build();
    }
}