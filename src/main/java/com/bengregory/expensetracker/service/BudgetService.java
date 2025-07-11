package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.dao.BudgetDAO;
import com.bengregory.expensetracker.dao.IBudgetDAO;
import com.bengregory.expensetracker.model.Budget;
import com.bengregory.expensetracker.dao.ExpenseDAO;
import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.model.ExpenseCategory;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;

import java.util.List;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetService implements IBudgetService {
    private final IBudgetDAO budgetDAO = new BudgetDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
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

    @Override
    public Map<String, List<String>> checkBudgetAlerts() throws DatabaseException {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getId();
            List<Budget> budgets = budgetDAO.getBudgetsByUserId(userId);
            List<Expense> expenses = expenseDAO.getExpensesByUserId(userId);
            Map<String, List<String>> alerts = new HashMap<>();

            LocalDate now = LocalDate.now();
            LocalDate monthStart = now.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

            for (Budget budget : budgets) {
                ExpenseCategory category = budget.getCategory();
                String period = budget.getPeriod();
                double budgetAmount = budget.getAmount();

                List<Expense> relevantExpenses = expenses.stream()
                        .filter(expense -> expense.getCategory() == category)
                        .filter(expense -> {
                            LocalDate expenseDate = expense.getDateTime().toLocalDate();
                            if (period.equals("MONTHLY")) {
                                return expenseDate.isAfter(monthStart.minusDays(1)) && expenseDate.isBefore(now.plusDays(1));
                            } else if (period.equals("WEEKLY")) {
                                return expenseDate.isAfter(weekStart.minusDays(1)) && expenseDate.isBefore(now.plusDays(1));
                            }
                            return false;
                        })
                        .toList();

                double totalSpent = relevantExpenses.stream().mapToDouble(Expense::getAmount).sum();
                if (totalSpent > budgetAmount) {
                    String alert = String.format("Budget exceeded for %s (%s): ₹%.2f/₹%.2f",
                            category.getDisplayName(), period, totalSpent, budgetAmount);
                    alerts.computeIfAbsent(period, k -> new ArrayList<>()).add(alert);
                }
            }

            logger.info("Generated budget alerts for user ID: " + userId);
            return alerts;
        } catch (Exception e) {
            logger.error("Failed to check budget alerts", e);
            throw new DatabaseException("Failed to check budget alerts: " + e.getMessage());
        }
    }
}