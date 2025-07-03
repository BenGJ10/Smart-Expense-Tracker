package com.bengregory.expensetracker.dao;

import com.bengregory.expensetracker.model.Income;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import java.util.List;

public interface IIncomeDAO {
    void addIncome(Income income) throws InvalidInputException, DatabaseException;
    void updateIncome(Income income) throws InvalidInputException, DatabaseException;
    void deleteIncome(int id) throws DatabaseException;
    List<Income> getIncomeByUserId(int userId) throws DatabaseException;
}