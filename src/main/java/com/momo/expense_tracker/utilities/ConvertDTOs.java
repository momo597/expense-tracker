package com.momo.expense_tracker.utilities;

import com.momo.expense_tracker.dto.ExpenseRequest;
import com.momo.expense_tracker.dto.ExpenseResponse;
import com.momo.expense_tracker.model.Expense;

public final class ConvertDTOs {

  public static ExpenseResponse convertToResponseDTO(Expense expense) {

    ExpenseResponse expenseResponse =
        new ExpenseResponse(
            expense.getId(),
            expense.getAmount(),
            expense.getName(),
            expense.getCategory().toString(),
            expense.getDate(),
            expense.getDescription(),
            expense.getCreatedAt(),
            expense.getUpdatedAt());

    return expenseResponse;
  }

  public static Expense convertToExpense(ExpenseRequest expenseRequest, Expense expense) {

    expense.setAmount(expenseRequest.getAmount());
    expense.setCategory(ExpenseCategory.valueOf(expenseRequest.getCategory().toUpperCase()));
    expense.setDescription(expenseRequest.getDescription());
    expense.setName(expenseRequest.getName());
    expense.setDate(expenseRequest.getDate());

    return expense;
  }
}
