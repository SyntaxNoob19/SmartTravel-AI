package com.riya.smarttravel.controller;

import com.riya.smarttravel.entity.BudgetPlan;
import com.riya.smarttravel.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    private final BudgetService budgetService;
    public BudgetController(BudgetService budgetService) { this.budgetService = budgetService; }

    @GetMapping("/current")
    public ResponseEntity<BudgetPlan> getCurrent() {
        return ResponseEntity.ok(budgetService.getCurrentBudget());
    }

    @PostMapping("/save")
    public ResponseEntity<BudgetPlan> save(@RequestBody BudgetPlan plan) {
        return ResponseEntity.ok(budgetService.saveOrUpdate(plan));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
