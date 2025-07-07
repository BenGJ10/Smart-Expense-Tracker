package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.dao.BudgetDAO;
import com.bengregory.expensetracker.dao.IBudgetDAO;
import com.bengregory.expensetracker.model.Budget;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;

import java.util.List;

public class BudgetService implements IBudgetService {
    private final IBudgetDAO budgetDAO = new BudgetDAO();
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void addBudget(Budget budget) throws InvalidInputException, DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to add budget without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Adding budget for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        budgetDAO.addBudget(budget);
        logger.info("Budget added successfully for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
    }

    @Override
    public List<Budget> getBudgetsByUser() throws DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to retrieve budgets without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Retrieving budgets for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        List<Budget> budgets = budgetDAO.getBudgetsByUserId(SessionManager.getInstance().getLoggedInUser().getId());
        logger.info("Retrieved " + budgets.size() + " budgets for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        return budgets;
    }

    @Override
    public void updateBudget(Budget budget) throws InvalidInputException, DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to update budget without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Updating budget ID: " + budget.getId());
        budgetDAO.updateBudget(budget);
        logger.info("Budget updated successfully: ID " + budget.getId());
    }

    @Override
    public void deleteBudget(int budgetId) throws DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to delete budget without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Deleting budget ID: " + budgetId);
        budgetDAO.deleteBudget(budgetId);
        logger.info("Budget deleted successfully: ID " + budgetId);
    }
}