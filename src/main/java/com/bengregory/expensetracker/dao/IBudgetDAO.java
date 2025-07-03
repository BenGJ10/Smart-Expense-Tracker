package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Budget;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import java.util.List;

public interface IBudgetDAO {
    void addBudget(Budget budget) throws InvalidInputException, DatabaseException;
    void updateBudget(Budget budget) throws InvalidInputException, DatabaseException;
    void deleteBudget(int id) throws DatabaseException;
    List<Budget> getBudgetsByUserId(int userId) throws DatabaseException;
}