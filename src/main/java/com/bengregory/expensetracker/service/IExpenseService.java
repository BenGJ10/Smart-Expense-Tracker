package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;

import java.util.List;

public interface IExpenseService {
    void addExpense(Expense expense) throws InvalidInputException, DatabaseException;
    List<Expense> getExpensesByUser() throws DatabaseException;
    void updateExpense(Expense expense) throws InvalidInputException, DatabaseException;
    void deleteExpense(int expenseId) throws DatabaseException;
    double getTotalExpenses() throws DatabaseException;
    List<Expense> getRecentExpenses(int limit) throws DatabaseException;
}