package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.model.Budget;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;

import java.util.List;
import java.util.Map;

public interface IBudgetService {
    void addBudget(Budget budget) throws InvalidInputException, DatabaseException;
    List<Budget> getBudgetsByUser() throws DatabaseException;
    Map<String, List<String>> checkBudgetAlerts() throws DatabaseException;
    void updateBudget(Budget budget) throws InvalidInputException, DatabaseException;
    void deleteBudget(int budgetId) throws DatabaseException;
}