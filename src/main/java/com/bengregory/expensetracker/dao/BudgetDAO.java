package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Budget;
import com.bengregory.expensetracker.model.ExpenseCategory;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseConnection;
import com.bengregory.expensetracker.util.ValidationUtil;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO implements IBudgetDAO {
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void addBudget(Budget budget) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateAmount(budget.getAmount());
        ValidationUtil.validateCategory(budget.getCategory() != null ? budget.getCategory().name() : null);

        if (budget.getPeriod() == null || budget.getStartDate() == null) {
            throw new InvalidInputException("Period and start date must not be null");
        }

        final String sql = "INSERT INTO budgets (user_id, category, amount, period, start_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, budget.getUserId());
            if (budget.getCategory() != null) {
                stmt.setString(2, budget.getCategory().name());
            } else {
                stmt.setNull(2, Types.VARCHAR); // Nullable category for overall budget
            }
            stmt.setDouble(3, budget.getAmount());
            stmt.setString(4, budget.getPeriod());
            stmt.setObject(5, budget.getStartDate());

            stmt.executeUpdate();
            logger.info("Budget added for user ID: " + budget.getUserId());

        } catch (SQLException e) {
            logger.error("Failed to add budget", e);
            throw new DatabaseException("Failed to add budget", e);
        }
    }

    @Override
    public void updateBudget(Budget budget) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateAmount(budget.getAmount());
        ValidationUtil.validateCategory(budget.getCategory() != null ? budget.getCategory().name() : null);

        if (budget.getPeriod() == null || budget.getStartDate() == null) {
            throw new InvalidInputException("Period and start date must not be null");
        }

        final String sql = "UPDATE budgets SET category = ?, amount = ?, period = ?, start_date = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (budget.getCategory() != null) {
                stmt.setString(1, budget.getCategory().name());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }
            stmt.setDouble(2, budget.getAmount());
            stmt.setString(3, budget.getPeriod());
            stmt.setObject(4, budget.getStartDate());
            stmt.setInt(5, budget.getId());
            stmt.setInt(6, budget.getUserId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.warning("No budget found to update with ID: " + budget.getId());
                throw new DatabaseException("Budget not found or not owned by user");
            }

            logger.info("Budget updated: ID " + budget.getId());

        } catch (SQLException e) {
            logger.error("Failed to update budget ID: " + budget.getId(), e);
            throw new DatabaseException("Failed to update budget", e);
        }
    }

    @Override
    public void deleteBudget(int id) throws DatabaseException {
        int userId = SessionManager.getInstance().getLoggedInUser().getId();
        final String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, userId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.warning("No budget found to delete with ID: " + id);
                throw new DatabaseException("Budget not found or not owned by user");
            }

            logger.info("Budget deleted: ID " + id);

        } catch (SQLException e) {
            logger.error("Failed to delete budget ID: " + id, e);
            throw new DatabaseException("Failed to delete budget", e);
        }
    }

    @Override
    public List<Budget> getBudgetsByUserId(int userId) throws DatabaseException {
        List<Budget> budgets = new ArrayList<>();
        final String sql = "SELECT id, category, amount, period, start_date FROM budgets WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                budgets.add(new Budget(
                        rs.getInt("id"),
                        userId,
                        rs.getString("category") != null ? ExpenseCategory.valueOf(rs.getString("category")) : null,
                        rs.getDouble("amount"),
                        rs.getString("period"),
                        rs.getObject("start_date", LocalDate.class)
                ));
            }

            logger.info("Retrieved " + budgets.size() + " budgets for user ID: " + userId);
            return budgets;

        } catch (SQLException | InvalidInputException e) {
            logger.error("Failed to retrieve budgets for user ID: " + userId, e);
            throw new DatabaseException("Failed to retrieve budgets", e);
        }
    }
}