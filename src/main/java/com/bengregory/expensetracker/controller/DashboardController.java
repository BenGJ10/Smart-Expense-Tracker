package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML private Label welcomeLabel;
    private final CustomLogger logger = CustomLogger.getInstance();

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername());
            logger.info("Dashboard initialized for user: " + user.getUsername());
        } else {
            logger.warning("Dashboard accessed without logged-in user");
            welcomeLabel.setText("No user logged in");
        }
    }

    @FXML
    private void handleLogout() {
        String username = SessionManager.getInstance().getLoggedInUser() != null ?
                SessionManager.getInstance().getLoggedInUser().getUsername() : "unknown";
        logger.info("User logged out: " + username);
        SessionManager.getInstance().logout();
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(login, 600, 400));
            stage.show();
        } catch (IOException e) {
            logger.error("Failed to load login screen", e);
            welcomeLabel.setText("Failed to load login screen");
        }
    }
}