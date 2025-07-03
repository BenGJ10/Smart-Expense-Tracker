package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Expense;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import java.util.List;

public interface IExpenseDAO {
    void addExpense(Expense expense) throws InvalidInputException, DatabaseException;
    void updateExpense(Expense expense) throws InvalidInputException, DatabaseException;
    void deleteExpense(int id) throws DatabaseException;
    List<Expense> getExpensesByUserId(int userId) throws DatabaseException;
}