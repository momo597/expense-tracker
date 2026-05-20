package com.momo.expense_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ExpenseResponse {

  private UUID id;

  private BigDecimal amount;

  private String name;

  private String category;

  private LocalDate date;

  private LocalDate createdAt;

  private LocalDate updatedAt;

  public ExpenseResponse() {}

  public ExpenseResponse(
      UUID id,
      BigDecimal amount,
      String name,
      String category,
      LocalDate date,
      LocalDate createdAt,
      LocalDate updatedAt) {
    this.id = id;
    this.amount = amount;
    this.name = name;
    this.category = category;
    this.date = date;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }
}
