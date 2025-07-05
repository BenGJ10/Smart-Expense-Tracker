package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private IUserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

//    @AfterEach
//    void cleanUp() {
//        try {
//            UserDAODeletionTest.deleteTestBudgets();
//            UserDAODeletionTest.deleteTestIncomes();
//            UserDAODeletionTest.deleteTestExpenses();
//            UserDAODeletionTest.deleteTestUsers();
//        } catch (DatabaseException e) {
//            System.err.println("Cleanup failed: " + e.getMessage());
//        }
//    }

    @Test
    void testRegisterAndLoginUser() {
        String username = "Testuser" + System.currentTimeMillis();
        String password = "Password123";

        try {
            userDAO.registerUser(username, password);
            User user = userDAO.loginUser(username, password);
            assertNotNull(user, "User should be logged in");
            assertEquals(username, user.getUsername(), "Username should match");
            assertTrue(user.getId() > 0, "User ID should be positive");
        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testRegisterDuplicateUsername() {
        String username = "Testuser" + System.currentTimeMillis();
        String password = "Password123";

        try {
            userDAO.registerUser(username, password);
            assertThrows(InvalidInputException.class, () -> userDAO.registerUser(username, password),
                    "Should throw exception for duplicate username");
        } catch (InvalidInputException | DatabaseException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    void testLoginInvalidCredentials() {
        String username = "nonexistent";
        String password = "Wrongpass";

        assertThrows(InvalidInputException.class, () -> userDAO.loginUser(username, password),
                "Should throw exception for invalid credentials");
    }


    @Test
    void testRegisterInvalidUsername() {
        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("", "Password123"),
                "Should throw exception for empty username");
        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("user@invalid", "Password123"),
                "Should throw exception for non-alphanumeric username");
        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("a".repeat(51), "Password123"),
                "Should throw exception for oversized username");
    }

    @Test
    void testRegisterInvalidPassword() {
        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("validuser1", "Short"),
                "Should throw exception for short password");

        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("validuser2", "password123"),
                "Should throw exception for missing uppercase letter");

        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("validuser3", "PASSWORD123"),
                "Should throw exception for missing lowercase letter");

        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("validuser4", "Password"),
                "Should throw exception for missing digit");

        assertThrows(InvalidInputException.class, () -> userDAO.registerUser("validuser5", "     "),
                "Should throw exception for all-whitespace password");

        assertThrows(InvalidInputException.class, () ->
                        userDAO.registerUser("validuser6", "A1b".repeat(100)),
                "Should throw exception for overly long password");
    }
}