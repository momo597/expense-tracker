package com.momo.expense_tracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.momo.expense_tracker.repository.ExpenseRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class ExpenseTrackerControllerIntegrationTests extends BasePostgresIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ExpenseRepository expenseRepository;

  @BeforeEach
  void setUp() {

    expenseRepository.deleteAll();
  }

  @Test
  void createExpense_shouldReturnCreatedResponseEntity() throws Exception {

    String requestBody =
        """
        {
          "amount": 42.50,
          "name": "Weekly groceries",
          "category": "MEDICAL",
          "date": "2026-05-20",
          "description": "Fruits, vegetables and pasta from the market"
        }
        """;

    mockMvc
        .perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.amount").value(42.50))
        .andExpect(jsonPath("$.name").value("Weekly groceries"))
        .andExpect(jsonPath("$.category").value("MEDICAL"))
        .andExpect(jsonPath("$.date").value("2026-05-20"))
        .andExpect(jsonPath("$.description").value("Fruits, vegetables and pasta from the market"))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty());
  }

  @Test
  void createExpense_shouldReturnCode400WhenInvalidBody() throws Exception {

    String requestBody =
        """
        {
          "amount": -42.50,
          "category": "test",
          "description": "Fruits, vegetables and pasta from the market"
        }
        """;

    mockMvc
        .perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.details").exists());
  }

  @Test
  void getAllExpenses_shouldReturnFilteredPaginatedResponseWithNoFilters_shouldReturnExpenses()
      throws Exception {
    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    String food2 =
        """
        {
          "amount": 25.00,
          "name": "Lunch",
          "category": "FOOD",
          "date": "2026-05-15",
          "description": "Restaurant"
        }
        """;

    String medical =
        """
        {
          "amount": 100.00,
          "name": "Doctor visit",
          "category": "MEDICAL",
          "date": "2026-05-12",
          "description": "Checkup"
        }
        """;
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1));
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food2));
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(medical));

    mockMvc
        .perform(get("/api/expenses").param("page", "0").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.content[0].category").value("FOOD"));
  }

  @Test
  void getAllExpensesWithFilter_shouldReturnFilteredPaginatedResponse() throws Exception {

    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    String food2 =
        """
        {
          "amount": 25.00,
          "name": "Lunch",
          "category": "FOOD",
          "date": "2026-05-15",
          "description": "Restaurant"
        }
        """;

    String medical =
        """
        {
          "amount": 100.00,
          "name": "Doctor visit",
          "category": "MEDICAL",
          "date": "2026-05-12",
          "description": "Checkup"
        }
        """;
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1));
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food2));
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(medical));

    mockMvc
        .perform(
            get("/api/expenses")
                .param("category", "food")
                .param("startDate", "2026-05-01")
                .param("endDate", "2026-05-30")
                .param("page", "0")
                .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].category").value("FOOD"));
  }

  @Test
  void getAllExpensesWithInvalidCategory_shouldReturnCode400() throws Exception {
    mockMvc
        .perform(
            get("/api/expenses").param("category", "test").param("page", "0").param("size", "1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void getExpenseById_shouldReturnFoundExpense() throws Exception {

    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    MvcResult postResult =
        mockMvc
            .perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1))
            .andReturn();

    String id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(get("/api/expenses/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(30.00))
        .andExpect(jsonPath("$.name").value("Groceries"))
        .andExpect(jsonPath("$.category").value("FOOD"))
        .andExpect(jsonPath("$.date").value("2026-05-10"))
        .andExpect(jsonPath("$.description").value("Weekly shop"))
        .andExpect(jsonPath("$.id").value(id));
  }

  @Test
  void getExpenseById_shouldReturn404WhenUnknownId() throws Exception {
    mockMvc
        .perform(get("/api/expenses/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void update_shouldModifyExpanse_andReturnIt() throws Exception {

    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    MvcResult postResult =
        mockMvc
            .perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1))
            .andReturn();

    String id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

    String requestBody =
        """
        {
          "amount": 40.00,
          "name": "Updated Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Updated weekly shop"
        }
        """;

    mockMvc
        .perform(
            put("/api/expenses/" + id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(40.00))
        .andExpect(jsonPath("$.name").value("Updated Groceries"))
        .andExpect(jsonPath("$.category").value("FOOD"))
        .andExpect(jsonPath("$.date").value("2026-05-10"))
        .andExpect(jsonPath("$.description").value("Updated weekly shop"))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty());
  }

  @Test
  void updateWithInvalidBody_shouldReturnCode400() throws Exception {

    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    MvcResult postResult =
        mockMvc
            .perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1))
            .andReturn();

    String id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

    String requestBody =
        """
        {
          "amount": -40.00,
          "category": "TEST",
          "date": "2026-05-10",
          "description": "Updated weekly shop"
        }
        """;

    mockMvc
        .perform(
            put("/api/expenses/" + id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateWithUnknowID_shouldReturnCode404() throws Exception {

    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    mockMvc
        .perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1))
        .andReturn();

    String requestBody =
        """
        {
          "amount": 40.00,
          "name": "Updated Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Updated weekly shop"
        }
        """;

    mockMvc
        .perform(
            put("/api/expenses/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_shouldDeleteTheGivenExpense() throws Exception {

    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    MvcResult postResult =
        mockMvc
            .perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1))
            .andReturn();

    String id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

    mockMvc.perform(delete("/api/expenses/" + id)).andExpect(status().isNoContent());

    mockMvc
        .perform(get("/api/expenses/" + id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void deleteWithUnknownID_shouldReturnCode404() throws Exception {

    mockMvc.perform(delete("/api/expenses/" + UUID.randomUUID())).andExpect(status().isNotFound());
  }

  @Test
  void getSummary_shouldReturnSummaryOfExpenses() throws Exception {

    String food1 =
        """
        {
          "amount": 30.00,
          "name": "Groceries",
          "category": "FOOD",
          "date": "2026-05-10",
          "description": "Weekly shop"
        }
        """;

    String food2 =
        """
        {
          "amount": 25.00,
          "name": "Lunch",
          "category": "FOOD",
          "date": "2026-05-15",
          "description": "Restaurant"
        }
        """;

    String medical =
        """
        {
          "amount": 100.00,
          "name": "Doctor visit",
          "category": "MEDICAL",
          "date": "2026-05-12",
          "description": "Checkup"
        }
        """;

    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food1));
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(food2));
    mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(medical));

    mockMvc
        .perform(get("/api/expenses/summary").param("option", "month"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].label").value("2026-05"))
        .andExpect(jsonPath("$[0].total").value(155.0))
        .andExpect(jsonPath("$[0].count").value(3));

    mockMvc
        .perform(get("/api/expenses/summary").param("option", "category"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].label").value("food"))
        .andExpect(jsonPath("$[0].total").value(55.0))
        .andExpect(jsonPath("$[0].count").value(2))
        .andExpect(jsonPath("$[1].label").value("medical"))
        .andExpect(jsonPath("$[1].total").value(100.0))
        .andExpect(jsonPath("$[1].count").value(1));
  }

  @Test
  void getSummaryWithNoParam_shouldReturnCode400() throws Exception {

    mockMvc
        .perform(get("/api/expenses/summary"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void getSummaryWithInvalidOption_shouldReturnCode400() throws Exception {

    mockMvc
        .perform(get("/api/expenses/summary").param("option", "week"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.message").exists());
  }
}
