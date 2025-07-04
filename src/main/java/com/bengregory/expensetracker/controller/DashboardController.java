package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.bengregory.expensetracker.model.User;

import java.io.IOException;

public class DashboardController {
    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername());
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(login, 600, 400));
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Failed to load login screen");
        }
    }
}