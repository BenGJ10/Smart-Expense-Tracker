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

    @Override
    public double getTotalExpenses() throws DatabaseException {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getId();
            List<Expense> expenses = expenseDAO.getExpensesByUserId(userId);
            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            logger.info("Calculated total expenses: ₹" + total + " for user ID: " + userId);
            return total;
        } catch (Exception e) {
            logger.error("Failed to calculate total expenses", e);
            throw new DatabaseException("Failed to calculate total expenses: " + e.getMessage());
        }
    }

    @Override
    public List<Expense> getRecentExpenses(int limit) throws DatabaseException {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getId();
            List<Expense> expenses = expenseDAO.getExpensesByUserId(userId);
            List<Expense> recent = expenses.stream()
                    .sorted((e1, e2) -> e2.getDateTime().compareTo(e1.getDateTime()))
                    .limit(limit)
                    .toList();
            logger.info("Retrieved " + recent.size() + " recent expenses for user ID: " + userId);
            return recent;
        } catch (Exception e) {
            logger.error("Failed to retrieve recent expenses", e);
            throw new DatabaseException("Failed to retrieve recent expenses: " + e.getMessage());
        }
    }
}