package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.model.Income;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;

import java.util.List;

public interface IIncomeService {
    void addIncome(Income income) throws InvalidInputException, DatabaseException;
    List<Income> getIncomeByUser() throws DatabaseException;
    void updateIncome(Income income) throws InvalidInputException, DatabaseException;
    void deleteIncome(int incomeId) throws DatabaseException;
}