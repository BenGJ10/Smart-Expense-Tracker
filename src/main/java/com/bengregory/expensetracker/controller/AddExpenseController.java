package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.model.ExpenseCategory;
import com.bengregory.expensetracker.service.IExpenseService;
import com.bengregory.expensetracker.service.ExpenseService;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class AddExpenseController {
    @FXML private TextField amountField;
    @FXML private ComboBox<ExpenseCategory> categoryComboBox;
    @FXML private TextField dateField;
    @FXML private TextField descriptionField;
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, Number> idColumn;
    @FXML private TableColumn<Expense, Number> amountColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private TableColumn<Expense, Void> actionColumn;
    @FXML private Label errorLabel;
    @FXML private Button addExpenseButton;
    @FXML private Button backButton;
    @FXML private GridPane expenseForm;
    private final IExpenseService expenseService = new ExpenseService();
    private final CustomLogger logger = CustomLogger.getInstance();

    @FXML
    public void initialize() {
        categoryComboBox.setItems(FXCollections.observableArrayList(ExpenseCategory.values()));
        categoryComboBox.setPromptText("Select Source");
        setupTable();
        loadExpenses();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()));
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getDisplayName()) // or .name()
        );
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateTime().toLocalDate().toString()));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    // Placeholder: Implement edit functionality
                    errorLabel.setText("Edit not implemented yet");
                });
                deleteButton.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    try {
                        logger.info("Deleting expense ID: " + expense.getId());
                        expenseService.deleteExpense(expense.getId());
                        loadExpenses();
                        errorLabel.setText("Expense deleted");
                    } catch (DatabaseException e) {
                        logger.error("Failed to delete expense ID: " + expense.getId(), e);
                        errorLabel.setText("Failed to delete expense");
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

    private void loadExpenses() {
        try {
            expenseTable.setItems(FXCollections.observableArrayList(expenseService.getExpensesByUser()));
            logger.info("Loaded expenses for user");
        } catch (DatabaseException e) {
            logger.error("Failed to load expenses", e);
            errorLabel.setText("Failed to load expenses");
        }
    }

    @FXML
    private void handleAddExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            ExpenseCategory category = categoryComboBox.getValue();
            LocalDateTime date = LocalDateTime.now();
            String description = descriptionField.getText().trim();
            if (amount <= 0 || category == null) {
                throw new InvalidInputException("Amount must be positive and category cannot be empty");
            }
            Expense expense = new Expense(0, SessionManager.getInstance().getLoggedInUser().getId(), amount, category, date, description);
            expenseService.addExpense(expense);
            logger.info("Added expense: ₹" + amount);
            errorLabel.setText("Expense added successfully");
            clearFields();
            loadExpenses();
        } catch (NumberFormatException e) {
            logger.warning("Invalid amount format: " + amountField.getText());
            errorLabel.setText("Invalid amount format");
        } catch (DateTimeParseException e) {
            logger.warning("Invalid date format: " + dateField.getText());
            errorLabel.setText("Invalid date format (YYYY-MM-DD)");
        } catch (InvalidInputException | DatabaseException e) {
            logger.error("Failed to add expense", e);
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
        dateField.clear();
    }

    private void navigateTo(String fxmlPath, String logMessage) {
        try {
            logger.info("Navigating to " + logMessage);
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            logger.error("Failed to load: " + fxmlPath, e);
            errorLabel.setText("Failed to load " + logMessage.toLowerCase() + " page");
        }
    }
}