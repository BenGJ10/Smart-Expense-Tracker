package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.dao.IUserDAO;
import com.bengregory.expensetracker.dao.UserDAO;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;
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
    private IUserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        try {
            User user = userDAO.loginUser(usernameField.getText(), passwordField.getText());
            SessionManager.getInstance().setLoggedInUser(user);
            // Load dashboard (placeholder for now)
            Parent dashboard = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/dashboard.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboard, 600, 400));
            stage.show();
        } catch (InvalidInputException | DatabaseException e) {
            errorLabel.setText(e.getMessage());
        } catch (IOException e) {
            errorLabel.setText("Failed to load dashboard");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Parent register = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(register, 600, 400));
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Failed to load registration screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}