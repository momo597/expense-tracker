package com.momo.expense_tracker.service;

import com.momo.expense_tracker.dto.ExpenseRequest;
import com.momo.expense_tracker.dto.ExpenseResponse;
import com.momo.expense_tracker.dto.SummaryResponse;
import com.momo.expense_tracker.exception.RessourceNotFoundException;
import com.momo.expense_tracker.model.Expense;
import com.momo.expense_tracker.repository.ExpenseRepository;
import com.momo.expense_tracker.utilities.ConvertDTOs;
import com.momo.expense_tracker.utilities.ExpenseCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  public ExpenseService(ExpenseRepository expenseRepository) {
    this.expenseRepository = expenseRepository;
  }

  public ExpenseResponse createExpense(ExpenseRequest expenseRequest) {

    // Map request DTO to entity
    Expense expense = new Expense();

    ConvertDTOs.convertToExpense(expenseRequest, expense);

    // Save to DB
    Expense saved = expenseRepository.save(expense);

    // Map saved entity to response DTO

    return ConvertDTOs.convertToResponseDTO(saved);
  }

  public Page<ExpenseResponse> getAllExpenses(
      String category, LocalDate startDate, LocalDate endDate, Pageable pageable) {

    Page<Expense> expenses =
        expenseRepository.findFiltered(
            category != null ? ExpenseCategory.valueOf(category.toUpperCase()) : null,
            startDate,
            endDate,
            pageable);

    return expenses.map(expense -> ConvertDTOs.convertToResponseDTO(expense));
  }

  public ExpenseResponse getExpenseById(UUID id) {
    return expenseRepository
        .findById(id)
        .map(expense -> ConvertDTOs.convertToResponseDTO(expense))
        .orElseThrow(() -> new RessourceNotFoundException(id));
  }

  public List<SummaryResponse> getSummary(String option) {

    if (option == null) throw new IllegalArgumentException("Option cannot be null");
    else if (option.toUpperCase().equals("CATEGORY")) {

      return expenseRepository.summaryByCategory().stream()
          .map(
              object ->
                  new SummaryResponse(
                      object[0].toString().toLowerCase(), (BigDecimal) object[1], (Long) object[2]))
          .toList();
    } else if (option.toUpperCase().equals("MONTH")) {
      return expenseRepository.summaryByMonth().stream()
          .map(
              object ->
                  new SummaryResponse(
                      object[0].toString().toLowerCase(), (BigDecimal) object[1], (Long) object[2]))
          .toList();
    } else {
      throw new IllegalArgumentException("Option must be either category or month");
    }
  }

  public ExpenseResponse updateExpense(UUID id, ExpenseRequest expenseRequest) {

    // Map request DTO to entity

    Expense foundExpense =
        expenseRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(id));

    ConvertDTOs.convertToExpense(expenseRequest, foundExpense);

    // Save to DB
    Expense saved = expenseRepository.save(foundExpense);

    // Map saved entity to response DTO
    return ConvertDTOs.convertToResponseDTO(saved);
  }

  public void deleteExpense(UUID id) {

    Expense expense =
        expenseRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(id));

    expenseRepository.deleteById(expense.getId());
  }
}
