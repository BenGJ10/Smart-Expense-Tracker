package com.bengregory.expensetracker.controller;

import com.bengregory.expensetracker.model.Income;
import com.bengregory.expensetracker.model.IncomeSource;
import com.bengregory.expensetracker.service.IIncomeService;
import com.bengregory.expensetracker.service.IncomeService;
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

public class AddIncomeController {
    @FXML private TextField amountField;
    @FXML private ComboBox<IncomeSource> sourceComboBox;
    @FXML private TextField descriptionField;
    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, Number> idColumn;
    @FXML private TableColumn<Income, Number> amountColumn;
    @FXML private TableColumn<Income, String> sourceColumn;
    @FXML private TableColumn<Income, String> dateColumn;
    @FXML private TableColumn<Income, String> descriptionColumn;
    @FXML private TableColumn<Income, Void> actionColumn;
    @FXML private Label errorLabel;
    @FXML private Button addIncomeButton;
    @FXML private Button backButton;
    @FXML private Button clearButton;
    @FXML private GridPane incomeForm;
    private final IIncomeService incomeService = new IncomeService();
    private final CustomLogger logger = CustomLogger.getInstance();
    private Income editingIncome = null;

    @FXML
    public void initialize() {
        sourceComboBox.setItems(FXCollections.observableArrayList(IncomeSource.values()));
        sourceComboBox.setPromptText("Select Source");
        setupTable();
        loadIncome();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()));
        sourceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSource().getDisplayName()) // or .name()
        );
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateTime().toLocalDate().toString()));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    Income income = getTableView().getItems().get(getIndex());
                    amountField.setText(String.valueOf(income.getAmount()));
                    sourceComboBox.setValue(income.getSource());
                    descriptionField.setText(income.getDescription());
                    addIncomeButton.setText("Update Income");
                    editingIncome = income;
                    errorLabel.setText("Edit not implemented yet");
                });
                deleteButton.setOnAction(event -> {
                    Income income = getTableView().getItems().get(getIndex());
                    try {
                        logger.info("Deleting income ID: " + income.getId());
                        incomeService.deleteIncome(income.getId());
                        loadIncome();
                        errorLabel.setText("Income deleted");
                    } catch (DatabaseException e) {
                        logger.error("Failed to delete income ID: " + income.getId(), e);
                        errorLabel.setText("Failed to delete income");
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

    private void loadIncome() {
        try {
            incomeTable.setItems(FXCollections.observableArrayList(incomeService.getIncomeByUser()));
            logger.info("Loaded incomes for user");
        } catch (DatabaseException e) {
            logger.error("Failed to load incomes", e);
            errorLabel.setText("Failed to load incomes");
        }
    }

    @FXML
    private void handleAddIncome() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            IncomeSource selectedSource = sourceComboBox.getValue();
            LocalDateTime date = LocalDateTime.now();
            String description = descriptionField.getText().trim();

            if (amount <= 0 || selectedSource == null) {
                throw new InvalidInputException("Amount must be positive and source cannot be empty");
            }
            if (editingIncome == null) {
                Income income = new Income(0, SessionManager.getInstance().getLoggedInUser().getId(), amount, selectedSource, date, description);
                incomeService.addIncome(income);
                logger.info("Added income: ₹" + amount);
                errorLabel.setText("Income added successfully");
            } else {
                Income updatedIncome = new Income(editingIncome.getId(), SessionManager.getInstance().getLoggedInUser().getId(), amount, selectedSource, date, description);
                incomeService.updateIncome(updatedIncome);
                logger.info("Updated income: ₹" + amount);
                errorLabel.setText("Income updated successfully");
                editingIncome = null;
                addIncomeButton.setText("Add Income");
            }
            clearFields();
            loadIncome();
        } catch (NumberFormatException e) {
            logger.warning("Invalid amount format: " + amountField.getText());
            errorLabel.setText("Invalid amount format");
        } catch (InvalidInputException | DatabaseException e) {
            logger.error("Failed to add income", e);
            errorLabel.setText(e.getMessage());
        }
    }
    @FXML
    private void handleClearForm() {
        clearFields();
        editingIncome = null;
        addIncomeButton.setText("Add Income");
        errorLabel.setText("");
    }

    @FXML
    private void handleBackToDashboard() {
        navigateTo("/com.bengregory.expensetracker.view/dashboard.fxml", "Dashboard");
    }

    private void clearFields() {
        amountField.clear();
        sourceComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
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