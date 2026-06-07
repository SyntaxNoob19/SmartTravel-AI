package com.riya.smarttravel.repository;

import com.riya.smarttravel.entity.SavedTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import com.riya.smarttravel.entity.UserAccount;

import java.util.List;
import java.util.Optional;

public interface SavedTripRepository extends JpaRepository<SavedTrip, Long> {
    List<SavedTrip> findByUserEmailIgnoreCaseOrderByCreatedAtDesc(String userEmail);

    Optional<SavedTrip> findByIdAndUserEmailIgnoreCase(Long id, String userEmail);
    long countByUserEmailIgnoreCase(String userEmail);
}