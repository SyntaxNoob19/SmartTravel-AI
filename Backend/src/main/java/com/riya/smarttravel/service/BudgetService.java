package com.riya.smarttravel.service;

import com.riya.smarttravel.entity.BudgetPlan;
import com.riya.smarttravel.entity.UserAccount;
import com.riya.smarttravel.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserService userService;

    public BudgetService(BudgetRepository budgetRepository, UserService userService) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
    }

    public BudgetPlan getCurrentBudget() {
        UserAccount user = userService.getAuthenticatedUser();
        return budgetRepository.findByUser(user).orElse(new BudgetPlan());
    }

    @Transactional
    public BudgetPlan saveOrUpdate(BudgetPlan plan) {
        UserAccount user = userService.getAuthenticatedUser();
        if (plan.getId() != null) {
            BudgetPlan existing = budgetRepository.findById(plan.getId())
                    .orElseThrow(() -> new RuntimeException("Budget plan not found"));
            if (!existing.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized budget update");
            }
        }
        plan.setUser(user);
        plan.setUpdatedAt(java.time.LocalDateTime.now());
        return budgetRepository.save(plan);
    }

    @Transactional
    public void delete(Long id) {
        BudgetPlan plan = budgetRepository.findById(id).orElseThrow(() -> new RuntimeException("Budget not found"));
        UserAccount user = userService.getAuthenticatedUser();
        if (!plan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        budgetRepository.delete(plan);
    }
}
