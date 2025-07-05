package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Income;
import com.bengregory.expensetracker.model.IncomeSource;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseConnection;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;
import com.bengregory.expensetracker.util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncomeDAO implements IIncomeDAO {
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void addIncome(Income income) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateAmount(income.getAmount());
        ValidationUtil.validateCategory(income.getSource().getDisplayName());
        if (income.getDateTime() == null) throw new InvalidInputException("Date cannot be null");
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to add income without logged-in user");
            throw new DatabaseException("No user logged in");
        }

        String sql = "INSERT INTO income (user_id, amount, source, date_time, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, SessionManager.getInstance().getLoggedInUser().getId());
            stmt.setDouble(2, income.getAmount());
            stmt.setString(3, income.getSource().getDisplayName());
            stmt.setObject(4, income.getDateTime());
            stmt.setString(5, income.getDescription());
            stmt.executeUpdate();
            logger.info("Income added for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        } catch (SQLException e) {
            logger.error("Failed to add income", e);
            throw new DatabaseException("Failed to add income", e);
        }
    }

    @Override
    public List<Income> getIncomeByUserId(int userId) throws DatabaseException {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT id, amount, source, date_time, description FROM income WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                incomes.add(new Income(
                        rs.getInt("id"),
                        userId,
                        rs.getDouble("amount"),
                        IncomeSource.valueOf(rs.getString("source").toUpperCase()),
                        rs.getObject("date_time", LocalDateTime.class),
                        rs.getString("description")
                ));
            }
            logger.info("Retrieved " + incomes.size() + " incomes for user ID: " + userId);
            return incomes;
        } catch (SQLException e) {
            logger.error("Failed to retrieve incomes for user ID: " + userId, e);
            throw new DatabaseException("Failed to retrieve incomes", e);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateIncome(Income income) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateAmount(income.getAmount());
        ValidationUtil.validateCategory(income.getSource().getDisplayName());
        if (income.getDateTime() == null) throw new InvalidInputException("Date cannot be null");

        String sql = "UPDATE income SET amount = ?, source = ?, date_time = ?, description = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, income.getAmount());
            stmt.setString(2, income.getSource().getDisplayName());
            stmt.setObject(3, income.getDateTime());
            stmt.setString(4, income.getDescription());
            stmt.setInt(5, income.getId());
            stmt.setInt(6, SessionManager.getInstance().getLoggedInUser().getId());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.warning("No income found to update with ID: " + income.getId());
                throw new DatabaseException("Income not found or not owned by user");
            }
            logger.info("Income updated: ID " + income.getId());
        } catch (SQLException e) {
            logger.error("Failed to update income ID: " + income.getId(), e);
            throw new DatabaseException("Failed to update income", e);
        }
    }

    @Override
    public void deleteIncome(int incomeId) throws DatabaseException {
        String sql = "DELETE FROM income WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, incomeId);
            stmt.setInt(2, SessionManager.getInstance().getLoggedInUser().getId());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.warning("No income found to delete with ID: " + incomeId);
                throw new DatabaseException("Income not found or not owned by user");
            }
            logger.info("Income deleted: ID " + incomeId);
        } catch (SQLException e) {
            logger.error("Failed to delete income ID: " + incomeId, e);
            throw new DatabaseException("Failed to delete income", e);
        }
    }
}