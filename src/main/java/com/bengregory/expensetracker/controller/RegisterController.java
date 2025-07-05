package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.dao.IUserDAO;
import com.bengregory.expensetracker.dao.UserDAO;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
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

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private IUserDAO userDAO;
    private CustomLogger logger;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        logger = CustomLogger.getInstance();
        logger.info("Register screen initialized");
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        try {
            userDAO.registerUser(username, passwordField.getText());
            logger.info("User registered: " + username);
            errorLabel.setText("Registration successful! Please login.");
            // Optionally switch to login screen
            handleBack();
        } catch (InvalidInputException | DatabaseException e) {
            logger.warning("Registration failed for user " + username + ": " + e.getMessage());
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            logger.info("Navigating back to login screen");
            Parent login = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(login, 600, 400));
            stage.show();
        } catch (IOException e) {
            logger.error("Failed to load login screen", e);
            errorLabel.setText("Failed to load login screen");
        }
    }
}