package com.momo.expense_tracker.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.momo.expense_tracker.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

}
