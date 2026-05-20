package com.momo.expense_tracker.exception;

import java.util.UUID;

public class RessourceNotFoundException extends RuntimeException {

  public RessourceNotFoundException(UUID id) {
    super("Expense not found with id " + id);
  }
}
