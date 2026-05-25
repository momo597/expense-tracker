package com.momo.expense_tracker.repository;

import com.momo.expense_tracker.model.Expense;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseRepository
    extends JpaRepository<Expense, UUID>, JpaSpecificationExecutor<Expense> {

  @Query(
      "SELECT e.category, SUM(e.amount), COUNT(e) FROM Expense e "
          + "GROUP BY e.category ORDER BY e.category ASC")
  List<Object[]> summaryByCategory();

  @Query(
      value =
          "SELECT TO_CHAR(e.date, 'YYYY-MM'), SUM(e.amount), COUNT(e.id) "
              + "FROM expenses e GROUP BY TO_CHAR(e.date, 'YYYY-MM')",
      nativeQuery = true)
  List<Object[]> summaryByMonth();
}
