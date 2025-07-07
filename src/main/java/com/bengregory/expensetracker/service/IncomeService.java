package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.dao.IIncomeDAO;
import com.bengregory.expensetracker.dao.IncomeDAO;
import com.bengregory.expensetracker.model.Income;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;

import java.util.List;

public class IncomeService implements IIncomeService {
    private final IIncomeDAO incomeDAO = new IncomeDAO();
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void addIncome(Income income) throws InvalidInputException, DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to add income without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Adding income for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        incomeDAO.addIncome(income);
        logger.info("Income added successfully for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
    }

    @Override
    public List<Income> getIncomeByUser() throws DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to retrieve incomes without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Retrieving incomes for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        List<Income> incomes = incomeDAO.getIncomeByUserId(SessionManager.getInstance().getLoggedInUser().getId());
        logger.info("Retrieved " + incomes.size() + " incomes for user ID: " + SessionManager.getInstance().getLoggedInUser().getId());
        return incomes;
    }

    @Override
    public void updateIncome(Income income) throws InvalidInputException, DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to update income without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Updating income ID: " + income.getId());
        incomeDAO.updateIncome(income);
        logger.info("Income updated successfully: ID " + income.getId());
    }

    @Override
    public void deleteIncome(int incomeId) throws DatabaseException {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            logger.warning("Attempt to delete income without logged-in user");
            throw new DatabaseException("No user logged in");
        }
        logger.info("Deleting income ID: " + incomeId);
        incomeDAO.deleteIncome(incomeId);
        logger.info("Income deleted successfully: ID " + incomeId);
    }

    @Override
    public double getTotalIncome() throws DatabaseException {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getId();
            List<Income> incomes = incomeDAO.getIncomeByUserId(userId);
            double total = incomes.stream().mapToDouble(Income::getAmount).sum();
            logger.info("Calculated total income: ₹" + total + " for user ID: " + userId);
            return total;
        } catch (Exception e) {
            logger.error("Failed to calculate total income", e);
            throw new DatabaseException("Failed to calculate total income: " + e.getMessage());
        }
    }

    @Override
    public List<Income> getRecentIncomes(int limit) throws DatabaseException {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getId();
            List<Income> incomes = incomeDAO.getIncomeByUserId(userId);
            List<Income> recent = incomes.stream()
                    .sorted((i1, i2) -> i2.getDateTime().compareTo(i1.getDateTime()))
                    .limit(limit)
                    .toList();
            logger.info("Retrieved " + recent.size() + " recent incomes for user ID: " + userId);
            return recent;
        } catch (Exception e) {
            logger.error("Failed to retrieve recent incomes", e);
            throw new DatabaseException("Failed to retrieve recent incomes: " + e.getMessage());
        }
    }
}