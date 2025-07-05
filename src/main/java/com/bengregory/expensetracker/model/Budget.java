package com.bengregory.expensetracker.model;

import com.bengregory.expensetracker.util.InvalidInputException;
import java.time.LocalDate;

/*
    Model classes encapsulate data with private fields and public getters/setters.
    These act as Data Transfer Objects (DTOs) between layers (e.g., DAO layer to controller layer).

 */

public class Budget {
    private int id;
    private int userId;
    private ExpenseCategory category; // Nullable for overall budget
    private double amount;
    private String period; // WEEKLY or MONTHLY
    private LocalDate startDate;

    public Budget(int id, int userId, ExpenseCategory category, double amount, String period, LocalDate startDate) throws InvalidInputException {
        setId(id);
        setUserId(userId);
        setCategory(category);
        setAmount(amount);
        setPeriod(period);
        setStartDate(startDate);
    }

    public int getId() { return id; }
    public void setId(int id) throws InvalidInputException {
        if (id < 0) throw new InvalidInputException("Budget ID cannot be negative");
        this.id = id;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) throws InvalidInputException {
        if (userId <= 0) throw new InvalidInputException("User ID must be positive");
        this.userId = userId;
    }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) throws InvalidInputException {
        if (amount <= 0) throw new InvalidInputException("Budget amount must be positive");
        this.amount = amount;
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) throws InvalidInputException {
        if (period == null || (!period.equals("WEEKLY") && !period.equals("MONTHLY"))) {
            throw new InvalidInputException("Period must be WEEKLY or MONTHLY");
        }
        this.period = period;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) throws InvalidInputException {
        if (startDate == null) throw new InvalidInputException("Start date cannot be null");
        this.startDate = startDate;
    }
}