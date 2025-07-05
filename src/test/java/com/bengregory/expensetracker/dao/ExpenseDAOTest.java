package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.model.ExpenseCategory;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseDAOTest {
    private IExpenseDAO expenseDAO;
    private IUserDAO userDAO;

    @BeforeEach
    void setUp() {
        expenseDAO = new ExpenseDAO();
        userDAO = new UserDAO();
        SessionManager.getInstance().logout(); // Clear session before each test
    }

    @Test
    void testAddAndGetExpense() {
        String username = "Testuser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Expense expense = new Expense(
                    0,
                    user.getId(),
                    100.0,
                    ExpenseCategory.FOOD,
                    LocalDateTime.now(),
                    "Dinner"
            );

            expenseDAO.addExpense(expense);

            List<Expense> expenses = expenseDAO.getExpensesByUserId(user.getId());
            assertFalse(expenses.isEmpty(), "Expense should be retrieved");
            assertEquals(100.0, expenses.get(0).getAmount(), "Amount should match");
            assertEquals(ExpenseCategory.FOOD, expenses.get(0).getCategory(), "Category should match");

        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testAddExpenseNoUser() throws InvalidInputException {
        SessionManager.getInstance().logout();

        Expense expense = new Expense(
                0,
                1,
                100.0,
                ExpenseCategory.ENTERTAINMENT,
                LocalDateTime.now(),
                "Concert"
        );

        assertThrows(DatabaseException.class, () -> expenseDAO.addExpense(expense),
                "Should throw exception for no logged-in user");
    }

    @Test
    void testUpdateNonExistentExpense() {
        try {
            String username = "Testuser" + System.currentTimeMillis();
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Expense expense = new Expense(
                    9999,
                    user.getId(),
                    200.0,
                    ExpenseCategory.EDUCATIONAL,
                    LocalDateTime.now(),
                    "Books"
            );

            assertThrows(DatabaseException.class, () -> expenseDAO.updateExpense(expense),
                    "Should throw exception for non-existent expense");

        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }
}
