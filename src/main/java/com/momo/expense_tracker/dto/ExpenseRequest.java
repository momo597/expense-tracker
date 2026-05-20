package com.momo.expense_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {

  @NotNull @Positive private BigDecimal amount;

  @NotBlank private String name;

  @NotNull private String category;

  @NotNull private LocalDate date;

  private String description;

  public ExpenseRequest() {}

  public ExpenseRequest(
      @NotNull @Positive BigDecimal amount,
      @NotBlank String name,
      @NotNull String category,
      @NotNull LocalDate date,
      String description) {
    this.amount = amount;
    this.name = name;
    this.category = category;
    this.date = date;
    this.description = description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
