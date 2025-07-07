package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.service.IExpenseService;
import com.bengregory.expensetracker.service.IIncomeService;
import com.bengregory.expensetracker.service.ExpenseService;
import com.bengregory.expensetracker.service.IncomeService;
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
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label currentBalanceLabel;
    @FXML private Label activity1;
    @FXML private Label activity2;
    @FXML private Label activity3;
    @FXML private Label errorLabel;
    private final IIncomeService incomeService = new IncomeService();
    private final IExpenseService expenseService = new ExpenseService();
    private final CustomLogger logger = CustomLogger.getInstance();

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername());
            logger.info("Dashboard initialized for user: " + user.getUsername());
            updateSummary(); // Placeholder for summary calculations
        } else {
            logger.warning("Dashboard accessed without logged-in user");
            errorLabel.setText("No user logged in");
        }
    }

    private void updateSummary() {
        try {
            // Placeholder: Replace with actual service calls
            double totalIncome = 0.0; // incomeService.getTotalIncome();
            double totalExpense = 0.0; // expenseService.getTotalExpenses();
            double balance = totalIncome - totalExpense;
            totalIncomeLabel.setText(String.format("₹%.2f", totalIncome));
            totalExpenseLabel.setText(String.format("₹%.2f", totalExpense));
            currentBalanceLabel.setText(String.format("₹%.2f", balance));
            // Placeholder: Update activity labels with recent transactions
            activity1.setText("Recent: Added income ₹1000");
            activity2.setText("Recent: Added expense ₹500");
            activity3.setText("Recent: Set budget ₹2000");
        } catch (Exception e) {
            logger.error("Failed to update dashboard summaries", e);
            errorLabel.setText("Error loading summaries");
        }
    }

    @FXML
    private void handleAddIncome() {
        navigateTo("/com.bengregory.expensetracker.view/add_income.fxml", "Add Income");
    }

    @FXML
    private void handleAddExpense() {
        navigateTo("/com.bengregory.expensetracker.view.add_expense.fxml", "Add Expense");
    }

    @FXML
    private void handleAddBudget() {
        navigateTo("/com.bengregory.expensetracker.view.add_budget.fxml", "Add Budget");
    }

    @FXML
    private void handleViewProfile() {
        navigateTo("/com.bengregory.expensetracker.view/profile.fxml", "View Profile");
    }

    @FXML
    private void handleLogout() {
        String username = SessionManager.getInstance().getLoggedInUser() != null ?
                SessionManager.getInstance().getLoggedInUser().getUsername() : "unknown";
        logger.info("User logged out: " + username);
        SessionManager.getInstance().logout();
        navigateTo("/com.bengregory.expensetracker.view/login.fxml", "Login");
    }

    private void navigateTo(String fxmlPath, String logMessage) {
        try {
            logger.info("Navigating to " + logMessage);
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            logger.error("Failed to load " + fxmlPath, e);
            errorLabel.setText("Failed to load " + logMessage.toLowerCase() + " page");
        }
    }
}