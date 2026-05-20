package com.momo.expense_tracker.service;

import com.momo.expense_tracker.dto.ExpenseRequest;
import com.momo.expense_tracker.dto.ExpenseResponse;
import com.momo.expense_tracker.model.Expense;
import com.momo.expense_tracker.repository.ExpenseRepository;
import java.util.List;
import java.util.UUID;
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

    convertToExpense(expenseRequest, expense);

    // Save to DB
    Expense saved = expenseRepository.save(expense);

    // Map saved entity to response DTO

    return convertToResponseDTO(saved);
  }

  public List<ExpenseResponse> getAllExpenses() {
    return expenseRepository.findAll().stream()
        .map(expense -> convertToResponseDTO(expense))
        .toList();
  }

  public ExpenseResponse getExpenseById(UUID id) {
    return expenseRepository
        .findById(id)
        .map(expense -> convertToResponseDTO(expense))
        .orElseThrow();
  }

  public ExpenseResponse updateExpense(UUID id, ExpenseRequest expenseRequest) {

    // Map request DTO to entity

    Expense foundExpense = expenseRepository.findById(id).orElseThrow();

    convertToExpense(expenseRequest, foundExpense);

    // Save to DB
    Expense saved = expenseRepository.save(foundExpense);

    // Map saved entity to response DTO
    return convertToResponseDTO(saved);
  }

  public void deleteExpense(UUID id) {

    Expense expense = expenseRepository.findById(id).orElseThrow();

    expenseRepository.deleteById(expense.getId());
  }

  private ExpenseResponse convertToResponseDTO(Expense expense) {

    ExpenseResponse expenseResponse =
        new ExpenseResponse(
            expense.getId(),
            expense.getAmount(),
            expense.getName(),
            expense.getCategory(),
            expense.getDate(),
            expense.getCreatedAt(),
            expense.getUpdatedAt());

    return expenseResponse;
  }

  private Expense convertToExpense(ExpenseRequest expenseRequest, Expense expense) {

    expense.setAmount(expenseRequest.getAmount());
    expense.setCategory(expenseRequest.getCategory());
    expense.setDescription(expenseRequest.getDescription());
    expense.setName(expenseRequest.getName());
    expense.setDate(expenseRequest.getDate());

    return expense;
  }
}
