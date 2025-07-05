package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.dao.IUserDAO;
import com.bengregory.expensetracker.dao.UserDAO;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;
import com.bengregory.expensetracker.util.CustomLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

/*
    Manages the login screen (login.fxml), validates credentials via UserDAO, and sets the logged-in user in SessionManager.
 */

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private IUserDAO userDAO;
    private CustomLogger logger;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        logger = CustomLogger.getInstance();
        logger.info("Login screen initialized");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        try {
            User user = userDAO.loginUser(username, passwordField.getText()); // Calls loginUser to authenticate, receiving a User object
            SessionManager.getInstance().setLoggedInUser(user); // Stores the User object on successful login.
            logger.info("User logged in: " + username);

            // Load dashboard (placeholder for now)
            Parent dashboard = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/dashboard.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboard, 600, 400));
            stage.show();
        } catch (InvalidInputException | DatabaseException e) {
            logger.warning("Login failed for user " + username + ": " + e.getMessage());
            errorLabel.setText(e.getMessage());
        } catch (IOException e) {
            logger.error("Failed to load dashboard", e);
            errorLabel.setText("Failed to load dashboard");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            logger.info("Navigating to registration screen");
            Parent register = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(register, 600, 400));
            stage.show();
        } catch (IOException e) {
            logger.error("Failed to load registration screen", e);
            errorLabel.setText("Failed to load registration screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}