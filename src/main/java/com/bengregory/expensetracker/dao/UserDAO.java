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

/*
    UserDAO creates User objects from database queries and passes them to controllers or SessionManager.
    UserDAO implements IUserDAO, providing concrete database operations.
 */

public class UserDAO implements IUserDAO {
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void registerUser(String username, String password) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateUsername(username); // ensure username and password meet requirements
        ValidationUtil.validatePassword(password);

        String hashedPassword = PasswordUtil.hashPassword(password); // Hashes the password.
        // The `?` symbols are placeholders that will be filled later to prevent SQL injection
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); // connection object to database
             PreparedStatement stmt = conn.prepareStatement(sql)) { // precompiled SQL command with placeholders
            stmt.setString(1, username.trim()); // sets username
            stmt.setString(2, hashedPassword);  // sets hashedPassword
            stmt.executeUpdate(); // executes the SQL query
            logger.info("User registered successfully: " + username);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // constraint violation, Duplicate username
                logger.warning("Duplicate username attempted: " + username);
                throw new InvalidInputException("Username already exists");
            }
            logger.error("Failed to register user: " + username, e);
            throw new DatabaseException("Failed to register user", e);
        }
    }
    @Override
    // Returns a User object upon successful login
    public User loginUser(String username, String password) throws InvalidInputException, DatabaseException {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);

        // SQL command to fetch user details from the database by username
        String sql = "SELECT id, username, password FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());

            // ResultSet is a Java object that holds the data retrieved from a database after executing a SQL SELECT query
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { // Checks if a user with that username was found, then it moves the cursor to the first result row.
                String storedHash = rs.getString("password"); // Retrieves the hashed password

                if (PasswordUtil.verifyPassword(password, storedHash)) { // Compares it with the input password
                    logger.info("User logged in: " + username); // Logs successful login
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
    @Override
    public void updateUser(User user) throws InvalidInputException, DatabaseException {
        String username = user.getUsername().trim();
        String password = user.getPassword();

        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);

        String hashedPassword = PasswordUtil.hashPassword(password);

        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setInt(3, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warning("No user found to update with ID: " + user.getId());
                throw new DatabaseException("No user found to update");
            }
            logger.info("Updated user profile: " + username);

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // likely duplicate username
                logger.warning("Duplicate username on update attempt: " + username);
                throw new InvalidInputException("Username already exists");
            }
            logger.error("Failed to update user: " + username, e);
            throw new DatabaseException("Failed to update user profile", e);
        }
    }

}