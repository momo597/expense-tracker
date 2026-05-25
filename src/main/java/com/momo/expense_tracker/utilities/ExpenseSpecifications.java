package com.momo.expense_tracker.utilities;

import com.momo.expense_tracker.model.Expense;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpecifications {

  public static Specification<Expense> hasCategory(String category) {
    return (root, query, criteriaBuilder) -> {
      if (category == null) {
        return null;
      }

      return criteriaBuilder.equal(
          root.get("category"), ExpenseCategory.valueOf(category.toUpperCase()));
    };
  }

  public static Specification<Expense> dateGreaterThanEqual(LocalDate startDate) {
    return (root, query, criteriaBuilder) -> {
      if (startDate == null) {
        return null;
      }

      return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
    };
  }

  public static Specification<Expense> dateLessThan(LocalDate endDate) {
    return (root, query, criteriaBuilder) -> {
      if (endDate == null) {
        return null;
      }

      return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
    };
  }
}
