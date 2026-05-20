package com.momo.expense_tracker.repository;

import com.momo.expense_tracker.model.Expense;
import com.momo.expense_tracker.utilities.ExpenseCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

  @Query(
      "SELECT e FROM Expense e WHERE "
          + "(:category IS NULL OR e.category = :category) AND "
          + "(:startDate IS NULL OR e.date >= :startDate) AND "
          + "(:endDate IS NULL OR e.date <= :endDate)")
  Page<Expense> findFiltered(
      @Param("category") ExpenseCategory category,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);

  @Query("SELECT e.category, SUM(e.amount), COUNT(e) FROM Expense e " + "GROUP BY e.category")
  List<Object[]> summaryByCategory();

  @Query(
      value =
          "SELECT TO_CHAR(e.date, 'YYYY-MM'), SUM(e.amount), COUNT(e.id) "
              + "FROM expenses e GROUP BY TO_CHAR(e.date, 'YYYY-MM')",
      nativeQuery = true)
  List<Object[]> summaryByMonth();
}
