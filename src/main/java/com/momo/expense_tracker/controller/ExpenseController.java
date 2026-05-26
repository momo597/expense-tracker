package com.momo.expense_tracker.controller;

import com.momo.expense_tracker.dto.ExpenseRequest;
import com.momo.expense_tracker.dto.ExpenseResponse;
import com.momo.expense_tracker.dto.SummaryResponse;
import com.momo.expense_tracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Expense management endpoints")
public class ExpenseController {

  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @GetMapping
  public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate,
      Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(expenseService.getAllExpenses(category, startDate, endDate, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(expenseService.getExpenseById(id));
  }

  @GetMapping("/summary")
  public ResponseEntity<List<SummaryResponse>> getSummary(
      @RequestParam(required = false) String option) {

    return ResponseEntity.status(HttpStatus.OK).body(expenseService.getSummary(option));
  }

  @PostMapping
  public ResponseEntity<ExpenseResponse> createExpense(
      @Valid @RequestBody ExpenseRequest expenseRequest) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(expenseService.createExpense(expenseRequest));
  }

  @PostMapping("/batch")
  public ResponseEntity<List<ExpenseResponse>> createMultipleExpenses(
      @Valid @RequestBody List<ExpenseRequest> expenseRequests) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(expenseService.createMultipleExpenses(expenseRequests));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ExpenseResponse> updateExpense(
      @PathVariable UUID id, @Valid @RequestBody ExpenseRequest expenseRequest) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(expenseService.updateExpense(id, expenseRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {

    expenseService.deleteExpense(id);
    return ResponseEntity.noContent().build();
  }
}
