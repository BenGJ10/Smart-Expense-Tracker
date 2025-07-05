package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseConnection;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.PasswordUtil;
import com.bengregory.expensetracker.util.ValidationUtil;
import com.bengregory.expensetracker.util.CustomLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO implements IUserDAO {
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void registerUser(String username, String password) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);

        String hashedPassword = PasswordUtil.hashPassword(password);
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            logger.info("User registered successfully: " + username);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Duplicate username
                logger.warning("Duplicate username attempted: " + username);
                throw new InvalidInputException("Username already exists");
            }
            logger.error("Failed to register user: " + username, e);
            throw new DatabaseException("Failed to register user", e);
        }
    }
    @Override
    public User loginUser(String username, String password) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);

        String sql = "SELECT id, username, password FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    logger.info("User logged in: " + username);
                    return new User(rs.getInt("id"), rs.getString("username"), storedHash);
                }
            }
            logger.warning("Invalid login attempt for user: " + username);
            throw new InvalidInputException("Invalid username or password");
        } catch (SQLException e) {
            logger.error("Failed to login user: " + username, e);
            throw new DatabaseException("Failed to login user", e);
        }
    }

    @Override
    public User getUserById(int id) throws DatabaseException {
        String sql = "SELECT id, username, password FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                logger.info("Retrieved user by ID: " + id);
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
            }
            logger.warning("User not found for ID: " + id);
            throw new DatabaseException("User with ID " + id + " not found");
        } catch (SQLException e) {
            logger.error("Failed to retrieve user by ID: " + id, e);
            throw new DatabaseException("Failed to retrieve user", e);
        } catch (InvalidInputException e) {
            logger.error("Invalid user data for ID: " + id, e);
            throw new DatabaseException("Invalid user data", e);
        }
    }
}