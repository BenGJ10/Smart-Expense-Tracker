package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.service.IUserService;
import com.bengregory.expensetracker.service.UserService;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private final IUserService userService = new UserService();
    private final CustomLogger logger = CustomLogger.getInstance();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        try {
            logger.info("Login attempt for user: " + username);
            User user = userService.loginUser(username, password);
            errorLabel.setText("Login successful!");
            logger.info("Navigating to dashboard for user: " + username);
            Parent dashboard = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/dashboard.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboard, 800, 600));
            stage.show();
        } catch (InvalidInputException e) {
            errorLabel.setText(e.getMessage());
            logger.warning("Login failed: " + e.getMessage());
        } catch (DatabaseException e) {
            errorLabel.setText("Database error occurred");
            logger.error("Database error during login for user: " + username, e);
        } catch (IOException e) {
            errorLabel.setText("Failed to load dashboard");
            logger.error("Failed to load dashboard for user: " + username, e);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            logger.info("Navigating to registration screen");
            Parent register = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(register, 800, 600));
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Failed to load registration screen");
            logger.error("Failed to load registration screen", e);
        }
    }
}