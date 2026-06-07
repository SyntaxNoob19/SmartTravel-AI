package com.riya.smarttravel.repository;

import com.riya.smarttravel.entity.BudgetPlan;
import com.riya.smarttravel.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<BudgetPlan, Long> {
    Optional<BudgetPlan> findByUser(UserAccount user);
    long countByUser(UserAccount user);
}
