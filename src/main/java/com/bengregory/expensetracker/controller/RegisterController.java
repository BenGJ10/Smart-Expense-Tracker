package com.bengregory.expensetracker.controller;

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

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private final IUserService userService = new UserService();
    private final CustomLogger logger = CustomLogger.getInstance();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        try {
            logger.info("Registration attempt for user: " + username);
            userService.registerUser(username, password);
            errorLabel.setText("Registration successful!");
            logger.info("Navigating to login screen after registration");
            Parent login = FXMLLoader.load(getClass().getResource("/com/bengregory/expensetracker/view/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(login, 600, 400));
            stage.show();
        } catch (InvalidInputException e) {
            errorLabel.setText(e.getMessage());
            logger.warning("Registration failed: " + e.getMessage());
        } catch (DatabaseException e) {
            errorLabel.setText("Database error occurred");
            logger.error("Database error during registration for user: " + username, e);
        } catch (IOException e) {
            errorLabel.setText("Failed to load login screen");
            logger.error("Failed to load login screen for user: " + username, e);
        }
    }

    @FXML
    private void handleBack() {
        try {
            logger.info("Navigating to login screen");
            Parent login = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(login, 600, 400));
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Failed to load login screen");
            logger.error("Failed to load login screen", e);
        }
    }
}