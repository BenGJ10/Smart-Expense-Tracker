package com.bengregory.expensetracker.model;

// Enums for Expense
public enum ExpenseCategory {
    FOOD("Food"),
    EDUCATIONAL("Educational"),
    TRANSPORT("Transport"),
    ENTERTAINMENT("Entertainment"),
    UTILITIES("Utilities"),
    SHOPPING("Shopping"),
    OTHER("Other");

    private final String displayName; // This stores the readable version of the enum constant.

    // Constructor for the enum. It runs for each enum constant when the enum is loaded.
    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}