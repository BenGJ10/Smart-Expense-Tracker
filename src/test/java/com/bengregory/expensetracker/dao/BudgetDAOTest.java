package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Budget;
import com.bengregory.expensetracker.model.ExpenseCategory;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetDAOTest {
    private IBudgetDAO budgetDAO;
    private IUserDAO userDAO;

    @BeforeEach
    void setUp() {
        budgetDAO = new BudgetDAO();
        userDAO = new UserDAO();
        SessionManager.getInstance().logout();
    }

    @Test
    void testAddAndGetBudget() {
        String username = "Testuser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Budget budget = new Budget(0, user.getId(), ExpenseCategory.FOOD, 500.0, "MONTHLY", LocalDate.now());
            budgetDAO.addBudget(budget);

            List<Budget> budgets = budgetDAO.getBudgetsByUserId(user.getId());
            assertFalse(budgets.isEmpty(), "Budget should be retrieved");
            assertEquals(500.0, budgets.get(0).getAmount(), "Amount should match");
            assertEquals("MONTHLY", budgets.get(0).getPeriod(), "Period should match");
        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testAddBudgetEmptyPeriod() {
        try {
            User user = userDAO.loginUser("Testuser" + System.currentTimeMillis(), "Password123");
            SessionManager.getInstance().setLoggedInUser(user);
            Budget budget = new Budget(0, user.getId(), ExpenseCategory.EDUCATIONAL, 500.0, "", LocalDate.now());
            assertThrows(InvalidInputException.class, () -> budgetDAO.addBudget(budget),
                    "Should throw exception for empty period");
        } catch (InvalidInputException | DatabaseException e) {
            // Expected
        }
    }
    @Test
    void testUpdateNonExistentBudget() {
        String username = "TestUser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Budget budget = new Budget(9999, user.getId(), ExpenseCategory.FOOD, 600.0, "MONTHLY", LocalDate.now());
            assertThrows(DatabaseException.class, () -> budgetDAO.updateBudget(budget),
                    "Should throw exception for non-existent budget");
        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testAddBudgetWithNullCategory() {
        String username = "Testuser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Budget budget = new Budget(0, user.getId(), null, 300.0, "WEEKLY", LocalDate.now());
            budgetDAO.addBudget(budget);

            List<Budget> budgets = budgetDAO.getBudgetsByUserId(user.getId());
            assertFalse(budgets.isEmpty(), "Budget should be retrieved");
            assertNull(budgets.get(0).getCategory(), "Category should be null for overall budget");
        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testAddBudgetNegativeAmount() {
        String username = "Testuser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Budget budget = new Budget(0, user.getId(), ExpenseCategory.SHOPPING, -100.0, "MONTHLY", LocalDate.now());
            assertThrows(InvalidInputException.class, () -> budgetDAO.addBudget(budget),
                    "Should throw exception for negative amount");
        } catch (InvalidInputException | DatabaseException e) {
            // Expected
        }
    }

    @Test
    void testGetBudgetsWhenEmpty() {
        String username = "EmptyUser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            List<Budget> budgets = budgetDAO.getBudgetsByUserId(user.getId());
            assertTrue(budgets.isEmpty(), "Budgets list should be empty initially");
        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteBudget() {
        String username = "Testuser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Budget budget = new Budget(0, user.getId(), ExpenseCategory.ENTERTAINMENT, 400.0, "WEEKLY", LocalDate.now());
            budgetDAO.addBudget(budget);

            List<Budget> budgets = budgetDAO.getBudgetsByUserId(user.getId());
            assertFalse(budgets.isEmpty(), "Budget should exist before deletion");

            int budgetId = budgets.get(0).getId();
            budgetDAO.deleteBudget(budgetId);

            List<Budget> updatedBudgets = budgetDAO.getBudgetsByUserId(user.getId());
            assertTrue(updatedBudgets.stream().noneMatch(b -> b.getId() == budgetId), "Budget should be deleted");

        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }
}