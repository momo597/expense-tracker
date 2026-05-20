package com.momo.expense_tracker.controller;

import com.momo.expense_tracker.dto.ExpenseRequest;
import com.momo.expense_tracker.dto.ExpenseResponse;
import com.momo.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @GetMapping
  public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
    return ResponseEntity.status(HttpStatus.OK).body(expenseService.getAllExpenses());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable UUID id) {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(expenseService.getExpenseById(id));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  public ResponseEntity<ExpenseResponse> createExpense(
      @Valid @RequestBody ExpenseRequest expenseRequest) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(expenseService.createExpense(expenseRequest));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ExpenseResponse> updateExpense(
      @PathVariable UUID id, @Valid @RequestBody ExpenseRequest expenseRequest) {

    try {
      return ResponseEntity.status(HttpStatus.OK)
          .body(expenseService.updateExpense(id, expenseRequest));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {

    try {
      expenseService.deleteExpense(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
