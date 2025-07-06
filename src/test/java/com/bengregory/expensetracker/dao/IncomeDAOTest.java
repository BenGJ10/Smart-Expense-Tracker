package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Income;
import com.bengregory.expensetracker.model.IncomeSource;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeDAOTest {
    private IIncomeDAO incomeDAO;
    private IUserDAO userDAO;

    @BeforeEach
    void setUp() {
        incomeDAO = new IncomeDAO();
        userDAO = new UserDAO();
        SessionManager.getInstance().logout(); // Clear session
    }

    @Test
    void testAddAndGetIncome() {
        String username = "TestUser" + System.currentTimeMillis();
        try {
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Income income = new Income(
                    0,
                    user.getId(),
                    1000.0,
                    IncomeSource.SALARY,
                    LocalDateTime.now(),
                    "Monthly salary"
            );

            incomeDAO.addIncome(income);

            List<Income> incomes = incomeDAO.getIncomeByUserId(user.getId());
            assertFalse(incomes.isEmpty(), "Income should be retrieved");
            assertEquals(1000.0, incomes.get(0).getAmount(), "Amount should match");
            assertEquals(IncomeSource.SALARY, incomes.get(0).getSource(), "Source should match");
        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testAddIncomeNoUser() throws InvalidInputException {
        SessionManager.getInstance().logout();

        Income income = new Income(
                0,
                1,
                500.0,
                IncomeSource.OTHER,
                LocalDateTime.now(),
                "Freelance"
        );

        assertThrows(DatabaseException.class, () -> incomeDAO.addIncome(income),
                "Should throw exception for no logged-in user");
    }

    @Test
    void testAddIncomeInvalidAmount() {
        try {
            String username = "TestUser" + System.currentTimeMillis();
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Income income = new Income(
                    0,
                    user.getId(),
                    -500.0,
                    IncomeSource.SALARY,
                    LocalDateTime.now(),
                    "Negative amount test"
            );

            assertThrows(InvalidInputException.class, () -> incomeDAO.addIncome(income),
                    "Should throw exception for invalid amount");
        } catch (InvalidInputException | DatabaseException e) {
            // Expected
        }
    }

    @Test
    void testGetIncomeWhenEmpty() {
        try {
            String username = "TestUser" + System.currentTimeMillis();
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            List<Income> incomes = incomeDAO.getIncomeByUserId(user.getId());
            assertTrue(incomes.isEmpty(), "Income list should be empty for new user");

        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteIncome() {
        try {
            String username = "TestUser" + System.currentTimeMillis();
            userDAO.registerUser(username, "Password123");
            User user = userDAO.loginUser(username, "Password123");
            SessionManager.getInstance().setLoggedInUser(user);

            Income income = new Income(
                    0,
                    user.getId(),
                    700.0,
                    IncomeSource.OTHER,
                    LocalDateTime.now(),
                    "To be deleted"
            );

            incomeDAO.addIncome(income);
            List<Income> incomes = incomeDAO.getIncomeByUserId(user.getId());
            assertFalse(incomes.isEmpty());

            int incomeId = incomes.get(0).getId();
            incomeDAO.deleteIncome(incomeId);

            List<Income> incomesAfterDelete = incomeDAO.getIncomeByUserId(user.getId());
            assertTrue(incomesAfterDelete.stream().noneMatch(i -> i.getId() == incomeId),
                    "Income should be deleted");

        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

}
