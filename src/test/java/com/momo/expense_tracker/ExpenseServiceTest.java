package com.momo.expense_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.momo.expense_tracker.dto.ExpenseRequest;
import com.momo.expense_tracker.dto.ExpenseResponse;
import com.momo.expense_tracker.dto.SummaryResponse;
import com.momo.expense_tracker.exception.RessourceNotFoundException;
import com.momo.expense_tracker.model.Expense;
import com.momo.expense_tracker.repository.ExpenseRepository;
import com.momo.expense_tracker.service.ExpenseService;
import com.momo.expense_tracker.utilities.ExpenseCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

  @Mock private ExpenseRepository expenseRepository;

  @InjectMocks private ExpenseService expenseService;

  @Test
  void createExpense_shouldReturnResponse_matchingSentExpense() {

    Expense expenseToSave =
        new Expense(
            UUID.randomUUID(),
            "Weekly groceries",
            ExpenseCategory.FOOD,
            new BigDecimal("50.0"),
            LocalDate.of(2026, 5, 20),
            LocalDate.now(),
            LocalDate.now(),
            "Fruits, vegetables and pasta from the market");

    ExpenseRequest expenseRequest =
        new ExpenseRequest(
            new BigDecimal("50.0"),
            "Weekly groceries",
            "FOOD",
            LocalDate.of(2026, 5, 20),
            "Fruits, vegetables and pasta from the market");

    when(expenseRepository.save(any(Expense.class))).thenReturn(expenseToSave);

    ExpenseResponse result = expenseService.createExpense(expenseRequest);

    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals(LocalDate.now(), result.getCreatedAt());
    assertEquals(LocalDate.now(), result.getUpdatedAt());
    assertEquals(new BigDecimal("50.0"), result.getAmount());
    assertEquals("Weekly groceries", result.getName());
    assertEquals("FOOD", result.getCategory());
    assertEquals(LocalDate.of(2026, 5, 20), result.getDate());
    assertEquals("Fruits, vegetables and pasta from the market", result.getDescription());

    verify(expenseRepository).save(any(Expense.class));
  }

  @Test
  void getAllExpenses_shouldReturnAllExpenses_ifTheyExist() {

    Expense expense1 =
        new Expense(
            UUID.randomUUID(),
            "Netflix subscription",
            ExpenseCategory.ENTERTAINMENT,
            new BigDecimal("15.0"),
            LocalDate.of(2026, 05, 22),
            LocalDate.now(),
            LocalDate.now(),
            "A netflix subscription");

    Pageable pageable = PageRequest.of(0, 10);

    Page<Expense> expensePage = new PageImpl<>(List.of(expense1), pageable, 1);

    when(expenseRepository.findFiltered(
            ExpenseCategory.ENTERTAINMENT,
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 30),
            pageable))
        .thenReturn(expensePage);

    Page<ExpenseResponse> result =
        expenseService.getAllExpenses(
            "ENTERTAINMENT", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 30), pageable);

    assertNotNull(result);
    assertNotNull(result.getContent().get(0).getId());
    assertEquals(1, result.getTotalElements());
    assertEquals(new BigDecimal("15.0"), result.getContent().get(0).getAmount());
    assertEquals("ENTERTAINMENT", result.getContent().get(0).getCategory());
    assertEquals("Netflix subscription", result.getContent().get(0).getName());

    verify(expenseRepository)
        .findFiltered(
            ExpenseCategory.ENTERTAINMENT,
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 30),
            pageable);
  }

  @Test
  void getExpenseById_shouldReturnResponse_ifExist() {

    UUID id = UUID.randomUUID();

    Expense expense =
        new Expense(
            id,
            "Netflix subscription",
            ExpenseCategory.ENTERTAINMENT,
            new BigDecimal("15.0"),
            LocalDate.of(2026, 05, 22),
            LocalDate.now(),
            LocalDate.now(),
            "A netflix subscription");

    when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));

    ExpenseResponse result = expenseService.getExpenseById(id);

    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals(new BigDecimal("15.0"), result.getAmount());
    assertEquals("ENTERTAINMENT", result.getCategory());
    assertEquals(expense.getName(), result.getName());
    assertEquals(LocalDate.of(2026, 05, 22), result.getDate());
    assertEquals(LocalDate.now(), result.getCreatedAt());
    assertEquals(LocalDate.now(), result.getUpdatedAt());
    assertEquals("A netflix subscription", result.getDescription());

    verify(expenseRepository).findById(id);
  }

  @Test
  void getSummary_byMonth_shouldReturnSummaryByMonth() {

    Object[] row1 = new Object[] {"2026-05", new BigDecimal("115.0"), 2L};

    List<Object[]> mockResult = new ArrayList<>();
    mockResult.add(row1);

    when(expenseRepository.summaryByMonth()).thenReturn(mockResult);

    List<SummaryResponse> result = expenseService.getSummary("MONTH");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("2026-05", result.get(0).getLabel());
    assertEquals(new BigDecimal("115.0"), result.get(0).getTotal());
    assertEquals(2L, result.get(0).getCount());

    verify(expenseRepository).summaryByMonth();
  }

  @Test
  void getSummary_byCategory_shouldReturnSummaryByCategory() {

    Object[] row1 = new Object[] {"FOOD", new BigDecimal("115.0"), 2L};

    List<Object[]> mockResult = new ArrayList<>();
    mockResult.add(row1);

    when(expenseRepository.summaryByCategory()).thenReturn(mockResult);

    List<SummaryResponse> result = expenseService.getSummary("CATEGORY");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("food", result.get(0).getLabel());
    assertEquals(new BigDecimal("115.0"), result.get(0).getTotal());
    assertEquals(2L, result.get(0).getCount());

    verify(expenseRepository).summaryByCategory();
  }

  @Test
  void update_shouldUpdateWithPassedExpense_andReturnUpdatedExpense() {

    UUID id = UUID.randomUUID();

    ExpenseRequest expenseRequest =
        new ExpenseRequest(
            new BigDecimal("60.0"),
            "Weekly groceries",
            "FOOD",
            LocalDate.of(2026, 5, 25),
            "Fruits, vegetables and pasta from the market");

    Expense expense =
        new Expense(
            id,
            "Weekly groceries",
            ExpenseCategory.FOOD,
            new BigDecimal("50.0"),
            LocalDate.of(2026, 05, 20),
            LocalDate.now(),
            LocalDate.now(),
            "Fruits, vegetables and pasta from the market");

    when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));
    when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

    ExpenseResponse result = expenseService.updateExpense(id, expenseRequest);

    assertNotNull(result);
    assertEquals(expense.getId(), result.getId());
    assertEquals(new BigDecimal("60.0"), result.getAmount());
    assertEquals(LocalDate.of(2026, 5, 25), result.getDate());

    verify(expenseRepository).findById(id);

    verify(expenseRepository).save(any(Expense.class));
  }

  @Test
  void deleteById_shouldDeleteTheCorrespondingExpense() {

    UUID id = UUID.randomUUID();

    Expense expense =
        new Expense(
            id,
            "Weekly groceries",
            ExpenseCategory.FOOD,
            new BigDecimal("50.0"),
            LocalDate.of(2026, 05, 20),
            LocalDate.now(),
            LocalDate.now(),
            "Fruits, vegetables and pasta from the market");

    when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));
    expenseService.deleteExpense(id);

    verify(expenseRepository).findById(id);

    verify(expenseRepository).deleteById(id);
  }

  @Test
  void getExpenseById_shouldThrow_whenNotFound() {

    UUID id = UUID.randomUUID();

    when(expenseRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(RessourceNotFoundException.class, () -> expenseService.getExpenseById(id));
  }

  @Test
  void update_shouldThrow_whenNotFound() {

    UUID id = UUID.randomUUID();

    ExpenseRequest expenseRequest =
        new ExpenseRequest(
            new BigDecimal("60.0"),
            "Weekly groceries",
            "FOOD",
            LocalDate.of(2026, 5, 25),
            "Fruits, vegetables and pasta from the market");

    when(expenseRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        RessourceNotFoundException.class, () -> expenseService.updateExpense(id, expenseRequest));
  }

  @Test
  void delete_shouldThrow_whenNotFound() {

    UUID id = UUID.randomUUID();

    when(expenseRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(RessourceNotFoundException.class, () -> expenseService.deleteExpense(id));
  }

  @Test
  void getSummary_shouldThrow_whenOptionIsNull() {

    assertThrows(IllegalArgumentException.class, () -> expenseService.getSummary(null));
  }

  @Test
  void getSummary_shouldThrow_whenOptionIsNotValid() {

    assertThrows(IllegalArgumentException.class, () -> expenseService.getSummary("TEST"));
  }
}
