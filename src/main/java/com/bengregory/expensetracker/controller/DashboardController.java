package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.model.Income;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.service.IExpenseService;
import com.bengregory.expensetracker.service.IIncomeService;
import com.bengregory.expensetracker.service.IBudgetService;
import com.bengregory.expensetracker.service.ExpenseService;
import com.bengregory.expensetracker.service.IncomeService;
import com.bengregory.expensetracker.service.BudgetService;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label currentBalanceLabel;
    @FXML private Label activity1;
    @FXML private Label activity2;
    @FXML private Label activity3;
    @FXML private Label errorLabel;
    @FXML private Label budgetAlertLabel;
    private final IIncomeService incomeService = new IncomeService();
    private final IExpenseService expenseService = new ExpenseService();
    private final IBudgetService budgetService = new BudgetService();
    private final CustomLogger logger = CustomLogger.getInstance();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
            double totalIncome = incomeService.getTotalIncome();
            double totalExpense = expenseService.getTotalExpenses();
            double balance = totalIncome - totalExpense;
            totalIncomeLabel.setText(String.format("₹%.2f", totalIncome));
            totalExpenseLabel.setText(String.format("₹%.2f", totalExpense));
            currentBalanceLabel.setText(String.format("₹%.2f", balance));

            List<Object> transactions = new ArrayList<>();
            transactions.addAll(incomeService.getRecentIncomes(3));
            transactions.addAll(expenseService.getRecentExpenses(3));
            transactions.sort(Comparator.comparing(o -> {
                if (o instanceof Income) return ((Income) o).getDateTime();
                return ((Expense) o).getDateTime();
            }, Comparator.reverseOrder()));
            List<Object> recent = transactions.stream().limit(3).toList();

            Label[] activityLabels = {activity1, activity2, activity3};
            for (int i = 0; i < activityLabels.length; i++) {
                if (i < recent.size()) {
                    Object transaction = recent.get(i);
                    if (transaction instanceof Income income) {
                        activityLabels[i].setText(String.format("Income: ₹%.2f (%s) on %s",
                                income.getAmount(), income.getSource().getDisplayName(), income.getDateTime().toLocalDate().format(formatter)));
                    } else if (transaction instanceof Expense expense) {
                        activityLabels[i].setText(String.format("Expense: ₹%.2f (%s) on %s",
                                expense.getAmount(), expense.getCategory().getDisplayName(), expense.getDateTime().toLocalDate().format(formatter)));
                    }
                } else {
                    activityLabels[i].setText("-");
                }
            }

            Map<String, List<String>> alerts = budgetService.checkBudgetAlerts();
            List<String> allAlerts = new ArrayList<>();
            if (alerts.containsKey("MONTHLY")) {
                allAlerts.addAll(alerts.get("MONTHLY"));
            }
            if (alerts.containsKey("WEEKLY")) {
                allAlerts.addAll(alerts.get("WEEKLY"));
            }
            if (!allAlerts.isEmpty()) {
                budgetAlertLabel.setText(String.join("; ", allAlerts));
            } else {
                budgetAlertLabel.setText("");
            }
        } catch (DatabaseException e) {
            logger.error("Failed to update dashboard summaries", e);
            errorLabel.setText("Error loading summaries or alerts");
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
        navigateTo("/com.bengregory.expensetracker.view/add_expense.fxml", "Add Expense");
    }

    @FXML
    private void handleAddBudget() {
        navigateTo("/com.bengregory.expensetracker.view/add_budget.fxml", "Add Budget");
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