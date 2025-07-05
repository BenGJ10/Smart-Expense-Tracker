package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.model.ExpenseCategory;
import com.bengregory.expensetracker.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO implements IExpenseDAO {
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void addExpense(Expense expense) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateAmount(expense.getAmount());
        ValidationUtil.validateCategory(expense.getCategory().getDisplayName());
        ValidationUtil.validateDescription(expense.getDescription());

        if (expense.getDateTime() == null)
            throw new InvalidInputException("Date cannot be null");

        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to add expense without logged-in user");
            throw new DatabaseException("No user logged in");
        }

        final String sql = "INSERT INTO expenses (user_id, amount, category, date_time, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, SessionManager.getInstance().getLoggedInUser().getId());
            stmt.setDouble(2, expense.getAmount());
            stmt.setString(3, expense.getCategory().name());
            stmt.setObject(4, expense.getDateTime());
            stmt.setString(5, expense.getDescription());

            stmt.executeUpdate();
            logger.info("Expense added for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());

        } catch (SQLException e) {
            logger.error("Failed to add expense", e);
            throw new DatabaseException("Failed to add expense", e);
        }
    }

    @Override
    public List<Expense> getExpensesByUserId(int userId) throws DatabaseException {
        List<Expense> expenses = new ArrayList<>();
        final String sql = "SELECT id, amount, category, date_time, description FROM expenses WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getInt("id"),
                        userId,
                        rs.getDouble("amount"),
                        ExpenseCategory.valueOf(rs.getString("category")),
                        rs.getObject("date_time", LocalDateTime.class),
                        rs.getString("description")
                ));
            }

            logger.info("Retrieved " + expenses.size() + " expenses for user ID: " + userId);
            return expenses;

        } catch (SQLException | InvalidInputException e) {
            logger.error("Failed to retrieve expenses for user ID: " + userId, e);
            throw new DatabaseException("Failed to retrieve expenses", e);
        }
    }

    @Override
    public void updateExpense(Expense expense) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateAmount(expense.getAmount());
        ValidationUtil.validateCategory(expense.getCategory().getDisplayName());
        ValidationUtil.validateDescription(expense.getDescription());

        if (expense.getDateTime() == null)
            throw new InvalidInputException("Date cannot be null");

        final String sql = "UPDATE expenses SET amount = ?, category = ?, date_time = ?, description = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, expense.getAmount());
            stmt.setString(2, expense.getCategory().name()); // Use enum name
            stmt.setObject(3, expense.getDateTime());
            stmt.setString(4, expense.getDescription());
            stmt.setInt(5, expense.getId());
            stmt.setInt(6, SessionManager.getInstance().getLoggedInUser().getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.warning("No expense found to update with ID: " + expense.getId());
                throw new DatabaseException("Expense not found or not owned by user");
            }

            logger.info("Expense updated: ID " + expense.getId());

        } catch (SQLException e) {
            logger.error("Failed to update expense ID: " + expense.getId(), e);
            throw new DatabaseException("Failed to update expense", e);
        }
    }

    @Override
    public void deleteExpense(int expenseId) throws DatabaseException {
        final String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, expenseId);
            stmt.setInt(2, SessionManager.getInstance().getLoggedInUser().getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.warning("No expense found to delete with ID: " + expenseId);
                throw new DatabaseException("Expense not found or not owned by user");
            }

            logger.info("Expense deleted: ID " + expenseId);

        } catch (SQLException e) {
            logger.error("Failed to delete expense ID: " + expenseId, e);
            throw new DatabaseException("Failed to delete expense", e);
        }
    }
}
