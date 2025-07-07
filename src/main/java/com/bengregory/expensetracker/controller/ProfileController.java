package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.service.IUserService;
import com.bengregory.expensetracker.service.UserService;
import com.bengregory.expensetracker.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {
    @FXML private TextField usernameField;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private final IUserService userService = new UserService();
    private final CustomLogger logger = CustomLogger.getInstance();

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {
            try {
                User currentUser = userService.getUserById(user.getId());
                usernameField.setText(currentUser.getUsername());
                logger.info("Loaded profile for user: " + currentUser.getUsername());
            } catch (DatabaseException e) {
                logger.error("Failed to load user profile", e);
                errorLabel.setText("Failed to load profile");
            }
        } else {
            logger.warning("Profile accessed without logged-in user");
            errorLabel.setText("No user logged in");
        }
    }

    @FXML
    private void handleSaveProfile() {
        try {
            User user = SessionManager.getInstance().getLoggedInUser();
            if (user == null) {
                throw new DatabaseException("No user logged in");
            }

            String username = usernameField.getText().trim();
            String oldPassword = oldPasswordField.getText();
            String newPassword = passwordField.getText();

            if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
                throw new InvalidInputException("Fields cannot be empty");
            }

            User currentUser = userService.getUserById(user.getId());

            if (!PasswordUtil.verifyPassword(oldPassword, currentUser.getPassword())) {
                throw new InvalidInputException("Old password is incorrect");
            }

            if (oldPassword.equals(newPassword)) {
                throw new InvalidInputException("New password cannot be the same as old password");
            }

            currentUser.setUsername(username);
            currentUser.setPassword(newPassword);
            userService.updateUser(currentUser);
            errorLabel.setText("Profile updated successfully");
            logger.info("Profile updated for user: " + username);

        } catch (InvalidInputException | DatabaseException e) {
            logger.error("Failed to save profile", e);
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBackToDashboard() {
        navigateTo("/com.bengregory.expensetracker.view/dashboard.fxml", "Dashboard");
    }

    private void navigateTo(String fxmlPath, String logMessage) {
        try {
            logger.info("Navigating to " + logMessage);
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            logger.error("Failed to load " + fxmlPath, e);
            errorLabel.setText("Failed to load " + logMessage.toLowerCase() + " page");
        }
    }
}