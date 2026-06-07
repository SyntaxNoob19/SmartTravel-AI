package com.riya.smarttravel.dto;

import java.time.LocalDateTime;

public class ProfileDto {
    private String email;
    private String name;
    private LocalDateTime createdAt;
    private long tripsCount;
    private long budgetsCount;

    public ProfileDto() {}

    public ProfileDto(String email, String name, LocalDateTime createdAt, long tripsCount, long budgetsCount) {
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
        this.tripsCount = tripsCount;
        this.budgetsCount = budgetsCount;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public long getTripsCount() { return tripsCount; }
    public void setTripsCount(long tripsCount) { this.tripsCount = tripsCount; }

    public long getBudgetsCount() { return budgetsCount; }
    public void setBudgetsCount(long budgetsCount) { this.budgetsCount = budgetsCount; }
}
