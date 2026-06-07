package com.riya.smarttravel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUserDto {
    private Long id;
    private String name;
    private String email;
}