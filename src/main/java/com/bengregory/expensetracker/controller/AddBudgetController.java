package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.Budget;
import com.bengregory.expensetracker.model.ExpenseCategory;
import com.bengregory.expensetracker.service.BudgetService;
import com.bengregory.expensetracker.service.IBudgetService;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AddBudgetController {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> periodComboBox;
    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, Number> idColumn;
    @FXML private TableColumn<Budget, Number> amountColumn;
    @FXML private TableColumn<Budget, String> categoryColumn;
    @FXML private TableColumn<Budget, String> periodColumn;
    @FXML private TableColumn<Budget, Void> actionColumn;
    @FXML private Label errorLabel;

    private final IBudgetService budgetService = new BudgetService();
    private final CustomLogger logger = CustomLogger.getInstance();

    @FXML
    public void initialize() {
        setupTable();
        initializeComboBoxes();
        loadBudgets();
    }

    private void initializeComboBoxes() {
        categoryComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(ExpenseCategory.values())
                        .map(Enum::name)
                        .collect(Collectors.toList())
        ));

        periodComboBox.setItems(FXCollections.observableArrayList("WEEKLY", "MONTHLY"));
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getDisplayName()));
        periodColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPeriod()));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    Budget budget = getTableView().getItems().get(getIndex());
                    errorLabel.setText("Edit not implemented yet");
                });

                deleteButton.setOnAction(event -> {
                    Budget budget = getTableView().getItems().get(getIndex());
                    try {
                        logger.info("Deleting budget ID: " + budget.getId());
                        budgetService.deleteBudget(budget.getId());
                        loadBudgets();
                        errorLabel.setText("Budget deleted");
                    } catch (DatabaseException e) {
                        logger.error("Failed to delete budget ID: " + budget.getId(), e);
                        errorLabel.setText("Failed to delete budget");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadBudgets() {
        try {
            budgetTable.setItems(FXCollections.observableArrayList(budgetService.getBudgetsByUser()));
            logger.info("Loaded budgets for user");
        } catch (DatabaseException e) {
            logger.error("Failed to load budgets", e);
            errorLabel.setText("Failed to load budgets");
        }
    }

    @FXML
    private void handleAddBudget() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            String categoryInput = categoryComboBox.getValue();
            String period = periodComboBox.getValue();

            if (amount <= 0 || categoryInput == null || period == null) {
                throw new InvalidInputException("All fields are required");
            }

            ExpenseCategory category;
            try {
                category = ExpenseCategory.valueOf(categoryInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid category: " + categoryInput);
            }

            Budget budget = new Budget(
                    0,
                    SessionManager.getInstance().getLoggedInUser().getId(),
                    category,
                    amount,
                    period,
                    LocalDate.now()
            );

            budgetService.addBudget(budget);
            logger.info("Budget added: ₹" + amount);
            errorLabel.setText("Budget added successfully");
            clearFields();
            loadBudgets();

        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a valid number");
        } catch (InvalidInputException | DatabaseException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBackToDashboard() {
        navigateTo("/com.bengregory.expensetracker.view/dashboard.fxml", "Dashboard");
    }

    private void clearFields() {
        amountField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        periodComboBox.getSelectionModel().clearSelection();
    }

    private void navigateTo(String fxmlPath, String logMessage) {
        try {
            logger.info("Navigating to " + logMessage);
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            logger.error("Failed to load " + fxmlPath, e);
            errorLabel.setText("Failed to load " + logMessage.toLowerCase() + " page");
        }
    }
}
