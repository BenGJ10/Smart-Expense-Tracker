package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.dao.ExpenseDAO;
import com.bengregory.expensetracker.dao.IExpenseDAO;
import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;

import java.util.List;

public class ExpenseService implements IExpenseService {
    private final IExpenseDAO expenseDAO = new ExpenseDAO();
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void addExpense(Expense expense) throws InvalidInputException, DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to add expense without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Adding expense for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        expenseDAO.addExpense(expense);
        logger.info("Expense added successfully for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
    }

    @Override
    public List<Expense> getExpensesByUser() throws DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to retrieve expenses without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Retrieving expenses for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        List<Expense> expenses = expenseDAO.getExpensesByUserId(SessionManager.getInstance().getLoggedInUser().getId());
        logger.info("Retrieved " + expenses.size() + " expenses for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        return expenses;
    }

    @Override
    public void updateExpense(Expense expense) throws InvalidInputException, DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to update expense without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Updating expense ID: " + expense.getId());
        expenseDAO.updateExpense(expense);
        logger.info("Expense updated successfully: ID " + expense.getId());
    }

    @Override
    public void deleteExpense(int expenseId) throws DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to delete expense without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Deleting expense ID: " + expenseId);
        expenseDAO.deleteExpense(expenseId);
        logger.info("Expense deleted successfully: ID " + expenseId);
    }
}