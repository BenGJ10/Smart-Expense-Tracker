package com.bengregory.expensetracker.dao;
import com.bengregory.expensetracker.util.DatabaseConnection;
import com.bengregory.expensetracker.util.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class UserDAODeletionTest {
    public static void deleteTestUsers() throws DatabaseException {
        executeCleanup("DELETE FROM users WHERE username LIKE 'testuser%'");
    }

    public static void deleteTestExpenses() throws DatabaseException {
        executeCleanup("DELETE FROM expenses WHERE user_id IN (SELECT id FROM users WHERE username LIKE 'testuser%')");
    }

    public static void deleteTestIncomes() throws DatabaseException {
        executeCleanup("DELETE FROM income WHERE user_id IN (SELECT id FROM users WHERE username LIKE 'testuser%')");
    }

    public static void deleteTestBudgets() throws DatabaseException {
        executeCleanup("DELETE FROM budgets WHERE user_id IN (SELECT id FROM users WHERE username LIKE 'testuser%')");
    }

    private static void executeCleanup(String sql) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Test cleanup failed: " + e.getMessage(), e);
        }
    }
}
