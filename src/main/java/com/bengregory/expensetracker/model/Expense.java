package com.bengregory.expensetracker.model;

import com.bengregory.expensetracker.util.InvalidInputException;

import java.time.LocalDateTime;

public class Expense {
    private int id;
    private int userId;
    private double amount;
    private ExpenseCategory category;
    private LocalDateTime dateTime;
    private String description;

    public Expense(int id, int userId, double amount, ExpenseCategory category, LocalDateTime dateTime, String description) throws InvalidInputException {
        setId(id);
        setUserId(userId);
        setAmount(amount);
        setCategory(category);
        setDateTime(dateTime);
        setDescription(description);
    }

    public int getId() { return id; }
    public void setId(int id) throws InvalidInputException {
        if (id < 0) throw new InvalidInputException("Expense ID cannot be negative");
        this.id = id;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) throws InvalidInputException {
        if (userId <= 0) throw new InvalidInputException("User ID must be positive");
        this.userId = userId;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) throws InvalidInputException {
        if (amount <= 0) throw new InvalidInputException("Amount must be positive");
        this.amount = amount;
    }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) throws InvalidInputException {
        if (category == null) throw new InvalidInputException("Category cannot be null");
        this.category = category;
    }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) throws InvalidInputException {
        if (dateTime == null) throw new InvalidInputException("Date and time cannot be null");
        this.dateTime = dateTime;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }
}